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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LowRiskStrategy implements GameStrategy {

  private final GameProperties properties;

  @Override
  public StrategyType type() {
    return StrategyType.LOW_RISK;
  }

  /** Sort first by highest success probability (lowest risk), then by highest reward */
  @Override
  public Message chooseAd(List<Message> ads, GameState state, ProbabilityEstimator estimator) {
    return ads.stream()
        .max(Comparator.comparingDouble(estimator::estimate).thenComparingDouble(Message::reward))
        .orElseThrow(() -> new IllegalStateException("No tasks available to choose from"));
  }

  /**
   * Risk-averse shopping: heal eagerly (one threshold higher than the default), and only spend on
   * upgrades while keeping a comfortable gold reserve. Healing is exempt from the reserve floor.
   */
  @Override
  public List<ShopItem> choosePurchases(List<ShopItem> shopItems, GameState state) {
    List<ShopItem> plan = new ArrayList<>();

    if (state.getLives() <= properties.lowLivesThreshold() + properties.extraLivesBuffer()) {
      shopItems.stream()
          .filter(ShopItem::isHealingPotion)
          .filter(i -> i.cost() <= properties.healingPotionMaxCost())
          .min(Comparator.comparingInt(ShopItem::cost))
          .ifPresent(plan::add);
    }

    int reserve = properties.goldReserve();
    int projectedGold = state.getGold() - plan.stream().mapToInt(ShopItem::cost).sum();

    List<ShopItem> upgrades =
        shopItems.stream()
            .filter(item -> !item.isHealingPotion())
            .sorted(Comparator.comparingInt(ShopItem::cost))
            .toList();

    for (ShopItem item : upgrades) {
      if (projectedGold - item.cost() >= reserve) {
        plan.add(item);
        projectedGold -= item.cost();
      }
    }
    return plan;
  }
}
