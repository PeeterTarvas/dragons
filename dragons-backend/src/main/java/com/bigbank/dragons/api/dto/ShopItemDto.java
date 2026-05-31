package com.bigbank.dragons.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "An item available in the shop")
public record ShopItemDto(
    @NotBlank @Schema(description = "Item unique identifier", example = "hpot") String id,
    @NotBlank @Schema(description = "Item name", example = "Healing potion") String name,
    @PositiveOrZero @Schema(description = "Item cost in gold", example = "50") Integer cost) {}
