package com.bigbank.dragons.domain;

public record BatchStats(
    Integer games,
    Double averageScore,
    Double maxScore,
    Double minScore,
    Long gamesReachedTarget,
    Double reachedTargetPercent) {}
