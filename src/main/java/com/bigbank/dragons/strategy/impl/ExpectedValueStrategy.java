package com.bigbank.dragons.strategy.impl;

import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.domain.ShopItem;
import com.bigbank.dragons.game.config.GameProperties;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.probability.ProbabilityEstimator;
import com.bigbank.dragons.strategy.GameStrategy;
import com.bigbank.dragons.strategy.StrategyType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpectedValueStrategy implements GameStrategy {

  private final GameProperties properties;

  @Override
  public StrategyType type() {
    return StrategyType.EXPECTED_VALUE;
  }

  @Override
  public Message chooseAd(List<Message> ads, GameState state, ProbabilityEstimator estimator) {
    return ads.stream()
        .max(Comparator.comparingDouble(ad -> expectedValue(ad, estimator)))
        .orElseThrow(() -> new IllegalStateException("No tasks available to choose from"));
  }

  private double expectedValue(Message ad, ProbabilityEstimator estimator) {
    return ad.reward() * estimator.estimate(ad);
  }

  @Override
  public List<ShopItem> choosePurchases(List<ShopItem> shopItems, GameState state) {
    List<ShopItem> plan = new ArrayList<>();

    if (state.getLives() <= properties.lowLivesThreshold()) {
      shopItems.stream()
          .filter(i -> isHealingPotion(i.name()))
          .filter(i -> i.cost() <= properties.healingPotionMaxCost())
          .min(Comparator.comparingInt(ShopItem::cost))
          .ifPresent(plan::add);
    }

    int reserve = properties.goldReserve();
    int projectedGold = state.getGold() - plan.stream().mapToInt(ShopItem::cost).sum();

    List<ShopItem> upgrades =
        shopItems.stream()
            .filter(i -> !isHealingPotion(i.name()))
            .sorted(Comparator.comparingInt(ShopItem::cost).reversed())
            .toList();

    for (ShopItem item : upgrades) {
      if (projectedGold - item.cost() >= reserve) {
        plan.add(item);
        projectedGold -= item.cost();
      }
    }
    return plan;
  }

  private static boolean isHealingPotion(String name) {
    return name != null && name.toLowerCase(Locale.ROOT).contains("healing");
  }
}
