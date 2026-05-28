package com.bigbank.dragons.client.dto;

public record SolveResponseDto(
    Boolean success, Integer lives, Integer gold, Double score, Double highScore, Integer turn, String message) {}
