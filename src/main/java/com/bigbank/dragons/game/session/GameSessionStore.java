package com.bigbank.dragons.game.session;

import com.bigbank.dragons.api.exception.GameNotFoundException;
import com.bigbank.dragons.game.state.GameState;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GameSessionStore {

  private static final Duration TTL = Duration.ofMinutes(30);

  private final Map<String, GameSession> sessions = new ConcurrentHashMap<>();

  public void create(GameState state) {
    GameSession session = new GameSession(state);
    sessions.put(state.getGameId(), session);
  }

  public GameSession get(String gameId) {
    GameSession session = sessions.get(gameId);
    if (session == null) {
      throw new GameNotFoundException("No active game with id: " + gameId);
    }
    session.touch();
    return session;
  }

  public void remove(String gameId) {
    sessions.remove(gameId);
  }

  /** Evicts abandoned sessions so the map doesn't grow unbounded. Runs every 10 minutes. */
  @Scheduled(fixedRate = 10 * 60 * 1000)
  void evictExpired() {
    Instant cutoff = Instant.now().minus(TTL);
    sessions.entrySet().removeIf(e -> e.getValue().getLastAccess().isBefore(cutoff));
  }
}
