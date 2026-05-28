package com.bigbank.dragons.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ShopItemDto(@NotBlank String id, @NotBlank String name, Integer cost) {}
