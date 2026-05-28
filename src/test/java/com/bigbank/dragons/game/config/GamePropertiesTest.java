package com.bigbank.dragons.game.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class GamePropertiesTest {

  @Test
  void constructorAppliesDefaultsWhenInvalidValuesProvided() {
    GameProperties props = new GameProperties(0.0, 0, 0, 0, "", 0.0, 0, 0, 0);

    assertEquals(1000, props.targetScore());
    assertEquals(1000, props.maxTurns());
    assertEquals(4, props.threadPoolSize());
    assertEquals(50, props.batchSize());
    assertEquals("expected-value", props.strategy());
    assertEquals(0.2, props.probabilityFloor());
    assertEquals(2, props.lowLivesThreshold());
    assertEquals(50, props.healingPotionMaxCost());
    assertEquals(200, props.goldReserve());
  }

  @Test
  void constructorKeepsAllProvidedValidValues() {
    GameProperties p = new GameProperties(500.0, 200, 8, 10, "low-risk", 0.3, 3, 40, 150);
    assertEquals(500.0, p.targetScore());
    assertEquals(200, p.maxTurns());
    assertEquals(8, p.threadPoolSize());
    assertEquals(10, p.batchSize());
    assertEquals("low-risk", p.strategy());
    assertEquals(0.3, p.probabilityFloor());
    assertEquals(3, p.lowLivesThreshold());
    assertEquals(40, p.healingPotionMaxCost());
    assertEquals(150, p.goldReserve());
  }

  @Test
  void constructorDefaultsGoldReserveWhenNull() {
    GameProperties p = new GameProperties(1000.0, 100, 4, 50, "expected-value", 0.2, 2, 50, null);
    assertEquals(200, p.goldReserve());
  }
}
