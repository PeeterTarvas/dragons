package com.bigbank.dragons.api.dto;

public record SolveResponseDto(
    boolean success, int lives, int gold, int score, int highScore, int turn, String message) {}
