package com.bigbank.dragons.strategy;

import com.bigbank.dragons.client.dto.Message;
import com.bigbank.dragons.client.dto.ShopItem;
import com.bigbank.dragons.game.ProbabilityEstimator;
import com.bigbank.dragons.game.state.GameState;
import java.util.List;
import java.util.Optional;

public interface GameStrategy {

  /** Pick the best ad to attempt, or empty if none are worth attempting. */
  Optional<Message> chooseAd(List<Message> ads, GameState state, ProbabilityEstimator estimator);

  Optional<ShopItem> choosePurchase(List<ShopItem> shopItems, GameState state);
}
