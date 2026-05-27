package com.bigbank.dragons.strategy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.domain.ShopItem;
import com.bigbank.dragons.game.config.GameProperties;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.probability.Probability;
import com.bigbank.dragons.probability.ProbabilityEstimator;
import com.bigbank.dragons.strategy.StrategyType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LowRiskStrategyTest {

  @Mock private GameProperties properties;
  @Mock private GameState gameState;
  @Mock private ProbabilityEstimator estimator;

  @InjectMocks private LowRiskStrategy strategy;

  @Test
  void type_IsLowRisk() {
    assertEquals(StrategyType.LOW_RISK, strategy.type());
  }

  @Test
  void chooseAdPicksSafestAdTiesBrokenByReward() {
    Message ad1 = new Message("1", "ad1", 100, 10, 0, Probability.PIECE_OF_CAKE.label());
    Message ad2 = new Message("2", "ad2", 200, 10, 0, Probability.RISKY.label());
    Message ad3 = new Message("3", "ad3", 150, 10, 0, Probability.PIECE_OF_CAKE.label());

    when(estimator.estimate(ad1)).thenReturn(0.9);
    when(estimator.estimate(ad2)).thenReturn(0.3);
    when(estimator.estimate(ad3)).thenReturn(0.9);

    Message chosen = strategy.chooseAd(List.of(ad1, ad2, ad3), gameState, estimator);

    assertEquals(ad3, chosen);
  }

  @Test
  void choosePurchasesHealsEarlierThanExpectedValueStrategy() {
    when(properties.lowLivesThreshold()).thenReturn(2);
    when(properties.healingPotionMaxCost()).thenReturn(50);
    when(properties.goldReserve()).thenReturn(0);

    when(gameState.getLives()).thenReturn(3);
    when(gameState.getGold()).thenReturn(100);

    ShopItem potion = new ShopItem("p1", "Healing potion", 50);
    List<ShopItem> plan = strategy.choosePurchases(List.of(potion), gameState);

    assertEquals(1, plan.size());
    assertEquals(potion, plan.getFirst());
  }

  @Test
  void choosePurchasesBuysCheapestUpgradesFirstRespectingReserve() {
    when(properties.lowLivesThreshold()).thenReturn(1);
    when(properties.goldReserve()).thenReturn(100);

    when(gameState.getLives()).thenReturn(5);
    when(gameState.getGold()).thenReturn(200);

    ShopItem cheapUpgrade = new ShopItem("u1", "Claw Sharpening", 60);
    ShopItem expensiveUpgrade = new ShopItem("u2", "Gasoline", 120);

    List<ShopItem> plan =
        strategy.choosePurchases(List.of(cheapUpgrade, expensiveUpgrade), gameState);

    assertEquals(1, plan.size());
    assertEquals(cheapUpgrade, plan.getFirst());
  }
}
