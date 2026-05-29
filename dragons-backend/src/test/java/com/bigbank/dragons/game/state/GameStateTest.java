package com.bigbank.dragons.game.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bigbank.dragons.domain.BuyResponse;
import com.bigbank.dragons.domain.TurnLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class GameStateTest {

  private GameState state;

  @BeforeEach
  void setUp() {
    state = new GameState("game-123", 3, 0, 1, 0.0, 0, false);
  }

  @ParameterizedTest
  @CsvSource({"3, true", "1, true", "0, false", "-1, false"})
  void isAliveEvaluatesCorrectlyBasedOnLives(int lives, boolean expectedAlive) {
    state.update(lives, 100, 50.0, 1);
    assertEquals(expectedAlive, state.isAlive());
  }

  @Test
  void updateAfterBuyAppliesShopResponseCorrectly() {
    BuyResponse response = new BuyResponse(true, 50, 4, 2, 5);
    state.updateAfterBuy(response);

    assertEquals(50, state.getGold());
    assertEquals(4, state.getLives());
    assertEquals(2, state.getLevel());
    assertEquals(5, state.getTurn());
  }

  @Test
  void markReachedGoalUpdatesState() {
    assertFalse(state.isReachedGoal());
    state.markReachedGoal(true);
    assertTrue(state.isReachedGoal());
  }

  @Test
  void addLogAppendsEntrySuccessfully() {
    TurnLog mockLog = new TurnLog(1, "Solved task", "Sure thing", true, 10.0, 3, 0);
    assertTrue(state.getLog().isEmpty());

    state.addLog(mockLog);

    assertEquals(1, state.getLog().size());
    assertEquals(mockLog, state.getLog().get(0));
  }

  @Test
  void updateStoresAllFields() {
    GameState state = new GameState("g1", 3, 100, 1, 0, 1, false);
    state.update(1, 250, 500.0, 7);
    assertEquals(1, state.getLives());
    assertEquals(250, state.getGold());
    assertEquals(500.0, state.getScore());
    assertEquals(7, state.getTurn());
  }

  @Test
  void markReachedGoalFalseStoresFalse() {
    GameState state = new GameState("g1", 3, 100, 1, 0, 1, true);
    state.markReachedGoal(false);
    assertFalse(state.isReachedGoal());
  }
}
