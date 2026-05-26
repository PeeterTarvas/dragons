package com.bigbank.dragons.service;

import com.bigbank.dragons.api.dto.BatchStatsDto;

public interface StatisticsService {
  void addGameScore(int score);

  BatchStatsDto snapshot();

  void reset();
}
