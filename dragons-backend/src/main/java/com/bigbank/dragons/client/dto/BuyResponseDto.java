package com.bigbank.dragons.client.dto;

public record BuyResponseDto(boolean shoppingSuccess, int gold, int lives, int level, int turn) {}
