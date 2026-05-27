package com.bigbank.dragons.api.dto;

public record TurnLogDto(
    int turn,
    String message,
    String probability,
    Boolean success,
    double score,
    int lives,
    int gold) {}
