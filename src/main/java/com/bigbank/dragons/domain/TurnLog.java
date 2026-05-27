package com.bigbank.dragons.domain;

public record TurnLog(
    int turn,
    String message,
    String probability,
    Boolean success,
    double score,
    int lives,
    int gold) {}
