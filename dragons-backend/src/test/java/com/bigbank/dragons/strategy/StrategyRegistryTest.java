package com.bigbank.dragons.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.bigbank.dragons.game.config.GameProperties;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StrategyRegistryTest {

  @Mock private GameStrategy expectedValueStrategy;
  @Mock private GameStrategy lowRiskStrategy;
  @Mock private GameProperties properties;

  private StrategyRegistry registry;

  @BeforeEach
  void setUp() {
    when(expectedValueStrategy.type()).thenReturn(StrategyType.EXPECTED_VALUE);
    when(lowRiskStrategy.type()).thenReturn(StrategyType.LOW_RISK);
    when(properties.strategy()).thenReturn("expected-value");

    registry = new StrategyRegistry(List.of(expectedValueStrategy, lowRiskStrategy), properties);
  }

  @Test
  void resolveReturnsExactMatch() {
    GameStrategy resolved = registry.resolve(StrategyType.LOW_RISK);
    assertEquals(lowRiskStrategy, resolved);
  }

  @Test
  void resolveReturnsConfiguredDefaultWhenNullPassed() {
    GameStrategy resolved = registry.resolve((StrategyType) null);
    assertEquals(
        expectedValueStrategy, resolved, "Should fallback to EXPECTED_VALUE based on config");
  }

  @Test
  void availableReturnsAllRegisteredTypes() {
    List<StrategyType> available = registry.available();
    assertEquals(2, available.size());
    assertTrue(available.contains(StrategyType.EXPECTED_VALUE));
    assertTrue(available.contains(StrategyType.LOW_RISK));
  }

  @Test
  void resolveStringReturnsExactMatchForValidKey() {
    assertEquals(StrategyType.LOW_RISK, registry.resolve("low-risk"));
  }

  @Test
  void resolveStringFallsBackToConfiguredStrategyForUnknownKey() {
    assertEquals(StrategyType.EXPECTED_VALUE, registry.resolve("nonsense"));
  }

  @Test
  void resolveStringFallsBackToExpectedValueWhenConfiguredAlsoInvalid() {
    when(properties.strategy()).thenReturn("also-bogus");
    assertEquals(StrategyType.EXPECTED_VALUE, registry.resolve("bogus"));
  }
}
