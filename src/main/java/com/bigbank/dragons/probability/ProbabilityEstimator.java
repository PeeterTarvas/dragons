package com.bigbank.dragons.probability;

import java.util.EnumMap;
import java.util.Map;

public class ProbabilityEstimator {

  private static final double PRIOR_WEIGHT =
      3.0; // how many "virtual" observations the prior is worth

  private final Map<Probability, int[]> stats =
      new EnumMap<>(Probability.class); // -> {successes, attempts}

  /** Convenience overload: resolve the raw API label, then estimate. */
  public double estimate(String label) {
    return estimate(Probability.fromLabel(label));
  }

  /** Success probability for a label, prior smoothed toward observed outcomes. */
  public double estimate(Probability probability) {
    int[] s = stats.get(probability);
    if (s == null) {
      return probability.prior();
    }
    double successes = s[0] + probability.prior() * PRIOR_WEIGHT;
    double attempts = s[1] + PRIOR_WEIGHT;
    return successes / attempts;
  }

  /** Convenience overload: resolve the raw API label, then record. */
  public void record(String label, boolean success) {
    record(Probability.fromLabel(label), success);
  }

  /** Record an outcome to refine future estimates. */
  public void record(Probability probability, boolean success) {
    stats.compute(
        probability,
        (k, v) -> {
          int[] arr = (v == null) ? new int[2] : v;
          if (success) arr[0]++;
          arr[1]++;
          return arr;
        });
  }
}
