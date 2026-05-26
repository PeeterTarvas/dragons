package com.bigbank.dragons.client.dto;

public record BuyResponse(boolean shoppingSuccess, int gold, int lives, int level, int turn) {}
