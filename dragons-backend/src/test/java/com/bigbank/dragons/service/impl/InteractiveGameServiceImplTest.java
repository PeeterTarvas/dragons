package com.bigbank.dragons.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bigbank.dragons.api.exception.InvalidStrategyException;
import com.bigbank.dragons.domain.Board;
import com.bigbank.dragons.domain.BuyResponse;
import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.domain.ShopItem;
import com.bigbank.dragons.domain.SolveResponse;
import com.bigbank.dragons.game.config.GameProperties;
import com.bigbank.dragons.game.session.GameSession;
import com.bigbank.dragons.game.session.GameSessionStore;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.game.turn.TurnExecutor;
import com.bigbank.dragons.probability.ProbabilityEstimator;
import com.bigbank.dragons.service.GameService;
import com.bigbank.dragons.service.ShopService;
import com.bigbank.dragons.service.TaskService;
import com.bigbank.dragons.service.validation.GameActionValidator;
import com.bigbank.dragons.strategy.GameStrategy;
import com.bigbank.dragons.strategy.StrategyRegistry;
import com.bigbank.dragons.strategy.StrategyType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InteractiveGameServiceImplTest {

  @Mock private GameSessionStore sessionStore;
  @Mock private TaskService taskService;
  @Mock private GameService gameService;
  @Mock private ShopService shopService;
  @Mock private StrategyRegistry strategyRegistry;
  @Mock private GameProperties props;
  @Mock private TurnExecutor turnExecutor;
  @Mock private GameActionValidator validator;

  @InjectMocks private InteractiveGameServiceImpl service;

  private GameState mockState;
  private GameSession mockSession;

  @BeforeEach
  void setUp() {
    mockState = mock(GameState.class);
    mockSession = mock(GameSession.class);
  }

  @Test
  void startGameCreatesSessionAndReturnsState() {
    when(gameService.start()).thenReturn(mockState);
    when(mockState.getGameId()).thenReturn("game-123");

    GameState result = service.startGame();

    assertEquals(mockState, result);
    verify(sessionStore).create(mockState);
  }

  @Test
  void getBoardNoStrategyReturnsBoardWithoutRecommendation() {
    when(sessionStore.get("game-1")).thenReturn(mockSession);
    when(mockSession.getEstimator()).thenReturn(new ProbabilityEstimator());

    Message msg = new Message("ad-1", "msg", 10, 10, 0, "p");
    when(taskService.getTasks("game-1")).thenReturn(List.of(msg));

    Board result = service.getBoard("game-1");

    assertEquals("", result.recommendedAdId());
    assertEquals(1, result.messages().size());
    assertEquals("ad-1", result.messages().getFirst().message().adId());
    verify(validator).validateGameIsActive(mockSession);
    verify(mockSession).updateAvailableMessages(anyList());
  }

  @Test
  void getBoardWithInvalidStrategyThrowsException() {
    assertThrows(InvalidStrategyException.class, () -> service.getBoard("game-1", "invalid-key"));
  }

  @Test
  void getBoardWithValidStrategyReturnsBoardWithRecommendation() {
    when(sessionStore.get("game-1")).thenReturn(mockSession);
    when(mockSession.getEstimator()).thenReturn(new ProbabilityEstimator());
    when(mockSession.getState()).thenReturn(mockState);

    Message msg = new Message("ad-1", "msg", 10, 10, 0, "p");
    when(taskService.getTasks("game-1")).thenReturn(List.of(msg));

    GameStrategy mockStrategy = mock(GameStrategy.class);
    when(strategyRegistry.resolve(StrategyType.LOW_RISK)).thenReturn(mockStrategy);
    when(mockStrategy.chooseAd(anyList(), eq(mockState), any())).thenReturn(msg);

    Board result = service.getBoard("game-1", "low-risk");

    assertEquals("ad-1", result.recommendedAdId());
    assertEquals(1, result.messages().size());
  }

  @Test
  void solveAdGameSurvivesStateUpdatedAndMaintained() {
    Message ad = mock(Message.class);
    SolveResponse solveResponse = mock(SolveResponse.class);

    when(sessionStore.get("game-1")).thenReturn(mockSession);
    when(mockSession.getState()).thenReturn(mockState);
    when(mockSession.getEstimator()).thenReturn(mock(ProbabilityEstimator.class));
    when(turnExecutor.execute(any(), any(), any())).thenReturn(solveResponse);
    when(mockState.getScore()).thenReturn(1500.0);
    when(props.targetScore()).thenReturn(1000.0);
    when(mockState.isAlive()).thenReturn(true);

    SolveResponse result = service.solveAd("game-1", ad);

    assertEquals(solveResponse, result);
    verify(validator).validateGameIsActive(mockSession);
    verify(validator).validateMessage(mockSession, ad);
    verify(mockState).markReachedGoal(true);
    verify(sessionStore, never()).remove("game-1");
  }

  @Test
  void solveAdGameDiesSessionIsRemoved() {
    Message ad = mock(Message.class);

    when(sessionStore.get("game-1")).thenReturn(mockSession);
    when(mockSession.getState()).thenReturn(mockState);
    when(mockSession.getEstimator()).thenReturn(mock(ProbabilityEstimator.class));
    when(mockState.isAlive()).thenReturn(false);

    service.solveAd("game-1", ad);

    verify(sessionStore).remove("game-1");
  }

  @Test
  void getShopReturnsShopItems() {
    List<ShopItem> items = List.of(mock(ShopItem.class));
    when(sessionStore.get("game-1")).thenReturn(mockSession);
    when(mockSession.getState()).thenReturn(mockState);
    when(shopService.getShopItems(mockState)).thenReturn(items);

    List<ShopItem> result = service.getShop("game-1");

    assertEquals(items, result);
    verify(validator).validateGameIsActive(mockSession);
  }

  @Test
  void buyItemReturnsBuyResponse() {
    ShopItem item = mock(ShopItem.class);
    BuyResponse response = mock(BuyResponse.class);
    when(sessionStore.get("game-1")).thenReturn(mockSession);
    when(mockSession.getState()).thenReturn(mockState);
    when(shopService.buyItem(mockState, item)).thenReturn(response);

    BuyResponse result = service.buyItem("game-1", item);

    assertEquals(response, result);
    verify(validator).validateGameIsActive(mockSession);
  }
}
