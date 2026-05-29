package com.bigbank.dragons.domain;

import java.util.Locale;

public record ShopItem(String id, String name, Integer cost) {
  public boolean isHealingPotion() {
    return name != null && name.toLowerCase(Locale.ROOT).contains("healing");
  }
}
