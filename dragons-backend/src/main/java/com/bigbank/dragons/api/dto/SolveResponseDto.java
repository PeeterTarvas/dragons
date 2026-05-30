package com.bigbank.dragons.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of attempting to solve a message")
public record SolveResponseDto(
    @Schema(description = "Whether the attempt was successful", example = "true") Boolean success,
    @Schema(description = "Lives left after the attempt", example = "3") Integer lives,
    @Schema(description = "Gold after the attempt", example = "70") Integer gold,
    @Schema(description = "Score after the attempt", example = "1200") Integer score,
    @Schema(description = "The current highest score", example = "1500") Integer highScore,
    @Schema(description = "Current turn number", example = "38") Integer turn,
    @Schema(
            description = "Text explanation of what happened on the last turn",
            example = "You successfully solved the task")
        String message) {}
