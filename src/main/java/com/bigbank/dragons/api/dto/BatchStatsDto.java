package com.bigbank.dragons.api.dto;

public record BatchStatsDto(
    int games,
    double averageScore,
    int maxScore,
    int minScore,
    long gamesReachedTarget,
    double reachedTargetPercent) {}
