package com.bigbank.dragons.service;

import com.bigbank.dragons.domain.Board;
import com.bigbank.dragons.domain.BuyResponse;
import com.bigbank.dragons.domain.ShopItem;
import com.bigbank.dragons.domain.SolveResponse;
import com.bigbank.dragons.game.state.GameState;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public interface InteractiveGameService {

  GameState startGame();

  /** Board without a strategy recommendation. */
  Board getBoard(@NotBlank String gameId);

  /** Board with the named strategy's recommended pick. */
  Board getBoard(@NotBlank String gameId, @NotBlank String strategyKey);

  SolveResponse solveAd(@NotBlank String gameId, @NotBlank String adId);

  List<ShopItem> getShop(@NotBlank String gameId);

  BuyResponse buyItem(@NotBlank String gameId,@NotBlank String itemId);
}
