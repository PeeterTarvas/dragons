package com.bigbank.dragons.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StrategyTypeTest {

  @ParameterizedTest
  @CsvSource({
    "expected-value, EXPECTED_VALUE",
    "EXPECTED-VALUE, EXPECTED_VALUE",
    "low-risk, LOW_RISK"
  })
  void fromKeyReturnsCorrectStrategy(String key, StrategyType expected) {
    Optional<StrategyType> result = StrategyType.fromKey(key);
    assertTrue(result.isPresent());
    assertEquals(expected, result.get());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"invalid-strategy", "high-risk"})
  void fromKeyReturnsEmpty_ForInvalidOrNullKeys(String invalidKey) {
    Optional<StrategyType> result = StrategyType.fromKey(invalidKey);
    assertTrue(result.isEmpty());
  }
}
