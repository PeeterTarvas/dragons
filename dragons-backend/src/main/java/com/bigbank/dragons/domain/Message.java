package com.bigbank.dragons.domain;

public record Message(
    String adId,
    String message,
    Integer reward,
    Integer expiresIn,
    Integer encrypted,
    String probability) {}
