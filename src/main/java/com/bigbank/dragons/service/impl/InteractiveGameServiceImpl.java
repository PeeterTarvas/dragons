package com.bigbank.dragons.service.impl;

import com.bigbank.dragons.api.exception.InvalidStrategyException;
import com.bigbank.dragons.client.MugloarClient;
import com.bigbank.dragons.client.mapper.ClientMapper;
import com.bigbank.dragons.domain.*;
import com.bigbank.dragons.game.config.GameProperties;
import com.bigbank.dragons.game.session.GameSession;
import com.bigbank.dragons.game.session.GameSessionStore;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.game.turn.TurnExecutor;
import com.bigbank.dragons.service.GameService;
import com.bigbank.dragons.service.InteractiveGameService;
import com.bigbank.dragons.service.TaskService;
import com.bigbank.dragons.service.validation.GameActionValidator;
import com.bigbank.dragons.strategy.GameStrategy;
import com.bigbank.dragons.strategy.StrategyRegistry;
import com.bigbank.dragons.strategy.StrategyType;
import jakarta.validation.constraints.NotBlank;
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

  private final MugloarClient client;
  private final ClientMapper clientMapper;
  private final GameSessionStore sessionStore;
  private final TaskService taskService;
  private final GameService gameService;
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
  public Board getBoard(@NotBlank String gameId) {
    return buildBoard(gameId, Optional.empty());
  }

  @Override
  public Board getBoard(@NotBlank String gameId, @NotBlank String strategyKey) {
    StrategyType type =
        StrategyType.fromKey(strategyKey)
            .orElseThrow(() -> new InvalidStrategyException("Unknown strategy: " + strategyKey));

    return buildBoard(gameId, Optional.of(strategyRegistry.resolve(type)));
  }

  private Board buildBoard(@NotBlank String gameId, Optional<GameStrategy> strategyOpt) {
    GameSession session = sessionStore.get(gameId);
    validator.validateGameIsActive(session);

    List<Message> messages = taskService.getTasks(gameId);
    session.setLastBoard(messages);
    String recommendedAdId =
        strategyOpt
            .filter(_ -> !messages.isEmpty())
            .map(
                strat ->
                    strat.chooseAd(messages, session.getState(), session.getEstimator()).adId())
            .orElse(null);

    List<EvaluatedMessage> evaluated =
        messages.stream()
            .map(message -> new EvaluatedMessage(message, session.getEstimator().estimate(message)))
            .sorted(Comparator.comparing(EvaluatedMessage::estimatedSuccess).reversed())
            .toList();

    return new Board(evaluated, recommendedAdId);
  }

  @Override
  public SolveResponse solveAd(@NotBlank String gameId, @NotBlank String adId) {
    GameSession session = sessionStore.get(gameId);
    validator.validateGameIsActive(session);
    Message ad = validator.validateAndGetAd(session, adId);

    SolveResponse result = turnExecutor.execute(session.getState(), ad, session.getEstimator());

    session.getState().markReachedGoal(session.getState().getScore() >= props.targetScore());
    if (!session.getState().isAlive()) {
      sessionStore.remove(gameId);
    }
    return result;
  }

  @Override
  public List<ShopItem> getShop(@NotBlank String gameId) {
    GameSession session = sessionStore.get(gameId);
    validator.validateGameIsActive(session);

    return client.getShop(gameId).stream()
        .map(i -> new ShopItem(i.id(), i.name(), i.cost()))
        .toList();
  }

  @Override
  public BuyResponse buyItem(@NotBlank String gameId, @NotBlank String itemId) {
    GameSession session = sessionStore.get(gameId);
    validator.validateGameIsActive(session);

    BuyResponse buy = clientMapper.toDomain(client.buy(gameId, itemId));
    session.getState().updateAfterBuy(buy);
    return buy;
  }
}
