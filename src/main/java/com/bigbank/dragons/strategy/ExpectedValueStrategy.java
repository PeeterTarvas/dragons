package com.bigbank.dragons.strategy;

import com.bigbank.dragons.client.dto.Message;
import com.bigbank.dragons.client.dto.ShopItem;
import com.bigbank.dragons.game.ProbabilityEstimator;
import com.bigbank.dragons.game.state.GameState;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ExpectedValueStrategy implements GameStrategy {
  private static final double PROBABILITY_FLOOR = 0.55; // don't gamble below this
  private static final int LOW_LIVES_THRESHOLD = 2; // buy a potion at/below this
  private static final int HEALING_POTION_MAX_COST = 50;

  @Override
  public Optional<Message> chooseAd(
      List<Message> ads, GameState state, ProbabilityEstimator estimator) {
    return ads.stream()
        .filter(ad -> estimator.estimate(ad.probability()) >= PROBABILITY_FLOOR)
        .max(Comparator.comparingDouble(ad -> expectedValue(ad, estimator)));
  }

  private double expectedValue(Message ad, ProbabilityEstimator estimator) {
    double ev = ad.reward() * estimator.estimate(ad.probability());
    if (ad.expiresIn() <= 1) ev *= 0.9;
    return ev;
  }

  @Override
  public Optional<ShopItem> choosePurchase(List<ShopItem> shopItems, GameState state) {
    // Priority 1: heal if low on lives and a potion is affordable.
    if (state.getLives() <= LOW_LIVES_THRESHOLD) {
      Optional<ShopItem> potion =
          shopItems.stream()
              .filter(i -> isHealingPotion(i.name()))
              .filter(i -> i.cost() <= state.getGold())
              .filter(i -> i.cost() <= HEALING_POTION_MAX_COST)
              .min(Comparator.comparingInt(ShopItem::cost));
      if (potion.isPresent()) return potion;
    }

    // Priority 2: with comfortable gold, buy the cheapest upgrade to raise level.
    if (state.getGold() >= 300) {
      return shopItems.stream()
          .filter(i -> !isHealingPotion(i.name()))
          .filter(i -> i.cost() <= state.getGold())
          .min(Comparator.comparingInt(ShopItem::cost));
    }

    return Optional.empty();
  }

  private static boolean isHealingPotion(String name) {
    return name != null && name.toLowerCase(Locale.ROOT).contains("healing");
  }
}
