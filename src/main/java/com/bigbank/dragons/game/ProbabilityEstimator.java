package com.bigbank.dragons.game;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProbabilityEstimator {

  private static final Map<String, Double> PRIORS =
      Map.ofEntries(
          Map.entry("Piece of cake", 0.90),
          Map.entry("Sure thing", 0.92),
          Map.entry("Walk in the park", 0.85),
          Map.entry("Quite likely", 0.70),
          Map.entry("Hmmm....", 0.50),
          Map.entry("Gamble", 0.45),
          Map.entry("Risky", 0.35),
          Map.entry("Rather detrimental", 0.30),
          Map.entry("Playing with fire", 0.20),
          Map.entry("Suicide mission", 0.12),
          Map.entry("Impossible", 0.08));

  private static final double DEFAULT_PRIOR = 0.30;
  private static final double PRIOR_WEIGHT =
      3.0; // how many "virtual" observations the prior is worth

  private final Map<String, int[]> stats =
      new ConcurrentHashMap<>(); // label -> {successes, attempts}

  /** Success probability for a label, prior smoothed toward observed outcomes. */
  public double estimate(String label) {
    double prior = PRIORS.getOrDefault(label, DEFAULT_PRIOR);
    int[] s = stats.get(label);
    if (s == null) return prior;
    double successes = s[0] + prior * PRIOR_WEIGHT;
    double attempts = s[1] + PRIOR_WEIGHT;
    return successes / attempts;
  }

  /** Record an outcome to refine future estimates. */
  public void record(String label, boolean success) {
    stats.compute(
        label,
        (k, v) -> {
          int[] arr = (v == null) ? new int[2] : v;
          if (success) arr[0]++;
          arr[1]++;
          return arr;
        });
  }
}
