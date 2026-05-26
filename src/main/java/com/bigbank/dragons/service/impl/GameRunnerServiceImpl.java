package com.bigbank.dragons.service.impl;

import com.bigbank.dragons.api.dto.GameResultDto;
import com.bigbank.dragons.api.dto.GameStatusDto;
import com.bigbank.dragons.api.dto.TurnLogDto;
import com.bigbank.dragons.client.MugloarClient;
import com.bigbank.dragons.client.dto.*;
import com.bigbank.dragons.decoder.AdDecoder;
import com.bigbank.dragons.game.ProbabilityEstimator;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.mapper.GameStateMapper;
import com.bigbank.dragons.service.GameRunnerService;
import com.bigbank.dragons.strategy.GameStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameRunnerServiceImpl implements GameRunnerService {

  private static final int MIN_TARGET_SCORE = 1000;
  private static final int MAX_TURNS = 1000;

  private final MugloarClient client;
  private final GameStrategy strategy;
  private final AdDecoder decoder;
  private final GameStateMapper gameStateMapper;

  private volatile GameState lastResult = null;
  private volatile boolean reached = false;


  public GameState playGame() {
    StartGameResponseDto start = client.startGame();
    GameState state = gameStateMapper.toEntity(start);
    ProbabilityEstimator estimator = new ProbabilityEstimator();
    try {
      log.info(
          "Started game {} (lives={}, gold={})",
          state.getGameId(),
          state.getLives(),
          state.getGold());

      while (state.isAlive() && state.getTurn() < MAX_TURNS) {

        maybeShop(state);
        if (!state.isAlive()) break;

        List<MessageDto> ads =
            client.getMessages(state.getGameId()).stream().map(decoder::decode).toList();

        Optional<MessageDto> choice = strategy.chooseAd(ads, state, estimator);
        if (choice.isEmpty()) {
          log.info("Turn {}: no acceptable ad, attempting upgrade", state.getTurn());
          if (!attemptUpgrade(state)) {
            log.info("Turn {}: nothing to do, ending run", state.getTurn());
            break;
          }
          continue;
        }

        MessageDto ad = choice.get();
        SolveResponseDto result = client.solve(state.getGameId(), ad.adId());
        reached = state.getScore() >= MIN_TARGET_SCORE;


        state.update(result.lives(), result.gold(), result.score(), result.turn());
        estimator.record(ad.probability(), result.success());
        log.info(
            "Turn {}: solve '{}' [{}] -> {} (score={}, lives={})",
            result.turn(),
            ad.message(),
            ad.probability(),
            result.success() ? "WIN" : "LOSS",
            result.score(),
            result.lives());
        state.addLog(
            new TurnLogDto(
                result.turn(),
                "SOLVE",
                ad.message(),
                ad.probability(),
                result.success(),
                result.score(),
                result.lives(),
                result.gold()));
      }

      log.info(
          "Game {} finished: score={}, turns={}, target={}",
          state.getGameId(),
          state.getScore(),
          state.getTurn(),
          reached);

      if (reached && !state.isReachedGoal()) {
        state.markReachedGoal(reached);
      }

      lastResult = state;
      return state;
    } finally {
      lastResult = state;
    }
  }

  private void maybeShop(GameState state) {
    List<ShopItemDto> items = client.getShop(state.getGameId());
    strategy.choosePurchase(items, state).ifPresent(item -> buy(state, item));
  }

  private boolean attemptUpgrade(GameState state) {
    List<ShopItemDto> items = client.getShop(state.getGameId());
    Optional<ShopItemDto> pick = strategy.choosePurchase(items, state);
    pick.ifPresent(item -> buy(state, item));
    return pick.isPresent();
  }

  private void buy(GameState state, ShopItemDto item) {
    BuyResponseDto buy = client.buy(state.getGameId(), item.id());
    state.updateAfterBuy(buy.gold(), buy.lives(), buy.level(), buy.turn());
    log.info(
        "Bought '{}' for gold; now gold={}, lives={}, level={}",
        item.name(),
        buy.gold(),
        buy.lives(),
        buy.level());
    state.addLog(
        new TurnLogDto(
            buy.turn(),
            "BUY",
            item.name(),
            null,
            buy.shoppingSuccess(),
            state.getScore(),
            buy.lives(),
            buy.gold()));
  }


}
