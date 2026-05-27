package com.bigbank.dragons.service.validation;

import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.game.session.GameSession;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class GameActionValidator {

  public void validateGameIsActive(GameSession session) {
    if (!session.getState().isAlive()) {
      throw new IllegalStateException("Game over! You have 0 lives remaining. Start a new game.");
    }
  }

  public Message validateAndGetAd(GameSession session, String adId) {
    if (Optional.ofNullable(session.getLastBoard()).isEmpty() || session.getLastBoard().isEmpty()) {
      throw new IllegalStateException("No tasks available. Please fetch the board first.");
    }

    return session.getLastBoard().stream()
        .filter(m -> m.adId().equals(adId))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Task '" + adId + "' is not on the current board for this game."));
  }
}
