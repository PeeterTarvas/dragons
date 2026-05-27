package com.bigbank.dragons.api.dto;

public record BatchStatsDto(
    int games,
    double averageScore,
    double maxScore,
    double minScore,
    long gamesReachedTarget,
    double reachedTargetPercent) {}
