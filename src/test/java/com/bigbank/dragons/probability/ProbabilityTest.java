package com.bigbank.dragons.probability;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class ProbabilityTest {

  @ParameterizedTest
  @CsvSource({
    "Sure thing, SURE_THING",
    "sure thing, SURE_THING",
    "Hmmm...., HMMM",
    "Rather detrimental, RATHER_DETRIMENTAL"
  })
  void fromLabelReturnsCorrectEnumForValidLabels(String label, Probability expected) {
    assertEquals(expected, Probability.fromLabel(label));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"  ", "Totally made up label", "null"})
  void fromLabelReturnsUnknownForInvalidOrBlankLabels(String invalidLabel) {
    assertEquals(Probability.UNKNOWN, Probability.fromLabel(invalidLabel));
  }

  @Test
  void enumsHaveExpectedPriors() {
    assertEquals(0.92, Probability.SURE_THING.prior(), 0.001);
    assertEquals(0.50, Probability.HMMM.prior(), 0.001);
    assertEquals(0.08, Probability.IMPOSSIBLE.prior(), 0.001);
  }
}
