package com.bigbank.dragons.api.dto;

public record BuyResponseDto(
    Boolean shoppingSuccess, Integer gold, Integer lives, Integer level, Integer turn) {}
