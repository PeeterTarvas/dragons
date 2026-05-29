package com.bigbank.dragons.service.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.game.session.GameSession;
import com.bigbank.dragons.game.state.GameState;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameActionValidatorTest {

  private GameActionValidator validator;

  @BeforeEach
  void setUp() {
    validator = new GameActionValidator();
  }

  @Test
  void validateGameIsActiveDeadGameThrowsException() {
    GameSession session = mock(GameSession.class);
    GameState state = mock(GameState.class);
    when(session.getState()).thenReturn(state);
    when(state.isAlive()).thenReturn(false);

    IllegalStateException ex =
        assertThrows(IllegalStateException.class, () -> validator.validateGameIsActive(session));
    assertEquals("Game over! You have 0 lives remaining. Start a new game.", ex.getMessage());
  }

  @Test
  void validateGameIsActiveAliveGamePasses() {
    GameSession session = mock(GameSession.class);
    GameState state = mock(GameState.class);
    when(session.getState()).thenReturn(state);
    when(state.isAlive()).thenReturn(true);

    assertDoesNotThrow(() -> validator.validateGameIsActive(session));
  }

  @Test
  void validateMessage_NullAvailableMessages_ThrowsException() {
    GameSession session = mock(GameSession.class);
    when(session.getAvailableMessages()).thenReturn(null);

    IllegalStateException ex =
        assertThrows(
            IllegalStateException.class,
            () -> validator.validateMessage(session, mock(Message.class)));
    assertEquals("No tasks available. Please fetch the board first.", ex.getMessage());
  }

  @Test
  void validateMessageEmptyAvailableMessagesThrowsException() {
    GameSession session = mock(GameSession.class);
    when(session.getAvailableMessages()).thenReturn(Collections.emptySet());

    IllegalStateException ex =
        assertThrows(
            IllegalStateException.class,
            () -> validator.validateMessage(session, mock(Message.class)));
    assertEquals("No tasks available. Please fetch the board first.", ex.getMessage());
  }

  @Test
  void validateMessageMessageNotInAvailableThrowsException() {
    GameSession session = mock(GameSession.class);
    Message requestMsg = new Message("id", "msg", 10, 10, 0, "p");
    when(session.getAvailableMessages())
        .thenReturn(Set.of(new Message("other", "msg", 10, 10, 0, "p")));

    IllegalStateException ex =
        assertThrows(
            IllegalStateException.class, () -> validator.validateMessage(session, requestMsg));
    assertEquals("Message not available.", ex.getMessage());
  }

  @Test
  void validateMessageMessageIsAvailable_Passes() {
    GameSession session = mock(GameSession.class);
    Message requestMsg = new Message("id", "msg", 10, 10, 0, "p");
    when(session.getAvailableMessages()).thenReturn(Set.of(requestMsg));

    assertDoesNotThrow(() -> validator.validateMessage(session, requestMsg));
  }
}
