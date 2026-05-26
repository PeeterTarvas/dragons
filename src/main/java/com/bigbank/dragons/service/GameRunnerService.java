package com.bigbank.dragons.service;


import com.bigbank.dragons.game.state.GameState;

public interface GameRunnerService {

  /** Start and play a full game to completion. */
  GameState playGame();

}
