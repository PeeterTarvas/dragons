package com.bigbank.dragons.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Message board: the available ads plus the recommended one")
public record BoardDto(
    @Schema(description = "Ads currently available to solve, sorted by estimated success")
        List<AdDto> ads,
    @Schema(
            description = "ID of the ad the chosen strategy recommends, or empty if none",
            example = "a1b2c3")
        String recommendedAdId) {}
