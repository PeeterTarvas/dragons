package com.bigbank.dragons.api.dto;

public record SolveResponseDto(
    Boolean success,
    Integer lives,
    Integer gold,
    Integer score,
    Integer highScore,
    Integer turn,
    String message) {}
