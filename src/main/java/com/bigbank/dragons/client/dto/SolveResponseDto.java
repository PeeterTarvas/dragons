package com.bigbank.dragons.client.dto;

public record SolveResponseDto(
    boolean success, int lives, int gold, int score, int highScore, int turn, String message) {}
