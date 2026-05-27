package com.bigbank.dragons.domain;

public record Message(
    String adId,
    String message,
    int reward,
    int expiresIn,
    Integer encrypted,
    String probability) {}
