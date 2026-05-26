package com.bigbank.dragons.game.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "game")
public record GameProperties(int targetScore, int maxTurns, int threadPoolSize, int batchSize) {

  public GameProperties {
    if (targetScore <= 0) targetScore = 1000;
    if (maxTurns <= 0) maxTurns = 1000;
    if (threadPoolSize <= 0) threadPoolSize = 4;
    if (batchSize <= 0) batchSize = 50;
  }
}
