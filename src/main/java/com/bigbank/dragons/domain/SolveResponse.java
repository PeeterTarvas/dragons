package com.bigbank.dragons.domain;

public record SolveResponse(
    Boolean success,
    Integer lives,
    Integer gold,
    Double score,
    Integer highScore,
    Integer turn,
    String message) {}
