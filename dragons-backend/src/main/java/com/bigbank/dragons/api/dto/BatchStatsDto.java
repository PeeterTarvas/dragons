package com.bigbank.dragons.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Aggregate statistics over a batch of automatic games")
public record BatchStatsDto(
    @Schema(description = "Number of games played in the batch", example = "50") Integer games,
    @Schema(description = "Average final score across the batch", example = "1180.4")
        Double averageScore,
    @Schema(description = "Highest final score in the batch", example = "2100.0") Double maxScore,
    @Schema(description = "Lowest final score in the batch", example = "300.0") Double minScore,
    @Schema(description = "Number of games that reached the 1000-point target", example = "41")
        Long gamesReachedTarget,
    @Schema(description = "Percentage of games that reached the target", example = "82.0")
        Double reachedTargetPercent) {}
