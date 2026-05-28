package com.bigbank.dragons.service;

import com.bigbank.dragons.domain.BatchStats;
import java.util.Collection;

public interface StatisticsService {

  /**
   * Compute aggregate statistics from a completed set of game scores. Pure computation — no state.
   */
  BatchStats snapshot(Collection<Double> scores);
}
