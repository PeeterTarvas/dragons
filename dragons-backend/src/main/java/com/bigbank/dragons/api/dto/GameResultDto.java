package com.bigbank.dragons.api.dto;

import java.util.List;

public record GameResultDto(GameStateDto gameStateDto, List<TurnLogDto> log) {}
