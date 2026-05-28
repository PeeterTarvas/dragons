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
    assertEquals(200, props.healingPotionMaxCost());
    assertEquals(0, props.goldReserve());
  }

  @Test
  void constructorKeepsProvidedValuesWhenValid() {
    GameProperties props = new GameProperties(500.0, 100, 2, 10, "low-risk", 0.5, 1, 100, 300);

    assertEquals(500, props.targetScore());
    assertEquals("low-risk", props.strategy());
    assertEquals(300, props.goldReserve());
  }
}
