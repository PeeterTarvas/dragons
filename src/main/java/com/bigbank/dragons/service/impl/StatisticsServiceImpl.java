package com.bigbank.dragons.service.impl;

import com.bigbank.dragons.domain.BatchStats;
import com.bigbank.dragons.game.config.GameProperties;
import com.bigbank.dragons.service.StatisticsService;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

  private final GameProperties props;
  private final Queue<Double> scores = new ConcurrentLinkedQueue<>();

  @Override
  public void addGameScore(double score) {
    scores.add(score);
  }

  @Override
  public void reset() {
    scores.clear();
  }

  @Override
  public BatchStats snapshot() {
    List<Double> snap = new ArrayList<>(scores);
    if (snap.isEmpty()) {
      return new BatchStats(0, 0, 0, 0, 0, 0);
    }
    DoubleSummaryStatistics statistics =
        snap.stream().mapToDouble(Double::doubleValue).summaryStatistics();
    long reached = snap.stream().filter(s -> s >= props.targetScore()).count();
    return new BatchStats(
        snap.size(),
        statistics.getAverage(),
        statistics.getMax(),
        statistics.getMin(),
        reached,
        reached * 100.0 / snap.size());
  }
}
