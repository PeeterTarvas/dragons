package com.bigbank.dragons.api.dto;

public record GameStateDto(
    String gameId,
    Integer lives,
    Integer gold,
    Integer level,
    Double score,
    Integer turn,
    Boolean reachedGoal) {}
