package com.bigbank.dragons.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.bigbank.dragons.domain.BatchStats;
import com.bigbank.dragons.game.config.GameProperties;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StatisticsServiceImplTest {

  @Mock private GameProperties props;
  @InjectMocks private StatisticsServiceImpl statisticsService;

  @Test
  void snapshotWithEmptyScoresShouldReturnZeroedStats() {
    BatchStats stats = statisticsService.snapshot(Collections.emptyList());

    assertEquals(0, stats.games());
    assertEquals(0.0, stats.averageScore());
    assertEquals(0.0, stats.maxScore());
    assertEquals(0.0, stats.minScore());
    assertEquals(0, stats.gamesReachedTarget());
    assertEquals(0.0, stats.reachedTargetPercent());
  }

  @Test
  void snapshotWithValidScoresShouldCalculateCorrectly() {
    when(props.targetScore()).thenReturn(1000.0);
    Collection<Double> scores = List.of(500.0, 1000.0, 1500.0);

    BatchStats stats = statisticsService.snapshot(scores);

    assertEquals(3, stats.games());
    assertEquals(1000.0, stats.averageScore());
    assertEquals(1500.0, stats.maxScore());
    assertEquals(500.0, stats.minScore());
    assertEquals(2, stats.gamesReachedTarget());
    assertEquals(66.66666666666667, stats.reachedTargetPercent());
  }

  @Test
  void snapshotCountsScoreExactlyAtTargetAsReached() {
    BatchStats stats = statisticsService.snapshot(List.of(1000.0));
    assertEquals(1, stats.gamesReachedTarget());
    assertEquals(100.0, stats.reachedTargetPercent());
  }
}
