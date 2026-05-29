package com.bigbank.dragons.domain;

public record TurnLog(
    Integer turn,
    String message,
    String probability,
    Boolean success,
    Double score,
    Integer lives,
    Integer gold) {}
