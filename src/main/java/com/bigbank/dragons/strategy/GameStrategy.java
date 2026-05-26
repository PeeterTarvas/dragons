package com.bigbank.dragons.strategy;

import com.bigbank.dragons.client.dto.MessageDto;
import com.bigbank.dragons.client.dto.ShopItemDto;
import com.bigbank.dragons.game.ProbabilityEstimator;
import com.bigbank.dragons.game.state.GameState;
import java.util.List;
import java.util.Optional;

public interface GameStrategy {

  /** Pick the best ad to attempt, or empty if none are worth attempting. */
  Optional<MessageDto> chooseAd(List<MessageDto> ads, GameState state, ProbabilityEstimator estimator);

  Optional<ShopItemDto> choosePurchase(List<ShopItemDto> shopItemDtos, GameState state);
}
