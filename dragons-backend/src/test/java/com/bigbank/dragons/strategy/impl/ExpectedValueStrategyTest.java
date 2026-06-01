package com.bigbank.dragons.strategy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.domain.ShopItem;
import com.bigbank.dragons.game.config.GameProperties;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.probability.Probability;
import com.bigbank.dragons.probability.ProbabilityEstimator;
import com.bigbank.dragons.strategy.StrategyType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ExpectedValueStrategyTest {

  @Mock private GameProperties properties;
  @Mock private GameState gameState;
  @Mock private ProbabilityEstimator estimator;

  @InjectMocks private ExpectedValueStrategy strategy;

  private Message ad1;
  private Message ad2;

  @BeforeEach
  void setUp() {
    ad1 = new Message("1", "ad1", 100, 10, 0, Probability.HMMM.label());
    ad2 = new Message("2", "ad2", 200, 10, 0, Probability.RATHER_DETRIMENTAL.label());
  }

  @Test
  void typeIsExpectedValue() {
    assertEquals(StrategyType.EXPECTED_VALUE, strategy.type());
  }

  @Test
  void chooseAdPicksAdWithHighestExpectedValue() {
    when(estimator.estimate(ad1)).thenReturn(0.5);
    when(estimator.estimate(ad2)).thenReturn(0.3);

    Message chosen = strategy.chooseAd(List.of(ad1, ad2), gameState, estimator);

    assertEquals(ad2, chosen);
  }

  @Test
  void choosePurchasesBuysHealingWhenLivesAtThreshold() {
    when(properties.lowLivesThreshold()).thenReturn(3);
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
  void choosePurchasesBuysMostExpensiveUpgradesFirstRespectingReserve() {
    when(properties.lowLivesThreshold()).thenReturn(1);
    when(properties.goldReserve()).thenReturn(100);

    when(gameState.getLives()).thenReturn(5);
    when(gameState.getGold()).thenReturn(300);

    ShopItem cheapUpgrade = new ShopItem("u1", "Claw Sharpening", 50);
    ShopItem expensiveUpgrade = new ShopItem("u2", "Gasoline", 150);

    List<ShopItem> plan =
        strategy.choosePurchases(List.of(cheapUpgrade, expensiveUpgrade), gameState);

    assertEquals(2, plan.size());
    assertEquals(expensiveUpgrade, plan.get(0));
    assertEquals(cheapUpgrade, plan.get(1));
  }

  @Test
  void chooseAdThrowsWhenNoAdsAvailable() {
    assertThrows(
        IllegalStateException.class, () -> strategy.chooseAd(List.of(), gameState, estimator));
  }

  @Test
  void choosePurchasesSkipsHealingWhenPotionExceedsMaxCost() {
    when(properties.lowLivesThreshold()).thenReturn(3);
    when(properties.healingPotionMaxCost()).thenReturn(50);
    when(properties.goldReserve()).thenReturn(0);
    when(gameState.getLives()).thenReturn(2);
    when(gameState.getGold()).thenReturn(100);

    ShopItem dearPotion = new ShopItem("p1", "Healing potion", 80);

    List<ShopItem> plan = strategy.choosePurchases(List.of(dearPotion), gameState);

    assertEquals(0, plan.size());
  }

  @Test
  void choosePurchasesSkipsUpgradeThatWouldBreachReserve() {
    when(properties.lowLivesThreshold()).thenReturn(1);
    when(properties.goldReserve()).thenReturn(100);
    when(gameState.getLives()).thenReturn(5);
    when(gameState.getGold()).thenReturn(120);

    ShopItem upgrade = new ShopItem("u1", "Claw Sharpening", 50);

    List<ShopItem> plan = strategy.choosePurchases(List.of(upgrade), gameState);

    assertEquals(0, plan.size());
  }
}
