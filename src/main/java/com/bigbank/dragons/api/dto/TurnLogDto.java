package com.bigbank.dragons.api.dto;

public record TurnLogDto(
    int turn,
    String action, // "SOLVE" | "BUY" | "SHOP_LISTED" ...
    String detail, // e.g. the ad message or item name
    String probability, // ad risk label, null for non-solve actions
    boolean success,
    int scoreAfter,
    int livesAfter,
    int goldAfter) {}
