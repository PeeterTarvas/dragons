package com.bigbank.dragons.api.dto;

import java.util.List;

public record GameResultDto(
    String gameId,
    int finalScore,
    int finalGold,
    int turns,
    boolean reachedTarget,
    List<TurnLogDto> log) {}
