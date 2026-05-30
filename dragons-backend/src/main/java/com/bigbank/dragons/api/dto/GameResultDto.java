package com.bigbank.dragons.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Final/current game state together with the full per-turn log")
public record GameResultDto(
    @Schema(description = "Current game state") GameStateDto gameStateDto,
    @Schema(description = "Chronological log of every turn played") List<TurnLogDto> log) {}
