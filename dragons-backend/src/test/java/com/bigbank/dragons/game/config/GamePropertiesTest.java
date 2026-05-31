package com.bigbank.dragons.game.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class GamePropertiesTest {

  @Test
  void constructorAppliesDefaultsWhenInvalidValuesProvided() {
    GameProperties props = new GameProperties(0.0, 0, 0, 0, "", 0.0, 0, 0, 0, 0, -1);

    assertEquals(1000, props.targetScore());
    assertEquals(1000, props.maxTurns());
    assertEquals(4, props.threadPoolSize());
    assertEquals(50, props.batchSize());
    assertEquals("expected-value", props.defaultStrategy());
    assertEquals(0.2, props.probabilityFloor());
    assertEquals(2, props.lowLivesThreshold());
    assertEquals(50, props.healingPotionMaxCost());
    assertEquals(200, props.goldReserve());
    assertEquals(1, props.sessionTtlMinutes());
    assertEquals(1, props.extraLivesBuffer());
  }

  @Test
  void constructorKeepsAllProvidedValidValues() {
    GameProperties p = new GameProperties(500.0, 200, 8, 10, "low-risk", 0.3, 3, 40, 150, 15, 2);

    assertEquals(500.0, p.targetScore());
    assertEquals(200, p.maxTurns());
    assertEquals(8, p.threadPoolSize());
    assertEquals(10, p.batchSize());
    assertEquals("low-risk", p.defaultStrategy());
    assertEquals(0.3, p.probabilityFloor());
    assertEquals(3, p.lowLivesThreshold());
    assertEquals(40, p.healingPotionMaxCost());
    assertEquals(150, p.goldReserve());
    assertEquals(15, p.sessionTtlMinutes());
    assertEquals(2, p.extraLivesBuffer());
  }

  @Test
  void constructorDefaultsGoldReserveWhenNull() {
    GameProperties p =
        new GameProperties(1000.0, 100, 4, 50, "expected-value", 0.2, 2, 50, null, 30, 1);
    assertEquals(200, p.goldReserve());
  }

  @Test
  void constructorDefaultsExplicitNullValues() {
    GameProperties p =
        new GameProperties(null, null, null, null, null, null, null, null, null, null, null);

    assertEquals("expected-value", p.defaultStrategy());
    assertEquals(200, p.goldReserve());
    assertEquals(1, p.sessionTtlMinutes());
  }
}
