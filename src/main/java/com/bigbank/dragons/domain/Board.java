package com.bigbank.dragons.domain;

import java.util.List;

public record Board(List<EvaluatedMessage> messages, String recommendedAdId) {}
