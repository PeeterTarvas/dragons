package com.bigbank.dragons.game.turn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.domain.SolveResponse;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.probability.Probability;
import com.bigbank.dragons.probability.ProbabilityEstimator;
import com.bigbank.dragons.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TurnExecutorTest {

  @Mock private TaskService taskService;
  @Mock private ProbabilityEstimator estimator;

  @InjectMocks private TurnExecutor turnExecutor;

  @Test
  void executeUpdatesStateAndEstimatorAndAddsLog() {
    GameState state = mock(GameState.class);
    Message ad = new Message("ad-1", "Solve this", 100, 5, null, Probability.SURE_THING.label());
    SolveResponse solveResponse =
        new SolveResponse(true, 3, 100, 50, 1, 1, "You successfully solved the mission!");

    when(taskService.solve(state, ad)).thenReturn(solveResponse);

    SolveResponse result = turnExecutor.execute(state, ad, estimator);

    assertEquals(solveResponse, result);
    verify(taskService).solve(state, ad);
    verify(state).update(3, 100, 50, 1);
    verify(estimator).record(Probability.SURE_THING.label(), true);
    verify(state).addLog(any());
  }
}
