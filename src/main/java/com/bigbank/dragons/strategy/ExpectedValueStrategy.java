package com.bigbank.dragons.strategy;

import com.bigbank.dragons.client.dto.MessageDto;
import com.bigbank.dragons.client.dto.ShopItemDto;
import com.bigbank.dragons.game.ProbabilityEstimator;
import com.bigbank.dragons.game.config.GameProperties;
import com.bigbank.dragons.game.state.GameState;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "game.strategy", havingValue = "expected-value", matchIfMissing = true)
public class ExpectedValueStrategy implements GameStrategy {

  private final GameProperties properties;

  @Override
  public Optional<MessageDto> chooseAd(
          List<MessageDto> ads, GameState state, ProbabilityEstimator estimator) {
    return ads.stream()
            .filter(ad -> estimator.estimate(ad.probability()) >= properties.probabilityFloor())
            .max(Comparator.comparingDouble(ad -> expectedValue(ad, estimator)));
  }

  private double expectedValue(MessageDto ad, ProbabilityEstimator estimator) {
      return ad.reward() * estimator.estimate(ad.probability());
  }

  @Override
  public Optional<ShopItemDto> choosePurchase(List<ShopItemDto> shopItemDtos, GameState state) {
    if (state.getLives() <= properties.lowLivesThreshold()) {
      Optional<ShopItemDto> potion =
              shopItemDtos.stream()
                      .filter(i -> isHealingPotion(i.name()))
                      .filter(i -> i.cost() <= state.getGold())
                      .filter(i -> i.cost() <= properties.healingPotionMaxCost())
                      .min(Comparator.comparingInt(ShopItemDto::cost));
      if (potion.isPresent()) return potion;
    }

    if (state.getGold() >= 300) {
      return shopItemDtos.stream()
              .filter(i -> !isHealingPotion(i.name()))
              .filter(i -> i.cost() <= state.getGold())
              .min(Comparator.comparingInt(ShopItemDto::cost));
    }

    return Optional.empty();
  }

  private static boolean isHealingPotion(String name) {
    return name != null && name.toLowerCase(Locale.ROOT).contains("healing");
  }
}
