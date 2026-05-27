package com.bigbank.dragons.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

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

  private StrategyRegistry registry;

  @BeforeEach
  void setUp() {
    when(expectedValueStrategy.type()).thenReturn(StrategyType.EXPECTED_VALUE);
    when(lowRiskStrategy.type()).thenReturn(StrategyType.LOW_RISK);

    registry = new StrategyRegistry(List.of(expectedValueStrategy, lowRiskStrategy));
  }

  @Test
  void resolveReturnsExactMatch() {
    GameStrategy resolved = registry.resolve(StrategyType.LOW_RISK);
    assertEquals(lowRiskStrategy, resolved);
  }

  @Test
  void resolveReturnsDefaultExpectedValueWhenNullPassed() {
    GameStrategy resolved = registry.resolve(null);
    assertEquals(expectedValueStrategy, resolved, "Should fallback to EXPECTED_VALUE");
  }

  @Test
  void availableReturnsAllRegisteredTypes() {
    List<StrategyType> available = registry.available();
    assertEquals(2, available.size());
    assertTrue(available.contains(StrategyType.EXPECTED_VALUE));
    assertTrue(available.contains(StrategyType.LOW_RISK));
  }
}
