package com.bigbank.dragons.domain;

public record BuyResponse(boolean shoppingSuccess, int gold, int lives, int level, int turn) {}
