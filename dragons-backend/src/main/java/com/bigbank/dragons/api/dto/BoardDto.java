package com.bigbank.dragons.api.dto;

import java.util.List;

public record BoardDto(List<AdDto> ads, String recommendedAdId) {}
