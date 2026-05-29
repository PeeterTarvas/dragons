package com.bigbank.dragons.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bigbank.dragons.api.dto.BatchStatsDto;
import com.bigbank.dragons.api.dto.GameResultDto;
import com.bigbank.dragons.api.mapper.ApiMapper;
import com.bigbank.dragons.domain.BatchStats;
import com.bigbank.dragons.game.config.GameProperties;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.service.AutomaticGameRunnerService;
import com.bigbank.dragons.strategy.StrategyRegistry;
import com.bigbank.dragons.strategy.StrategyType;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
public class AutomaticGameControllerTest {

  @Mock private AutomaticGameRunnerService automaticGameRunnerService;
  @Mock private ApiMapper apiMapper;
  @Mock private StrategyRegistry strategyRegistry;
  @Mock private GameProperties gameProperties;
  @Mock private ExecutorService batchExecutorService;

  @InjectMocks private AutomaticGameController controller;

  @Test
  void playWithNullStrategyFallsBackToExpectedValue() {
    GameState state = mock(GameState.class);
    GameResultDto expectedDto = mock(GameResultDto.class);

    when(gameProperties.strategy()).thenReturn("EXPECTED_VALUE");
    when(automaticGameRunnerService.playGame(StrategyType.EXPECTED_VALUE)).thenReturn(state);
    when(apiMapper.toGameResultDto(state)).thenReturn(expectedDto);

    GameResultDto result = controller.play(null);

    assertEquals(expectedDto, result);
    verify(automaticGameRunnerService).playGame(StrategyType.EXPECTED_VALUE);
  }

  @Test
  void playWithInvalidStrategyFallsBackToExpectedValue() {
    GameState state = mock(GameState.class);
    GameResultDto expectedDto = mock(GameResultDto.class);

    when(gameProperties.strategy()).thenReturn("EXPECTED_VALUE");
    when(automaticGameRunnerService.playGame(StrategyType.EXPECTED_VALUE)).thenReturn(state);
    when(apiMapper.toGameResultDto(state)).thenReturn(expectedDto);

    GameResultDto result = controller.play("unknown-strategy");

    assertEquals(expectedDto, result);
    verify(automaticGameRunnerService).playGame(StrategyType.EXPECTED_VALUE);
  }

  @Test
  void playWithValidStrategyUsesProvidedStrategy() {
    GameState state = mock(GameState.class);
    GameResultDto expectedDto = mock(GameResultDto.class);

    when(automaticGameRunnerService.playGame(StrategyType.LOW_RISK)).thenReturn(state);
    when(apiMapper.toGameResultDto(state)).thenReturn(expectedDto);

    GameResultDto result = controller.play("low-risk");

    assertEquals(expectedDto, result);
    verify(automaticGameRunnerService).playGame(StrategyType.LOW_RISK);
  }

  @Test
  void playBatchWithNullStrategyFallsBackToExpectedValue() {
    BatchStats stats = mock(BatchStats.class);
    BatchStatsDto expectedDto = mock(BatchStatsDto.class);

    when(gameProperties.strategy()).thenReturn("EXPECTED_VALUE");
    when(automaticGameRunnerService.playBatch(10, StrategyType.EXPECTED_VALUE)).thenReturn(stats);
    when(apiMapper.toDto(stats)).thenReturn(expectedDto);

    BatchStatsDto result = controller.playBatch(10, null);

    assertEquals(expectedDto, result);
    verify(automaticGameRunnerService).playBatch(10, StrategyType.EXPECTED_VALUE);
  }

  @Test
  void playBatchWithValidStrategyUsesProvidedStrategy() {
    BatchStats stats = mock(BatchStats.class);
    BatchStatsDto expectedDto = mock(BatchStatsDto.class);

    when(automaticGameRunnerService.playBatch(5, StrategyType.LOW_RISK)).thenReturn(stats);
    when(apiMapper.toDto(stats)).thenReturn(expectedDto);

    BatchStatsDto result = controller.playBatch(5, "low-risk");

    assertEquals(expectedDto, result);
    verify(automaticGameRunnerService).playBatch(5, StrategyType.LOW_RISK);
  }

  @Test
  void strategiesReturnsListOfAvailableStrategyKeys() {
    when(strategyRegistry.available())
        .thenReturn(List.of(StrategyType.EXPECTED_VALUE, StrategyType.LOW_RISK));

    List<String> results = controller.strategies();

    assertEquals(List.of("expected-value", "low-risk"), results);
  }

  @Test
  void streamGameWithValidStrategySubmitsTaskAndReturnsEmitter() {
    SseEmitter result = controller.streamGame("low-risk");

    assertNotNull(result);
    verify(batchExecutorService).submit(any(Runnable.class));
  }
}
