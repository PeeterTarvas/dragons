package com.bigbank.dragons.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of attempting to purchase a shop item")
public record BuyResponseDto(
    @Schema(description = "Whether the purchase was successful", example = "true")
        Boolean shoppingSuccess,
    @Schema(description = "Gold left after the transaction", example = "20") Integer gold,
    @Schema(description = "Lives left after the transaction", example = "4") Integer lives,
    @Schema(description = "Dragon level after the transaction", example = "2") Integer level,
    @Schema(description = "Current turn (increments even if the purchase fails)", example = "39")
        Integer turn) {}
