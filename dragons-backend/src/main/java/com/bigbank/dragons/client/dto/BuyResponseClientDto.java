package com.bigbank.dragons.client.dto;

public record BuyResponseClientDto(
    Boolean shoppingSuccess, Integer gold, Integer lives, Integer level, Integer turn) {}
