package com.bigbank.dragons.service;

import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.domain.SolveResponse;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.probability.ProbabilityEstimator;
import com.bigbank.dragons.strategy.GameStrategy;
import java.util.List;

public interface TaskService {

  /** Fetch and decode the current messageboard. */
  List<Message> getTasks(String gameId);

  /** Pick the best task to attempt, or empty if none are worth it. */
  Message chooseTask(
      List<Message> ads, GameState state, ProbabilityEstimator estimator, GameStrategy strategy);

  /** Attempt to solve a task. */
  SolveResponse solve(GameState state, Message ad);
}
