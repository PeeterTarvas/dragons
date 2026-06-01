package com.bigbank.dragons.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bigbank.dragons.api.dto.GameResultDto;
import com.bigbank.dragons.api.mapper.ApiMapper;
import com.bigbank.dragons.client.exception.MugloarRateLimitException;
import com.bigbank.dragons.client.exception.MugloarUnavailableException;
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
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
class AutomaticGameRunnerServiceImplTest {

  @Mock private ApiMapper apiMapper;
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
  void playBatchHandlesFutureResultsAndCollectsScores() {
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
  void playGameSafelyReturnsNullWhenGameStartFails() throws Exception {
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

  @Test
  void playBatchWithZeroGamesReturnsEmptyStats() {
    BatchStats empty = new BatchStats(0, 0.0, 0.0, 0.0, 0L, 0.0);
    when(statisticsService.snapshot(argThat(Collection::isEmpty))).thenReturn(empty);

    BatchStats result = runnerService.playBatch(0, StrategyType.EXPECTED_VALUE);

    assertEquals(0, result.games());
    verifyNoInteractions(gameService);
  }

  @Test
  void playGameMarksGoalNotReachedWhenScoreBelowTarget() {
    when(strategyRegistry.resolve(StrategyType.EXPECTED_VALUE)).thenReturn(strategy);
    when(gameService.start()).thenReturn(state);

    when(state.isAlive()).thenReturn(false);

    when(state.getScore()).thenReturn(500.0);
    when(props.targetScore()).thenReturn(1000.0);

    runnerService.playGame(StrategyType.EXPECTED_VALUE);

    verify(state).markReachedGoal(false);
  }

  @Test
  void playGameStreamingCompletesSuccessfully() throws Exception {
    SseEmitter emitter = mock(SseEmitter.class);

    when(strategyRegistry.resolve(StrategyType.EXPECTED_VALUE)).thenReturn(strategy);
    when(gameService.start()).thenReturn(state);
    when(state.isAlive()).thenReturn(true, false);
    when(props.maxTurns()).thenReturn(10);
    when(state.getTurn()).thenReturn(0);
    when(state.getGameId()).thenReturn("game-1");
    when(state.getScore()).thenReturn(1200.0);
    when(props.targetScore()).thenReturn(1000.0);

    Message ad = mock(Message.class);
    when(taskService.getTasks("game-1")).thenReturn(List.of(ad));
    when(taskService.chooseTask(any(), eq(state), any(), eq(strategy))).thenReturn(ad);

    runnerService.playGameStreaming(StrategyType.EXPECTED_VALUE, emitter);

    verify(turnExecutor).execute(eq(state), eq(ad), any());
    verify(shopService, never()).shop(any(), any());
    verify(state).markReachedGoal(true);
    verify(emitter, atLeastOnce()).send(any(SseEmitter.SseEventBuilder.class));
    verify(emitter).complete();
  }

  @Test
  void playGameStreamingSendsFailureEventAndCompletesOnException() throws Exception {
    SseEmitter emitter = mock(SseEmitter.class);
    when(gameService.start()).thenThrow(new RuntimeException("API error"));

    runnerService.playGameStreaming(StrategyType.EXPECTED_VALUE, emitter);

    verify(emitter).send(any(SseEmitter.SseEventBuilder.class));
    verify(emitter).complete();
    verify(emitter, never()).completeWithError(any());
  }

  @Test
  void playGameStreamingShopsWhenStillAliveAfterTurn() {
    SseEmitter emitter = mock(SseEmitter.class);
    when(strategyRegistry.resolve(StrategyType.EXPECTED_VALUE)).thenReturn(strategy);
    when(gameService.start()).thenReturn(state);
    when(state.isAlive()).thenReturn(true, true, false);
    when(props.maxTurns()).thenReturn(10);
    when(state.getTurn()).thenReturn(0);
    when(state.getGameId()).thenReturn("game-1");
    when(state.getScore()).thenReturn(1200.0);
    when(props.targetScore()).thenReturn(1000.0);

    Message ad = mock(Message.class);
    when(taskService.getTasks("game-1")).thenReturn(List.of(ad));
    when(taskService.chooseTask(any(), eq(state), any(), eq(strategy))).thenReturn(ad);

    runnerService.playGameStreaming(StrategyType.EXPECTED_VALUE, emitter);

    verify(shopService).shop(state, strategy);
    verify(state).markReachedGoal(true);
    verify(emitter).complete();
  }

  @Test
  void playGameStreamingExitsAtMaxTurnsAndMarksGoalNotReached() {
    SseEmitter emitter = mock(SseEmitter.class);
    when(strategyRegistry.resolve(StrategyType.EXPECTED_VALUE)).thenReturn(strategy);
    when(gameService.start()).thenReturn(state);
    when(state.isAlive()).thenReturn(true);
    when(props.maxTurns()).thenReturn(10);
    when(state.getTurn()).thenReturn(10);
    when(state.getGameId()).thenReturn("game-1");
    when(state.getScore()).thenReturn(500.0);
    when(props.targetScore()).thenReturn(1000.0);

    runnerService.playGameStreaming(StrategyType.EXPECTED_VALUE, emitter);

    verify(taskService, never()).getTasks(anyString());
    verify(state).markReachedGoal(false);
    verify(emitter).complete();
  }

  @Test
  void playGameStreamingSendsStateMappedThroughApiMapper() throws IOException {
    SseEmitter emitter = mock(SseEmitter.class);
    GameResultDto dto = mock(GameResultDto.class);

    when(strategyRegistry.resolve(StrategyType.EXPECTED_VALUE)).thenReturn(strategy);
    when(gameService.start()).thenReturn(state);
    when(state.isAlive()).thenReturn(true);
    when(props.maxTurns()).thenReturn(10);
    when(state.getTurn()).thenReturn(10);
    when(state.getGameId()).thenReturn("game-1");
    when(state.getScore()).thenReturn(500.0);
    when(props.targetScore()).thenReturn(1000.0);
    when(apiMapper.toGameResultDto(state)).thenReturn(dto);

    runnerService.playGameStreaming(StrategyType.EXPECTED_VALUE, emitter);

    verify(apiMapper, atLeastOnce()).toGameResultDto(state);
    verify(emitter, atLeastOnce()).send(any(SseEmitter.SseEventBuilder.class));
    verify(emitter).complete();
  }

  @Test
  void playGameStreamingStopsQuietlyWhenClientDisconnects() throws Exception {
    SseEmitter emitter = mock(SseEmitter.class);
    when(strategyRegistry.resolve(StrategyType.EXPECTED_VALUE)).thenReturn(strategy);
    when(gameService.start()).thenReturn(state);
    when(state.isAlive()).thenReturn(true);
    when(props.maxTurns()).thenReturn(10);
    when(state.getTurn()).thenReturn(0);
    when(state.getGameId()).thenReturn("game-1");

    Message ad = mock(Message.class);
    when(taskService.getTasks("game-1")).thenReturn(List.of(ad));
    when(taskService.chooseTask(any(), eq(state), any(), eq(strategy))).thenReturn(ad);

    doNothing()
        .doThrow(new IOException("Connection reset by peer"))
        .when(emitter)
        .send(any(SseEmitter.SseEventBuilder.class));

    runnerService.playGameStreaming(StrategyType.EXPECTED_VALUE, emitter);

    verify(emitter, never()).completeWithError(any());
  }

  @Test
  void playGameStreamingStopsWhenClientDisconnectsMidLoop() {
    SseEmitter emitter = mock(SseEmitter.class);
    AtomicReference<Runnable> onCompletion = new AtomicReference<>();
    doAnswer(
            inv -> {
              onCompletion.set(inv.getArgument(0));
              return null;
            })
        .when(emitter)
        .onCompletion(any());

    when(strategyRegistry.resolve(StrategyType.EXPECTED_VALUE)).thenReturn(strategy);
    when(gameService.start()).thenReturn(state);
    when(state.isAlive()).thenReturn(true);
    when(props.maxTurns()).thenReturn(10);
    when(state.getTurn()).thenReturn(0);
    when(state.getGameId()).thenReturn("game-1");

    Message ad = mock(Message.class);
    when(taskService.getTasks("game-1")).thenReturn(List.of(ad));
    when(taskService.chooseTask(any(), eq(state), any(), eq(strategy))).thenReturn(ad);

    doAnswer(
            _ -> {
              onCompletion.get().run();
              return null;
            })
        .when(turnExecutor)
        .execute(eq(state), eq(ad), any());

    runnerService.playGameStreaming(StrategyType.EXPECTED_VALUE, emitter);

    verify(state, never()).markReachedGoal(anyBoolean());
    verify(emitter, never()).complete();
  }

  @Test
  void playGameStreamingLifecycleCallbacksRunSafely() {
    SseEmitter emitter = mock(SseEmitter.class);
    AtomicReference<Runnable> onCompletion = new AtomicReference<>();
    AtomicReference<Runnable> onTimeout = new AtomicReference<>();
    AtomicReference<Consumer<Throwable>> onError = new AtomicReference<>();
    doAnswer(
            inv -> {
              onCompletion.set(inv.getArgument(0));
              return null;
            })
        .when(emitter)
        .onCompletion(any());
    doAnswer(
            inv -> {
              onTimeout.set(inv.getArgument(0));
              return null;
            })
        .when(emitter)
        .onTimeout(any());
    doAnswer(
            inv -> {
              onError.set(inv.getArgument(0));
              return null;
            })
        .when(emitter)
        .onError(any());

    when(gameService.start()).thenThrow(new RuntimeException("stop early"));

    runnerService.playGameStreaming(StrategyType.EXPECTED_VALUE, emitter);

    assertDoesNotThrow(
        () -> {
          onCompletion.get().run();
          onTimeout.get().run();
          onError.get().accept(new RuntimeException("boom"));
        });
  }

  @Test
  void playGameStreamingSwallowsErrorWhenFailureCannotBeDelivered() throws Exception {
    SseEmitter emitter = mock(SseEmitter.class);
    when(gameService.start()).thenThrow(new MugloarRateLimitException("429", null));
    doThrow(new IOException("broken pipe"))
        .when(emitter)
        .send(any(SseEmitter.SseEventBuilder.class));

    runnerService.playGameStreaming(StrategyType.EXPECTED_VALUE, emitter);

    verify(emitter, never()).completeWithError(any());
  }

  @Test
  void playGameStreamingReportsBusyMessageForUnavailableUpstream() throws IOException {
    SseEmitter emitter = mock(SseEmitter.class);
    when(gameService.start()).thenThrow(new MugloarUnavailableException("503"));

    runnerService.playGameStreaming(StrategyType.EXPECTED_VALUE, emitter);

    verify(emitter).send(any(SseEmitter.SseEventBuilder.class));
    verify(emitter).complete();
    verify(emitter, never()).completeWithError(any());
  }

  @Test
  void playGameStreamingHandlesInterruptionDuringSleep() {
    SseEmitter emitter = mock(SseEmitter.class);
    when(strategyRegistry.resolve(StrategyType.EXPECTED_VALUE)).thenReturn(strategy);
    when(gameService.start()).thenReturn(state);
    when(state.isAlive()).thenReturn(true);
    when(props.maxTurns()).thenReturn(10);
    when(state.getTurn()).thenReturn(0);
    when(state.getGameId()).thenReturn("game-1");

    Message ad = mock(Message.class);
    when(taskService.getTasks("game-1")).thenReturn(List.of(ad));
    when(taskService.chooseTask(any(), eq(state), any(), eq(strategy))).thenReturn(ad);

    doAnswer(
            inv -> {
              Thread.currentThread().interrupt();
              return null;
            })
        .when(turnExecutor)
        .execute(eq(state), eq(ad), any());

    try {
      runnerService.playGameStreaming(StrategyType.EXPECTED_VALUE, emitter);
      verify(state, never()).markReachedGoal(anyBoolean());
    } finally {
      Thread.interrupted();
    }
  }
}
