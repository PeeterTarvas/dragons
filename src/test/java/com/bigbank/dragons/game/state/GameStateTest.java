package com.bigbank.dragons.game.state;

import static org.junit.jupiter.api.Assertions.*;

import com.bigbank.dragons.domain.BuyResponse;
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
}
