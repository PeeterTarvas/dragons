package com.bigbank.dragons.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AdDto(
    @NotBlank String adId,
    @NotBlank String message,
    Integer reward,
    Integer expiresIn,
    @NotBlank String probability,
    Integer encrypted,
    Double estimatedSuccess) {}
