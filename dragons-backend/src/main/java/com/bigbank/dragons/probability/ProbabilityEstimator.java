package com.bigbank.dragons.probability;

import com.bigbank.dragons.domain.Message;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ProbabilityEstimator {

  private static final double PRIOR_WEIGHT = 3.0;
  private static final Set<String> BAD_WORDS = Set.of("steal", "diamond");

  private final Map<Probability, Observation> stats = new EnumMap<>(Probability.class);

  public double estimate(Message message) {
    String text = message.message().toLowerCase(Locale.ROOT);

    if (BAD_WORDS.stream().allMatch(text::contains)) {
      return 0.0;
    }

    return estimate(Probability.fromLabel(message.probability()));
  }

  public void record(String label, boolean success) {
    record(Probability.fromLabel(label), success);
  }

  private double estimate(Probability probability) {
    Observation obs = stats.get(probability);
    if (obs == null) {
      return probability.prior();
    }

    double successes = obs.successes() + (probability.prior() * PRIOR_WEIGHT);
    double attempts = obs.attempts() + PRIOR_WEIGHT;

    return successes / attempts;
  }

  private void record(Probability probability, boolean success) {
    stats.compute(
        probability,
        (key, currentObs) -> {
          if (currentObs == null) {
            return new Observation(success ? 1 : 0, 1);
          }
          return currentObs.addAttempt(success);
        });
  }

  /** An immutable record to track statistics */
  private record Observation(int successes, int attempts) {
    public Observation addAttempt(boolean success) {
      return new Observation(this.successes + (success ? 1 : 0), this.attempts + 1);
    }
  }
}
