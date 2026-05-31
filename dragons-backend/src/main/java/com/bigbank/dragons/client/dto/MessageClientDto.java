package com.bigbank.dragons.client.dto;

public record MessageClientDto(
    String adId,
    String message,
    int reward,
    int expiresIn,
    Integer encrypted,
    String probability) {}
