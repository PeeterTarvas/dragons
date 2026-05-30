package com.bigbank.dragons.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "A single message (ad / task) from the message board")
public record AdDto(
    @NotBlank @Schema(description = "Unique ID of the message", example = "a1b2c3") String adId,
    @NotBlank
        @Schema(description = "Free-text description of the task", example = "Help the villagers")
        String message,
    @Schema(description = "Gold rewarded for successfully solving the task", example = "20")
        Integer reward,
    @Schema(description = "Turns remaining before the message expires", example = "5")
        Integer expiresIn,
    @NotBlank @Schema(description = "Textual success likelihood", example = "Piece of cake")
        String probability,
    @Schema(
            description = "Encryption applied to the ad (0 = none, 1 = Base64, 2 = ROT13)",
            example = "0")
        Integer encrypted,
    @Schema(
            description = "Estimated success probability in [0,1] computed by the solver",
            example = "0.85")
        Double estimatedSuccess) {}
