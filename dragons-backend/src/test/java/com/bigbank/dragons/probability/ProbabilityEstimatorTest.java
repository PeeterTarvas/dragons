package com.bigbank.dragons.probability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bigbank.dragons.domain.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class ProbabilityEstimatorTest {

  private ProbabilityEstimator estimator;

  @BeforeEach
  void setUp() {
    estimator = new ProbabilityEstimator();
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = ',',
      value = {
        "Steal super awesome diamond cat, 0.0",
        "STEAL a diamond ring, 0.0",
        "steal some diamonds, 0.0",
      })
  void estimateReturnsZeroForDiamondTraps(String messageText, double expectedProbability) {
    Message trapMessage =
        new Message("1", messageText, 1000, 10, null, Probability.SURE_THING.label());
    assertEquals(expectedProbability, estimator.estimate(trapMessage), 0.001);
  }

  /** HMMM has a prior of 0.50. Because we have no history, it should return exactly 0.50 */
  @ParameterizedTest
  @ValueSource(strings = {"Steal a cat", "Buy a diamond", "Escort someone safely"})
  void estimateReturnsNormalPriorWhenNotATrap(String messageText) {
    Message normalMessage = new Message("1", messageText, 100, 10, null, Probability.HMMM.label());

    assertEquals(0.50, estimator.estimate(normalMessage), 0.001);
  }

  /**
   * Math for HMMM: Prior = 0.5, Weight = 3.0 Successes = 0 + (0.5 * 3.0) = 1.5 Attempts = 0 + 3.0 =
   * 3.0 Base estimate = 1.5 / 3.0 = 0.5 Add 1 success: Successes = 1 + 1.5 = 2.5 Attempts = 1 + 3.0
   * = 4.0 New estimate = 2.5 / 4.0 = 0.625
   */
  @Test
  void estimateAdjustsUpwardWhenSuccessIsRecorded() {
    estimator.record(Probability.HMMM.label(), true);

    Message message = new Message("1", "Normal task", 100, 10, null, Probability.HMMM.label());
    assertEquals(0.625, estimator.estimate(message), 0.001);
  }

  /**
   * Math for HMMM: Add 1 failure: Successes = 0 + 1.5 = 1.5 Attempts = 1 + 3.0 = 4.0 New estimate =
   * 1.5 / 4.0 = 0.375
   */
  @Test
  void estimateAdjustsDownwardWhenFailureIsRecorded() {
    estimator.record(Probability.HMMM.label(), false);

    Message message = new Message("1", "Normal task", 100, 10, null, Probability.HMMM.label());
    assertEquals(0.375, estimator.estimate(message), 0.001);
  }

  /** UNKNOWN prior is 0.30. 1 success = (1 + 0.3 * 3.0) / (1 + 3.0) = 1.9 / 4.0 = 0.475 */
  @Test
  void recordHandlesUnknownLabelsSafely() {
    estimator.record("Some bizarre new risk label", true);

    Message message = new Message("1", "Task", 100, 10, null, Probability.UNKNOWN.label());

    assertEquals(0.475, estimator.estimate(message), 0.001);
  }

  @Test
  void recordAccumulatesSuccessesAndFailuresAcrossMultipleCalls() {
    estimator.record(Probability.HMMM.label(), true);
    estimator.record(Probability.HMMM.label(), false);
    Message message = new Message("1", "Something hmm", 100, 10, null, Probability.HMMM.label());

    double estimate = estimator.estimate(message);
    assertTrue(estimate > 0.0 && estimate < 1.0);
  }
}
