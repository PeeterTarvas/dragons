package com.bigbank.dragons.game.turn;

import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.domain.SolveResponse;
import com.bigbank.dragons.domain.TurnLog;
import com.bigbank.dragons.game.enums.Result;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.probability.ProbabilityEstimator;
import com.bigbank.dragons.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TurnExecutor {

  private final TaskService taskService;

  public SolveResponse execute(GameState state, Message ad, ProbabilityEstimator estimator) {
    SolveResponse result = taskService.solve(state, ad);
    state.update(result.lives(), result.gold(), result.score(), result.turn());
    estimator.record(ad.probability(), result.success());

    state.addLog(
        new TurnLog(
            result.turn(),
            ad.message(),
            ad.probability(),
            result.success(),
            result.score(),
            result.lives(),
            result.gold()));

    log.info(
        "Turn {}: '{}' [{}] -> {} (score={}, lives={})",
        result.turn(),
        ad.message(),
        ad.probability(),
        result.success() ? Result.WIN : Result.LOSS,
        result.score(),
        result.lives());

    return result;
  }
}
