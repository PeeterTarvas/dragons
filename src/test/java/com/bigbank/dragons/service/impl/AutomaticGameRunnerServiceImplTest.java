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

    @InjectMocks private AutomaticGameRunnerServiceImpl runnerService;

    private GameState state;

    @BeforeEach
    void setUp() {
        state = mock(GameState.class);
    }

    @Test
    void playGameCompletesLoopAndUpdatesStats() {
        GameStrategy strategy = mock(GameStrategy.class);
        when(strategyRegistry.resolve(StrategyType.EXPECTED_VALUE)).thenReturn(strategy);
        when(gameService.start()).thenReturn(state);

        when(state.isAlive()).thenReturn(true, true, false);
        when(props.maxTurns()).thenReturn(10);
        when(state.getTurn()).thenReturn(0);
        when(state.getGameId()).thenReturn("game-1");

        Message ad = mock(Message.class);
        when(taskService.getTasks("game-1")).thenReturn(List.of(ad));
        when(taskService.chooseTask(any(), eq(state), any(), eq(strategy))).thenReturn(ad);
        when(state.getScore()).thenReturn(2000.0);
        when(props.targetScore()).thenReturn(1000.0);

        GameState result = runnerService.playGame(StrategyType.EXPECTED_VALUE);

        assertEquals(state, result);
        verify(turnExecutor).execute(eq(state), eq(ad), any());
        verify(shopService).shop(state, strategy);
        verify(state).markReachedGoal(true);
    }

    @Test
    void playGameExceptionThrownWrappedInRuntimeException() {
        when(strategyRegistry.resolve(StrategyType.EXPECTED_VALUE)).thenReturn(mock(GameStrategy.class));
        when(gameService.start()).thenThrow(new IllegalStateException("API error"));

        assertThrows(RuntimeException.class, () -> runnerService.playGame(StrategyType.EXPECTED_VALUE));
    }

    @Test
    @SuppressWarnings("unchecked")
    void playBatchHandlesSuccessFailureAndExceptionFutures() {
        BatchStats mockStats = mock(BatchStats.class);
        when(statisticsService.snapshot(any())).thenReturn(mockStats);

        when(batchExecutorService.submit(any(Callable.class))).thenAnswer(invocation -> {
            Callable<GameState> callable = invocation.getArgument(0);
            Future<GameState> future = mock(Future.class);

            when(future.get())
                    .thenReturn(state) // Run 1
                    .thenReturn(null)  // Run 2
                    .thenThrow(new RuntimeException("Future failed")); // Run 3
            return future;
        });

        when(state.getScore()).thenReturn(100.0);

        BatchStats result = runnerService.playBatch(3, StrategyType.LOW_RISK);

        assertEquals(mockStats, result);
        verify(batchExecutorService, times(3)).submit(any(Callable.class));
    }
}
