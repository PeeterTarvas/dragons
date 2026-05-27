package com.bigbank.dragons.service;

import com.bigbank.dragons.domain.Board;
import com.bigbank.dragons.domain.ShopItem;
import com.bigbank.dragons.domain.SolveResponse;
import com.bigbank.dragons.game.state.GameState;
import java.util.List;

public interface InteractiveGameService {

  GameState startGame();

  /** Board without a strategy recommendation. */
  Board getBoard(String gameId);

  /** Board with the named strategy's recommended pick. */
  Board getBoard(String gameId, String strategyKey);

  SolveResponse solveAd(String gameId, String adId);

  List<ShopItem> getShop(String gameId);

  GameState buyItem(String gameId, String itemId);
}
