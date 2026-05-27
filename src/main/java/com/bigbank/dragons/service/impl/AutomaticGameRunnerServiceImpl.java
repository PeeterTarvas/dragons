package com.bigbank.dragons.service.impl;

import com.bigbank.dragons.domain.BatchStats;
import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.game.config.GameProperties;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.game.turn.TurnExecutor;
import com.bigbank.dragons.probability.ProbabilityEstimator;
import com.bigbank.dragons.service.*;
import com.bigbank.dragons.strategy.GameStrategy;
import com.bigbank.dragons.strategy.StrategyRegistry;
import com.bigbank.dragons.strategy.StrategyType;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
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
public class AutomaticGameRunnerServiceImpl implements AutomaticGameRunnerService {

  private final GameService gameService;
  private final TaskService taskService;
  private final ShopService shopService;
  private final StatisticsService statisticsService;
  private final GameProperties props;
  private final StrategyRegistry strategyRegistry;
  private final TurnExecutor turnExecutor;

  @Override
  public GameState playGame(StrategyType strategyType) {
    GameStrategy strategy = strategyRegistry.resolve(strategyType);
    GameState state = gameService.start();
    ProbabilityEstimator estimator = new ProbabilityEstimator();
    log.info(
        "Started game {} (lives={}, gold={})",
        state.getGameId(),
        state.getLives(),
        state.getGold());

    try {
      while (state.isAlive() && state.getTurn() < props.maxTurns()) {
        List<Message> ads = taskService.getTasks(state.getGameId());
        Message ad = taskService.chooseTask(ads, state, estimator, strategy);

        turnExecutor.execute(state, ad, estimator);

        if (state.isAlive()) {
          shopService.shop(state, strategy);
        }
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
  public BatchStats playBatch(@NotBlank int games, StrategyType strategyType) {
    statisticsService.reset();
    ExecutorService pool = Executors.newFixedThreadPool(props.threadPoolSize());
    try {
      List<Future<?>> futures = new ArrayList<>(games);
      for (int i = 0; i < games; i++) {
        futures.add(pool.submit(() -> playGameSafely(strategyType)));
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
    BatchStats stats = statisticsService.snapshot();
    log.info("Batch finished: {}", stats);
    return stats;
  }

  /** Wrapper so one failed game doesn't kill the batch; failures are simply not recorded. */
  private void playGameSafely(StrategyType strategyType) {
    try {
      playGame(strategyType);
    } catch (Exception e) {
      log.warn("Game run failed: {}", e.getMessage());
    }
  }
}
