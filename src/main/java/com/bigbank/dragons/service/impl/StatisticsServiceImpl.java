package com.bigbank.dragons.service.impl;

import com.bigbank.dragons.domain.BatchStats;
import com.bigbank.dragons.game.config.GameProperties;
import com.bigbank.dragons.service.StatisticsService;
import java.util.Collection;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

  private final GameProperties props;

  @Override
  public BatchStats snapshot(Collection<Double> scores) {
    List<Double> snap = List.copyOf(scores);
    if (snap.isEmpty()) {
      return new BatchStats(0, 0.0, 0.0, 0.0, 0L, 0.0);
    }
    DoubleSummaryStatistics stats =
        snap.stream().mapToDouble(Double::doubleValue).summaryStatistics();
    long reached = snap.stream().filter(s -> s >= props.targetScore()).count();
    return new BatchStats(
        snap.size(),
        stats.getAverage(),
        stats.getMax(),
        stats.getMin(),
        reached,
        reached * 100.0 / snap.size());
  }
}
