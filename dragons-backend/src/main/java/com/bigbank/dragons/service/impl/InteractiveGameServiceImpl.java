package com.bigbank.dragons.service.impl;

import com.bigbank.dragons.api.exception.InvalidStrategyException;
import com.bigbank.dragons.domain.Board;
import com.bigbank.dragons.domain.BuyResponse;
import com.bigbank.dragons.domain.EvaluatedMessage;
import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.domain.ShopItem;
import com.bigbank.dragons.domain.SolveResponse;
import com.bigbank.dragons.game.config.GameProperties;
import com.bigbank.dragons.game.session.GameSession;
import com.bigbank.dragons.game.session.GameSessionStore;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.game.turn.TurnExecutor;
import com.bigbank.dragons.service.GameService;
import com.bigbank.dragons.service.InteractiveGameService;
import com.bigbank.dragons.service.ShopService;
import com.bigbank.dragons.service.TaskService;
import com.bigbank.dragons.service.validation.GameActionValidator;
import com.bigbank.dragons.strategy.GameStrategy;
import com.bigbank.dragons.strategy.StrategyRegistry;
import com.bigbank.dragons.strategy.StrategyType;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class InteractiveGameServiceImpl implements InteractiveGameService {

  private final GameSessionStore sessionStore;
  private final TaskService taskService;
  private final GameService gameService;
  private final ShopService shopService;
  private final StrategyRegistry strategyRegistry;
  private final GameProperties props;
  private final TurnExecutor turnExecutor;
  private final GameActionValidator validator;

  @Override
  public GameState startGame() {
    GameState gameState = gameService.start();
    sessionStore.create(gameState);
    log.info("Interactive game started: {}", gameState.getGameId());
    return gameState;
  }

  @Override
  public Board getBoard(String gameId) {
    return buildBoard(gameId, Optional.empty());
  }

  @Override
  public Board getBoard(String gameId, String strategyKey) {
    StrategyType type =
        StrategyType.fromKey(strategyKey)
            .orElseThrow(() -> new InvalidStrategyException("Unknown strategy: " + strategyKey));

    return buildBoard(gameId, Optional.of(strategyRegistry.resolve(type)));
  }

  @Override
  public SolveResponse solveAd(String gameId, Message ad) {
    GameSession session = sessionStore.get(gameId);
    validator.validateGameIsActive(session);
    validator.validateMessage(session, ad);

    SolveResponse result = turnExecutor.execute(session.getState(), ad, session.getEstimator());

    session.getState().markReachedGoal(session.getState().getScore() >= props.targetScore());
    return result;
  }

  @Override
  public List<ShopItem> getShop(String gameId) {
    GameSession session = sessionStore.get(gameId);
    validator.validateGameIsActive(session);
    return shopService.getShopItems(session.getState());
  }

  @Override
  public BuyResponse buyItem(String gameId, ShopItem shopItem) {
    GameSession session = sessionStore.get(gameId);
    validator.validateGameIsActive(session);
    return shopService.buyItem(session.getState(), shopItem);
  }

  @Override
  public GameState getGameState(String gameId) {
    return sessionStore.get(gameId).getState();
  }

  private Board buildBoard(String gameId, Optional<GameStrategy> strategyOpt) {
    GameSession session = sessionStore.get(gameId);
    validator.validateGameIsActive(session);

    List<Message> messages = taskService.getTasks(gameId);
    session.updateAvailableMessages(messages);

    Optional<String> recommendedAdIdOpt =
        strategyOpt
            .filter(_ -> !messages.isEmpty())
            .map(
                strat ->
                    strat.chooseAd(messages, session.getState(), session.getEstimator()).adId());

    List<EvaluatedMessage> evaluated =
        messages.stream()
            .map(message -> new EvaluatedMessage(message, session.getEstimator().estimate(message)))
            .sorted(Comparator.comparing(EvaluatedMessage::estimatedSuccess).reversed())
            .toList();
    String recommendedAdId = recommendedAdIdOpt.orElse("");
    return new Board(evaluated, recommendedAdId);
  }
}
