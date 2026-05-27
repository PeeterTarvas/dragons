package com.bigbank.dragons.game.session;

import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.probability.ProbabilityEstimator;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
public class GameSession {

  private final GameState state;
  private final ProbabilityEstimator estimator;
  private volatile Instant lastAccess;

  @Setter private volatile List<Message> lastBoard = List.of();

  public GameSession(GameState state) {
    this.state = state;
    this.estimator = new ProbabilityEstimator();
    this.lastAccess = Instant.now();
  }

  public void touch() {
    this.lastAccess = Instant.now();
  }
}
