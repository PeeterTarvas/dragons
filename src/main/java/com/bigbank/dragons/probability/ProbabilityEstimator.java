package com.bigbank.dragons.probability;

import com.bigbank.dragons.domain.Message;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public class ProbabilityEstimator {

  private static final double PRIOR_WEIGHT = 3.0;

  private final Map<Probability, int[]> stats = new EnumMap<>(Probability.class);

  public double estimate(Message message) {
    String text = message.message().toLowerCase(Locale.ROOT);
    if (text.contains("steal") && text.contains("diamond")) { // TODO: make this better
      return 0.0;
    }
    return estimate(message.probability());
  }

  private double estimate(String label) {
    return estimate(Probability.fromLabel(label));
  }

  public void record(String label, boolean success) {
    record(Probability.fromLabel(label), success);
  }

  private double estimate(Probability probability) {
    int[] s = stats.get(probability);
    if (s == null) {
      return probability.prior();
    }
    double successes = s[0] + probability.prior() * PRIOR_WEIGHT;
    double attempts = s[1] + PRIOR_WEIGHT;
    return successes / attempts;
  }

  private void record(Probability probability, boolean success) {
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
