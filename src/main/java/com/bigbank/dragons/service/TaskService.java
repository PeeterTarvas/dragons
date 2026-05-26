package com.bigbank.dragons.service;

import com.bigbank.dragons.client.dto.MessageDto;
import com.bigbank.dragons.client.dto.SolveResponseDto;
import com.bigbank.dragons.game.ProbabilityEstimator;
import com.bigbank.dragons.game.state.GameState;

import java.util.List;
import java.util.Optional;

public interface TaskService {

    /** Fetch and decode the current messageboard. */
    List<MessageDto> getTasks(String gameId);

    /** Pick the best task to attempt, or empty if none are worth it. */
    Optional<MessageDto> chooseTask(List<MessageDto> ads, GameState state, ProbabilityEstimator estimator);

    /** Attempt to solve a task. */
    SolveResponseDto solve(GameState state, MessageDto ad);
}
