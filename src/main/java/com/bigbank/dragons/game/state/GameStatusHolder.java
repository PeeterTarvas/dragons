package com.bigbank.dragons.game.state;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Component;

@Component
public class GameStatusHolder {

  private final AtomicInteger running = new AtomicInteger();
  private final AtomicReference<GameState> last = new AtomicReference<>();

  public void gameStarted() {
    running.incrementAndGet();
  }

  public void gameFinished(GameState state) {
    running.decrementAndGet();
    last.set(state);
  }

  public int running() {
    return running.get();
  }

  public GameState last() {
    return last.get();
  }
}
