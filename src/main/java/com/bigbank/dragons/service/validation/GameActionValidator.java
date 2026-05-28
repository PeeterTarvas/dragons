package com.bigbank.dragons.service.validation;

import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.game.session.GameSession;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameActionValidator {

  public void validateGameIsActive(GameSession session) {
    if (!session.getState().isAlive()) {
      throw new IllegalStateException("Game over! You have 0 lives remaining. Start a new game.");
    }
  }

  public void validateMessage(GameSession session, Message message) {
    if (Optional.ofNullable(session.getAvailableMessages()).isEmpty()
        || session.getAvailableMessages().isEmpty()) {
      throw new IllegalStateException("No tasks available. Please fetch the board first.");
    }
    if (!session.getAvailableMessages().contains(message)) {
      throw new IllegalStateException("Message not available.");
    }
  }
}
