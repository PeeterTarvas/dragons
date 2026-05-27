package com.bigbank.dragons.api.dto;

public record TurnLogDto(
    int turn,
    String detail, // e.g. the ad message or item name
    String probability, // ad risk label, null for non-solve actions
    boolean success,
    double scoreAfter,
    int livesAfter,
    int goldAfter) {}
