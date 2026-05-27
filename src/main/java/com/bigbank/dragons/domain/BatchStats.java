package com.bigbank.dragons.domain;

public record BatchStats(
    int gamesPlayed,
    double averageScore,
    double maxScore,
    double minScore,
    long gamesReachedTarget,
    double successRate) {}
