package com.bigbank.dragons.domain;

public record BuyResponse(
    Boolean shoppingSuccess, Integer gold, Integer lives, Integer level, Integer turn) {}
