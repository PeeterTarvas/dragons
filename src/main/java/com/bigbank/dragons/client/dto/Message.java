package com.bigbank.dragons.client.dto;

/**
 * One ad from the messageboard.
 *
 * <p>Note: the API docs claim {@code reward} is a String, but live responses return a JSON number.
 * Jackson coerces both forms into an int by default, so this field tolerates either. {@code
 * encrypted} is null for plain ads and a cipher code (0, 1, 2…) for encoded ones.
 */
public record Message(
    String adId,
    String message,
    int reward,
    int expiresIn,
    Integer encrypted,
    String probability) {}
