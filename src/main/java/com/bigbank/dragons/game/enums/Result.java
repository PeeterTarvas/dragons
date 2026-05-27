package com.bigbank.dragons.game.enums;

public enum Result {
  WIN("WIN"),
  LOSS("LOSS"); // Semicolon required here

  private final String label;

  // Constructor required to map the string to the field
  Result(String label) {
    this.label = label;
  }

  // You likely want a getter here too (or use Lombok's @Getter)
  public String getLabel() {
    return label;
  }
}
