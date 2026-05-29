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
    "Piece of cake,    PIECE_OF_CAKE,    0.90",
    "Walk in the park, WALK_IN_THE_PARK, 0.85",
    "Quite likely,     QUITE_LIKELY,     0.70",
    "Gamble,           GAMBLE,           0.45",
    "Risky,            RISKY,            0.35",
    "Rather detrimental, RATHER_DETRIMENTAL, 0.30",
    "Playing with fire, PLAYING_WITH_FIRE, 0.20",
    "Suicide mission,  SUICIDE_MISSION,  0.12",
  })
  void fromLabelAndPriorCorrectForAllRemainingValues(
      String label, Probability expected, double expectedPrior) {
    Probability p = Probability.fromLabel(label);
    assertEquals(expected, p);
    assertEquals(expectedPrior, p.prior(), 0.001);
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

  @Test
  void labelAccessorReturnsConfiguredLabel() {
    assertEquals("Sure thing", Probability.SURE_THING.label());
    assertEquals("Impossible", Probability.IMPOSSIBLE.label());
  }
}
