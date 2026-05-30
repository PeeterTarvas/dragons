package com.bigbank.dragons.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Snapshot of the current game state")
public record GameStateDto(
    @Schema(description = "Unique game ID for the started game", example = "mAg6juyHkfBp")
        String gameId,
    @Schema(description = "How many lives the player currently has", example = "3") Integer lives,
    @Schema(description = "Gold the player currently holds", example = "50") Integer gold,
    @Schema(description = "Current dragon level", example = "2") Integer level,
    @Schema(description = "Current game score", example = "1200.0") Double score,
    @Schema(description = "Current turn number", example = "37") Integer turn,
    @Schema(description = "Whether the 1000-point target has been reached", example = "true")
        Boolean reachedGoal) {}
