package com.bigbank.dragons.service.impl;

import com.bigbank.dragons.api.dto.BatchStatsDto;
import com.bigbank.dragons.api.dto.TurnLogDto;
import com.bigbank.dragons.client.dto.MessageDto;
import com.bigbank.dragons.client.dto.SolveResponseDto;
import com.bigbank.dragons.game.ProbabilityEstimator;
import com.bigbank.dragons.game.config.GameProperties;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.service.GameRunnerService;
import com.bigbank.dragons.service.GameService;
import com.bigbank.dragons.service.ShopService;
import com.bigbank.dragons.service.StatisticsService;
import com.bigbank.dragons.service.TaskService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameRunnerServiceImpl implements GameRunnerService {

  private final GameService gameService;
  private final TaskService taskService;
  private final ShopService shopService;
  private final StatisticsService statisticsService;
  private final GameProperties props;

  @Override
  public GameState playGame() {
    GameState state = gameService.start(); // may throw -> propagates, not counted
    ProbabilityEstimator estimator = new ProbabilityEstimator();
    log.info(
        "Started game {} (lives={}, gold={})",
        state.getGameId(),
        state.getLives(),
        state.getGold());

    try {
      while (state.isAlive() && state.getTurn() < props.maxTurns()) {

        shopService.shop(state);
        if (!state.isAlive()) break;

        List<MessageDto> ads = taskService.getTasks(state.getGameId());
        Optional<MessageDto> choice = taskService.chooseTask(ads, state, estimator);

        if (choice.isEmpty()) {
          log.info("Turn {}: no acceptable ad, trying an upgrade", state.getTurn());
          if (!shopService.shop(state)) {
            log.info("Turn {}: nothing to do, ending run", state.getTurn());
            break;
          }
          continue;
        }

        MessageDto ad = choice.get();
        SolveResponseDto result = taskService.solve(state, ad);
        state.update(result.lives(), result.gold(), result.score(), result.turn());
        estimator.record(ad.probability(), result.success());

        log.info(
            "Turn {}: '{}' [{}] -> {} (score={}, lives={})",
            result.turn(),
            ad.message(),
            ad.probability(),
            result.success() ? "WIN" : "LOSS",
            result.score(),
            result.lives());
        state.addLog(
            new TurnLogDto(
                result.turn(),
                "SOLVE",
                ad.message(),
                ad.probability(),
                result.success(),
                result.score(),
                result.lives(),
                result.gold()));
      }

      state.markReachedGoal(state.getScore() >= props.targetScore());
      log.info(
          "Game {} finished: score={}, turns={}, reached={}",
          state.getGameId(),
          state.getScore(),
          state.getTurn(),
          state.isReachedGoal());
      return state;
    } finally {
      statisticsService.addGameScore(state.getScore());
    }
  }

  @Override
  public BatchStatsDto playBatch(int games) {
    statisticsService.reset();
    ExecutorService pool = Executors.newFixedThreadPool(props.threadPoolSize());
    try {
      List<Future<?>> futures = new ArrayList<>(games);
      for (int i = 0; i < games; i++) {
        futures.add(pool.submit(this::playGameSafely));
      }
      for (Future<?> f : futures) {
        try {
          f.get();
        } catch (Exception e) {
          log.warn("A game failed and was skipped: {}", e.getMessage());
        }
      }
    } finally {
      pool.shutdown();
      try {
        if (!pool.awaitTermination(1, TimeUnit.HOURS)) {
          log.error("Batch did not finish within timeout");
          pool.shutdownNow();
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        pool.shutdownNow();
      }
    }
    BatchStatsDto stats = statisticsService.snapshot();
    log.info("Batch finished: {}", stats);
    return stats;
  }

  /** Wrapper so one failed game doesn't kill the batch; failures are simply not recorded. */
  private void playGameSafely() {
    try {
      playGame();
    } catch (Exception e) {
      log.warn("Game run failed: {}", e.getMessage());
    }
  }
}
