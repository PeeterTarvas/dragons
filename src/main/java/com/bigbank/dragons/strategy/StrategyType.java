package com.bigbank.dragons.strategy;

import java.util.Arrays;
import java.util.Optional;

public enum StrategyType {
  EXPECTED_VALUE("expected-value"),
  LOW_RISK("low-risk");

  private final String key;

  StrategyType(String key) {
    this.key = key;
  }

  public String key() {
    return key;
  }

  public static Optional<StrategyType> fromKey(String key) {
    return Arrays.stream(values()).filter(s -> s.key.equalsIgnoreCase(key)).findFirst();
  }
}
