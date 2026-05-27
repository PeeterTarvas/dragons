package com.bigbank.dragons.api.dto;

public record BuyResponseDto(boolean shoppingSuccess, int gold, int lives, int level, int turn) {}
