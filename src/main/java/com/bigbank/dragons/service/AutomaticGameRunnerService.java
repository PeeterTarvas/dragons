package com.bigbank.dragons.service;

import com.bigbank.dragons.domain.BatchStats;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.strategy.StrategyType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface AutomaticGameRunnerService {

  /** Play one full game to completion. */
  GameState playGame(StrategyType strategyType);

  /** Play N games concurrently and return aggregate statistics. */
  BatchStats playBatch(@NotNull @Positive Integer games, StrategyType strategyType);
}
