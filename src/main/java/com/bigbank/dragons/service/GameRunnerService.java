package com.bigbank.dragons.service;

import com.bigbank.dragons.api.dto.BatchStatsDto;
import com.bigbank.dragons.game.state.GameState;

public interface GameRunnerService {

  /** Play one full game to completion. */
  GameState playGame();

  /** Play N games concurrently and return aggregate statistics. */
  BatchStatsDto playBatch(int games);
}
