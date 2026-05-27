package com.bigbank.dragons.game.session;

import static org.junit.jupiter.api.Assertions.*;

import com.bigbank.dragons.api.exception.GameNotFoundException;
import com.bigbank.dragons.game.state.GameState;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class GameSessionStoreTest {

  private GameSessionStore store;
  private GameState mockState;

  @BeforeEach
  void setUp() {
    store = new GameSessionStore();
    mockState = new GameState("game-123", 3, 0, 1, 0, 0, false);
  }

  @Test
  void createAndGetRetrievesSessionSuccessfully() {
    store.create(mockState);
    GameSession session = store.get("game-123");

    assertNotNull(session);
    assertEquals(mockState, session.getState());
  }

  @Test
  void getThrowsExceptionWhenSessionDoesNotExist() {
    assertThrows(GameNotFoundException.class, () -> store.get("invalid-id"));
  }

  @Test
  void removeDeletesSession() {
    store.create(mockState);
    store.remove("game-123");

    assertThrows(GameNotFoundException.class, () -> store.get("game-123"));
  }

  @Test
  void evictExpiredRemovesSessionsOlderThan30Minutes() {
    store.create(mockState);
    GameSession session = store.get("game-123");

    ReflectionTestUtils.setField(
        session, "lastAccess", Instant.now().minus(31, ChronoUnit.MINUTES));

    store.evictExpired();

    assertThrows(
        GameNotFoundException.class,
        () -> store.get("game-123"),
        "Expired session should be evicted");
  }

  @Test
  void evictExpiredKeepsActiveSessions() {
    store.create(mockState);
    GameSession session = store.get("game-123");

    ReflectionTestUtils.setField(
        session, "lastAccess", Instant.now().minus(29, ChronoUnit.MINUTES));

    store.evictExpired();

    assertDoesNotThrow(() -> store.get("game-123"), "Active session should not be evicted");
  }
}
