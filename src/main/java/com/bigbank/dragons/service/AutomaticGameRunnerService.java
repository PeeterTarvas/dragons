package com.bigbank.dragons.service;

import com.bigbank.dragons.domain.BatchStats;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.strategy.StrategyType;

public interface AutomaticGameRunnerService {

  /** Play one full game to completion. */
  GameState playGame(StrategyType strategyType);

  /** Play N games concurrently and return aggregate statistics. */
  BatchStats playBatch(int games, StrategyType strategyType);
}
