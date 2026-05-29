package com.bigbank.dragons.game.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ResultTest {

  @Test
  void winLabelIsWin() {
    assertEquals("WIN", Result.WIN.getLabel());
  }

  @Test
  void lossLabelIsLoss() {
    assertEquals("LOSS", Result.LOSS.getLabel());
  }
}
