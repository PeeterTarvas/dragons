package com.bigbank.dragons.api.dto;

public record AdDto(
    String adId,
    String message,
    int reward,
    int expiresIn,
    String probability,
    double estimatedSuccess) {}
