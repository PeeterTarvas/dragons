package com.bigbank.dragons.service;

import com.bigbank.dragons.domain.*;
import com.bigbank.dragons.game.state.GameState;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public interface InteractiveGameService {

  GameState startGame();

  /** Board without a strategy recommendation. */
  Board getBoard(@NotBlank String gameId);

  /** Board with the named strategy's recommended pick. */
  Board getBoard(@NotBlank String gameId, @NotBlank String strategyKey);

  SolveResponse solveAd(@NotBlank String gameId, Message adId);

  List<ShopItem> getShop(@NotBlank String gameId);

  BuyResponse buyItem(@NotBlank String gameId, ShopItem itemId);

  GameState getGameState(@NotBlank String gameId);
}
