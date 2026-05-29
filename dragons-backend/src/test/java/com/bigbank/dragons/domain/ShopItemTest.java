package com.bigbank.dragons.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ShopItemTest {

  @Test
  void isHealingPotionReturnsTrueWhenNameContainsHealing() {
    assertTrue(new ShopItem("1", "Healing Potion", 50).isHealingPotion());
  }

  @Test
  void isHealingPotionReturnsFalseWhenNameDoesNotContainHealing() {
    assertFalse(new ShopItem("2", "Sword", 30).isHealingPotion());
  }

  @Test
  void isHealingPotionReturnsFalseWhenNameIsNull() {
    assertFalse(new ShopItem("3", null, 20).isHealingPotion());
  }

  @Test
  void isHealingPotionIsCaseInsensitive() {
    assertTrue(new ShopItem("4", "HEALING ELIXIR", 40).isHealingPotion());
  }
}
