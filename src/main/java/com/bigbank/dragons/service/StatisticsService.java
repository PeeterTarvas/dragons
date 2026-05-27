package com.bigbank.dragons.service;

import com.bigbank.dragons.domain.BatchStats;

public interface StatisticsService {
  void addGameScore(double score);

  BatchStats snapshot();

  void reset();
}
