package com.bigbank.dragons.game;

import com.bigbank.dragons.api.dto.GameResultDto;
import com.bigbank.dragons.api.dto.GameStatusDto;
import com.bigbank.dragons.api.dto.TurnLogDto;
import com.bigbank.dragons.client.MugloarClient;
import com.bigbank.dragons.client.dto.*;
import com.bigbank.dragons.decoder.AdDecoder;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.strategy.GameStrategy;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameRunner {

  private static final int MIN_TARGET_SCORE = 1000;
  private static final int MAX_TURNS = 1000;

  private final MugloarClient client;
  private final GameStrategy strategy;
  private final AdDecoder decoder;

  private volatile boolean inProgress = false;
  private volatile GameResultDto lastResult = null;

  public GameResultDto playGame() {
    inProgress = true;
    try {
      StartGameResponse start = client.startGame();
      GameState state = new GameState(start);
      ProbabilityEstimator estimator = new ProbabilityEstimator();
      log.info(
          "Started game {} (lives={}, gold={})",
          state.getGameId(),
          state.getLives(),
          state.getGold());

      while (state.isAlive() && state.getTurn() < MAX_TURNS) {

        if (state.getTurn() > 10) maybeShop(state);
        if (!state.isAlive()) break;

        List<Message> ads =
            client.getMessages(state.getGameId()).stream().map(decoder::decode).toList();

        Optional<Message> choice = strategy.chooseAd(ads, state, estimator);
        if (choice.isEmpty()) {
          log.info("Turn {}: no acceptable ad, attempting upgrade", state.getTurn());
          if (!attemptUpgrade(state)) {
            log.info("Turn {}: nothing to do, ending run", state.getTurn());
            break;
          }
          continue;
        }

        Message ad = choice.get();
        SolveResponse result = client.solve(state.getGameId(), ad.adId());
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

      boolean reached = state.getScore() >= MIN_TARGET_SCORE;
      log.info(
          "Game {} finished: score={}, turns={}, target={}",
          state.getGameId(),
          state.getScore(),
          state.getTurn(),
          reached);

      GameResultDto dto =
          new GameResultDto(
              state.getGameId(),
              state.getScore(),
              state.getGold(),
              state.getTurn(),
              reached,
              state.getLog());
      lastResult = dto;
      return dto;
    } finally {
      inProgress = false;
    }
  }

  private void maybeShop(GameState state) {
    List<ShopItem> items = client.getShop(state.getGameId());
    strategy.choosePurchase(items, state).ifPresent(item -> buy(state, item));
  }

  private boolean attemptUpgrade(GameState state) {
    List<ShopItem> items = client.getShop(state.getGameId());
    Optional<ShopItem> pick = strategy.choosePurchase(items, state);
    pick.ifPresent(item -> buy(state, item));
    return pick.isPresent();
  }

  private void buy(GameState state, ShopItem item) {
    BuyResponse buy = client.buy(state.getGameId(), item.id());
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

  public GameStatusDto status() {
    return new GameStatusDto(inProgress, lastResult);
  }
}
