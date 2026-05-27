package com.bigbank.dragons.game.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "game")
public record GameProperties(
    int targetScore,
    int maxTurns,
    int threadPoolSize,
    int batchSize,
    String strategy,
    double probabilityFloor,
    int lowLivesThreshold,
    int healingPotionMaxCost,
    int goldReserve) {

  public GameProperties {
    if (targetScore <= 0) targetScore = 1000;
    if (maxTurns <= 0) maxTurns = 1000;
    if (threadPoolSize <= 0) threadPoolSize = 4;
    if (batchSize <= 0) batchSize = 50;
    if (strategy == null || strategy.isBlank()) strategy = "expected-value";
    if (probabilityFloor <= 0) probabilityFloor = 0.2;
    if (lowLivesThreshold <= 0) lowLivesThreshold = 2;
    if (healingPotionMaxCost <= 0) healingPotionMaxCost = 50;
    if (goldReserve <= 0) healingPotionMaxCost = 200;
  }
}
