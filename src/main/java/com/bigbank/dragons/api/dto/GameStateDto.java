package com.bigbank.dragons.api.dto;

public record GameStateDto(
    String gameId, int lives, int gold, int level, double score, int turn, boolean reachedGoal) {}
