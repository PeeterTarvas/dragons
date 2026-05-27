package com.bigbank.dragons.probability;

import java.util.Arrays;

public enum Probability {
  SURE_THING("Sure thing", 0.92),
  PIECE_OF_CAKE("Piece of cake", 0.90),
  WALK_IN_THE_PARK("Walk in the park", 0.85),
  QUITE_LIKELY("Quite likely", 0.70),
  HMMM("Hmmm....", 0.50),
  GAMBLE("Gamble", 0.45),
  RISKY("Risky", 0.35),
  RATHER_DETRIMENTAL("Rather detrimental", 0.30),
  PLAYING_WITH_FIRE("Playing with fire", 0.20),
  SUICIDE_MISSION("Suicide mission", 0.12),
  IMPOSSIBLE("Impossible", 0.08),
  UNKNOWN("", 0.30);

  private final String label;
  private final double prior;

  Probability(String label, double prior) {
    this.label = label;
    this.prior = prior;
  }

  public String label() {
    return label;
  }

  public double prior() {
    return prior;
  }

  public static Probability fromLabel(String label) {
    if (label == null || label.isBlank()) {
      return UNKNOWN;
    }
    return Arrays.stream(values())
        .filter(p -> p.label.equalsIgnoreCase(label))
        .findFirst()
        .orElse(UNKNOWN);
  }
}
