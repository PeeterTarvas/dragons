package com.bigbank.dragons.game.turn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
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
        new SolveResponse(true, 3, 100, 50.0, 50.0, 1, "You successfully solved the mission!");

    when(taskService.solve(state, ad)).thenReturn(solveResponse);

    SolveResponse result = turnExecutor.execute(state, ad, estimator);

    assertEquals(solveResponse, result);
    verify(taskService).solve(state, ad);
    verify(state).update(3, 100, 50, 1);
    verify(estimator).record(Probability.SURE_THING.label(), true);
    verify(state).addLog(any());
  }

  @Test
  void executeRecordsLossWhenSolveResponseIndicatesFailure() {
    GameState state = mock(GameState.class);
    Message ad = new Message("ad-1", "Solve this", 100, 5, null, Probability.SURE_THING.label());

    SolveResponse loss = new SolveResponse(false, 2, 80, 200.0, 200.0, 20, "You failed");
    when(taskService.solve(state, ad)).thenReturn(loss);

    turnExecutor.execute(state, ad, estimator);

    verify(state).update(eq(2), eq(80), eq(200.0), anyInt());
  }
}
