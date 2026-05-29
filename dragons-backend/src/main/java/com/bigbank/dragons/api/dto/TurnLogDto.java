package com.bigbank.dragons.api.dto;

public record TurnLogDto(
    Integer turn,
    String message,
    String probability,
    Boolean success,
    Double score,
    Integer lives,
    Integer gold) {}
