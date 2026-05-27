package com.bigbank.dragons.strategy;

import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.domain.ShopItem;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.probability.ProbabilityEstimator;
import java.util.List;

public interface GameStrategy {
  StrategyType type();

  /** Pick the best ad to attempt, or empty if none are worth attempting. */
  Message chooseAd(List<Message> ads, GameState state, ProbabilityEstimator estimator);

  List<ShopItem> choosePurchases(List<ShopItem> shopItems, GameState state);
}
