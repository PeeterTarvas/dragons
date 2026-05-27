package com.bigbank.dragons.game.enums;

import lombok.Getter;

@Getter
public enum Result {
  WIN("WIN"),
  LOSS("LOSS");

  private final String label;

  Result(String label) {
    this.label = label;
  }
}
