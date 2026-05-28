package com.bigbank.dragons.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bigbank.dragons.domain.BatchStats;
import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.game.config.GameProperties;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.game.turn.TurnExecutor;
import com.bigbank.dragons.service.GameService;
import com.bigbank.dragons.service.ShopService;
import com.bigbank.dragons.service.StatisticsService;
import com.bigbank.dragons.service.TaskService;
import com.bigbank.dragons.strategy.GameStrategy;
import com.bigbank.dragons.strategy.StrategyRegistry;
import com.bigbank.dragons.strategy.StrategyType;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AutomaticGameRunnerServiceImplTest {

  @Mock private GameService gameService;
  @Mock private TaskService taskService;
  @Mock private ShopService shopService;
  @Mock private StatisticsService statisticsService;
  @Mock private GameProperties props;
  @Mock private StrategyRegistry strategyRegistry;
  @Mock private TurnExecutor turnExecutor;
  @Mock private ExecutorService batchExecutorService;

  @Captor private ArgumentCaptor<Callable<GameState>> callableCaptor;

  @InjectMocks private AutomaticGameRunnerServiceImpl runnerService;

  private GameState state;
  private GameStrategy strategy;

  @BeforeEach
  void setUp() {
    state = mock(GameState.class);
    strategy = mock(GameStrategy.class);
  }

  @Test
  void playGameCompletesLoopAndShopsWhenAlive() {
    when(strategyRegistry.resolve(StrategyType.EXPECTED_VALUE)).thenReturn(strategy);
    when(gameService.start()).thenReturn(state);

    when(state.isAlive()).thenReturn(true, true, false);
    when(props.maxTurns()).thenReturn(10);
    when(state.getTurn()).thenReturn(0);
    when(state.getGameId()).thenReturn("game-1");
    when(state.getScore()).thenReturn(2000.0);
    when(props.targetScore()).thenReturn(1000.0);

    Message ad = mock(Message.class);
    when(taskService.getTasks("game-1")).thenReturn(List.of(ad));
    when(taskService.chooseTask(any(), eq(state), any(), eq(strategy))).thenReturn(ad);

    GameState result = runnerService.playGame(StrategyType.EXPECTED_VALUE);

    assertEquals(state, result);
    verify(turnExecutor).execute(eq(state), eq(ad), any());
    verify(shopService).shop(state, strategy);
    verify(state).markReachedGoal(true);
  }

  @Test
  void playGameDoesNotShopWhenDiesDuringTurn() {
    when(strategyRegistry.resolve(StrategyType.EXPECTED_VALUE)).thenReturn(strategy);
    when(gameService.start()).thenReturn(state);

    when(state.isAlive()).thenReturn(true, false);
    when(props.maxTurns()).thenReturn(10);
    when(state.getTurn()).thenReturn(0);
    when(state.getGameId()).thenReturn("game-1");

    Message ad = mock(Message.class);
    when(taskService.getTasks("game-1")).thenReturn(List.of(ad));
    when(taskService.chooseTask(any(), eq(state), any(), eq(strategy))).thenReturn(ad);

    runnerService.playGame(StrategyType.EXPECTED_VALUE);

    verify(turnExecutor).execute(eq(state), eq(ad), any());
    verify(shopService, never()).shop(any(), any());
  }

  @Test
  void playGameExitsLoopWhenMaxTurnsReached() {
    when(strategyRegistry.resolve(StrategyType.EXPECTED_VALUE)).thenReturn(strategy);
    when(gameService.start()).thenReturn(state);

    when(state.isAlive()).thenReturn(true);
    when(props.maxTurns()).thenReturn(10);
    when(state.getTurn()).thenReturn(10);

    runnerService.playGame(StrategyType.EXPECTED_VALUE);

    verify(taskService, never()).getTasks(anyString());
    verify(state).markReachedGoal(anyBoolean());
  }

  @Test
  void playGamePropagatesException() {
    when(gameService.start()).thenThrow(new IllegalStateException("API error"));

    assertThrows(
        IllegalStateException.class, () -> runnerService.playGame(StrategyType.EXPECTED_VALUE));
  }

  @Test
  @SuppressWarnings("unchecked")
  void playBatchHandlesFutureResultsAndCollectsScores() throws Exception {
    BatchStats mockStats = mock(BatchStats.class);
    when(statisticsService.snapshot(any())).thenReturn(mockStats);

    when(batchExecutorService.submit(any(Callable.class)))
        .thenAnswer(
            invocation -> {
              Future<GameState> future = mock(Future.class);
              when(future.get())
                  .thenReturn(state)
                  .thenReturn(null)
                  .thenThrow(new RuntimeException("Execution failed"));
              return future;
            });

    when(state.getScore()).thenReturn(100.0);

    BatchStats result = runnerService.playBatch(3, StrategyType.LOW_RISK);

    assertEquals(mockStats, result);
    verify(batchExecutorService, times(3)).submit(any(Callable.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  void playGameSafely_ReturnsNullWhenGameStartFails() throws Exception {
    when(batchExecutorService.submit(callableCaptor.capture())).thenReturn(mock(Future.class));
    runnerService.playBatch(1, StrategyType.EXPECTED_VALUE);

    Callable<GameState> playGameSafelyCallable = callableCaptor.getValue();

    when(gameService.start()).thenThrow(new RuntimeException("API is completely down"));

    GameState result = playGameSafelyCallable.call();

    assertNull(result);
  }

  @Test
  @SuppressWarnings("unchecked")
  void playGameSafelyReturnsPartialStateWhenInterruptedDuringLoop() throws Exception {
    when(batchExecutorService.submit(callableCaptor.capture())).thenReturn(mock(Future.class));
    runnerService.playBatch(1, StrategyType.EXPECTED_VALUE);

    Callable<GameState> playGameSafelyCallable = callableCaptor.getValue();

    when(strategyRegistry.resolve(StrategyType.EXPECTED_VALUE)).thenReturn(strategy);
    when(gameService.start()).thenReturn(state);
    when(state.isAlive()).thenReturn(true);
    when(state.getTurn()).thenReturn(0);
    when(props.maxTurns()).thenReturn(10);

    when(taskService.getTasks(any())).thenThrow(new RuntimeException("Network timeout mid-game"));

    GameState result = playGameSafelyCallable.call();

    assertEquals(state, result);
  }
}
