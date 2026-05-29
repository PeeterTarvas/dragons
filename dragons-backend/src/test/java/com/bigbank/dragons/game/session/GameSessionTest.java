package com.bigbank.dragons.game.session;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.game.state.GameState;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameSessionTest {

  private GameState mockState;

  @BeforeEach
  void setUp() {
    mockState = new GameState("game-123", 3, 0, 1, 0, 0, false);
  }

  @Test
  void updateAvailableMessagesUpdatesSetAndTouchSession() {
    GameSession session = new GameSession(mockState);
    Instant beforeUpdate = session.getLastAccess();

    Message mockMessage = new Message("ad-1", "Save the dragon", 100, 10, 0, "Sure thing");
    session.updateAvailableMessages(List.of(mockMessage));

    assertEquals(1, session.getAvailableMessages().size());
    assertTrue(session.getAvailableMessages().contains(mockMessage));
    assertFalse(session.getLastAccess().isBefore(beforeUpdate));
  }

  @Test
  void constructorInitialisesFieldsCorrectly() {
    GameState state = mock(GameState.class);
    GameSession session = new GameSession(state);

    assertSame(state, session.getState());
    assertNotNull(session.getEstimator());
    assertNotNull(session.getLastAccess());
    assertTrue(session.getAvailableMessages().isEmpty());
  }

  @Test
  void touchAdvancesLastAccess() throws InterruptedException {
    GameSession session = new GameSession(mock(GameState.class));
    Instant before = session.getLastAccess();
    Thread.sleep(5);
    session.touch();
    assertTrue(session.getLastAccess().isAfter(before));
  }
}
