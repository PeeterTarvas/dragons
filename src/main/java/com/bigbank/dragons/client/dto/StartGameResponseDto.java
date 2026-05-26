package com.bigbank.dragons.client.dto;

public record StartGameResponseDto(
    String gameId, int lives, int gold, int level, int score, int highScore, int turn) {}
