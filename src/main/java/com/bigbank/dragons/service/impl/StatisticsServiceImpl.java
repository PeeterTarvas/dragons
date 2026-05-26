package com.bigbank.dragons.service.impl;

import com.bigbank.dragons.api.dto.BatchStatsDto;
import com.bigbank.dragons.game.config.GameProperties;
import com.bigbank.dragons.service.StatisticsService;
import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

  private final GameProperties props;
  private final Queue<Integer> scores = new ConcurrentLinkedQueue<>();

  @Override
  public void addGameScore(int score) {
    scores.add(score);
  }

  @Override
  public void reset() {
    scores.clear();
  }

  @Override
  public BatchStatsDto snapshot() {
    List<Integer> snap = new ArrayList<>(scores);
    if (snap.isEmpty()) {
      return new BatchStatsDto(0, 0, 0, 0, 0, 0);
    }
    IntSummaryStatistics st = snap.stream().mapToInt(Integer::intValue).summaryStatistics();
    long reached = snap.stream().filter(s -> s >= props.targetScore()).count();
    return new BatchStatsDto(
        snap.size(),
        st.getAverage(),
        st.getMax(),
        st.getMin(),
        reached,
        reached * 100.0 / snap.size());
  }
}
