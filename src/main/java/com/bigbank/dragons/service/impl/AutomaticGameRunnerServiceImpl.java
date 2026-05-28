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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class AutomaticGameRunnerServiceImpl implements AutomaticGameRunnerService {

  private final GameService gameService;
  private final TaskService taskService;
  private final ShopService shopService;
  private final StatisticsService statisticsService;
  private final GameProperties props;
  private final StrategyRegistry strategyRegistry;
  private final TurnExecutor turnExecutor;
  private final ExecutorService batchExecutorService;

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
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public BatchStats playBatch(Integer games, StrategyType strategyType) {
    ConcurrentLinkedQueue<Double> scores = new ConcurrentLinkedQueue<>();
    List<Future<GameState>> futures = new ArrayList<>(games);

    for (int i = 0; i < games; i++) {
      futures.add(batchExecutorService.submit(() -> playGameSafely(strategyType)));
    }

    for (Future<GameState> f : futures) {
      try {
        GameState gs = f.get();
        if (gs != null) {
          scores.add(gs.getScore());
        }
      } catch (Exception e) {
        log.warn("A game failed and was skipped: {}", e.getMessage());
      }
    }

    BatchStats stats = statisticsService.snapshot(scores);
    log.info("Batch finished: {}", stats);
    return stats;
  }

  /** Wrapper so one failed game doesn't abort the batch; null return signals a failed run. */
  private GameState playGameSafely(StrategyType strategyType) {
    try {
      return playGame(strategyType);
    } catch (Exception e) {
      log.warn("Game run failed: {}", e.getMessage());
      return null;
    }
  }
}
