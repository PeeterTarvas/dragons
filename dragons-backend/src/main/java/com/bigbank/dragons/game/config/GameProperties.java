package com.bigbank.dragons.game.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "game")
public record GameProperties(
    @DefaultValue("1000.0") Double targetScore,
    @DefaultValue("1000") Integer maxTurns,
    @DefaultValue("4") Integer threadPoolSize,
    @DefaultValue("50") Integer batchSize,
    @DefaultValue("expected-value") String strategy,
    @DefaultValue("0.2") Double probabilityFloor,
    @DefaultValue("2") Integer lowLivesThreshold,
    @DefaultValue("50") Integer healingPotionMaxCost,
    @DefaultValue("200") Integer goldReserve,
    @DefaultValue("30") Integer sessionTtlMinutes,
    @DefaultValue("1") Integer extraLivesBuffer) {

  public GameProperties {
    if (targetScore != null && targetScore <= 0) targetScore = 1000.0;
    if (maxTurns != null && maxTurns <= 0) maxTurns = 1000;
    if (threadPoolSize != null && threadPoolSize <= 0) threadPoolSize = 4;
    if (batchSize != null && batchSize <= 0) batchSize = 50;
    if (strategy == null || strategy.isBlank()) strategy = "expected-value";
    if (probabilityFloor != null && probabilityFloor <= 0) probabilityFloor = 0.2;
    if (lowLivesThreshold != null && lowLivesThreshold <= 0) lowLivesThreshold = 2;
    if (healingPotionMaxCost != null && healingPotionMaxCost <= 0) healingPotionMaxCost = 50;
    if (goldReserve == null || goldReserve <= 0) goldReserve = 200;
    if (sessionTtlMinutes == null || sessionTtlMinutes <= 0) sessionTtlMinutes = 1;
    if (extraLivesBuffer != null && extraLivesBuffer < 0) extraLivesBuffer = 1;
  }
}
