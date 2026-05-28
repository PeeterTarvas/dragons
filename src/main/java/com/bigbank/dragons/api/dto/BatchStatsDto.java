package com.bigbank.dragons.api.dto;

public record BatchStatsDto(
    Integer games,
    Double averageScore,
    Double maxScore,
    Double minScore,
    Long gamesReachedTarget,
    Double reachedTargetPercent) {}
