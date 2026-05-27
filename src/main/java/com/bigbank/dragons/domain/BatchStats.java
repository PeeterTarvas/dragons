package com.bigbank.dragons.domain;

public record BatchStats(
    int games,
    double averageScore,
    double maxScore,
    double minScore,
    long gamesReachedTarget,
    double reachedTargetPercent) {}
