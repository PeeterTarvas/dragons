package com.bigbank.dragons.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A single entry in the per-turn game log")
public record TurnLogDto(
    @Schema(description = "Turn number this entry refers to", example = "12") Integer turn,
    @Schema(description = "What happened on this turn", example = "SOLVE: Help the villagers")
        String message,
    @Schema(description = "Textual success likelihood of the action", example = "Sure thing")
        String probability,
    @Schema(description = "Whether the action succeeded", example = "true") Boolean success,
    @Schema(description = "Score after the turn", example = "640.0") Double score,
    @Schema(description = "Lives after the turn", example = "3") Integer lives,
    @Schema(description = "Gold after the turn", example = "85") Integer gold) {}
