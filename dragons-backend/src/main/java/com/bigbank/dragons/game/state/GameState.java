package com.bigbank.dragons.game.state;

import com.bigbank.dragons.domain.BuyResponse;
import com.bigbank.dragons.domain.TurnLog;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameState {

  private final String gameId;
  private int lives;
  private int gold;
  private int level;
  private double score;
  private int turn;
  private boolean reachedGoal;

  private final List<TurnLog> log = new ArrayList<>();

  public void update(int lives, int gold, double score, int turn) {
    this.lives = lives;
    this.gold = gold;
    this.score = score;
    this.turn = turn;
  }

  public void updateAfterBuy(BuyResponse buyResponse) {
    this.gold = buyResponse.gold();
    this.lives = buyResponse.lives();
    this.level = buyResponse.level();
    this.turn = buyResponse.turn();
  }

  public void markReachedGoal(boolean reached) {
    this.reachedGoal = reached;
  }

  public void addLog(TurnLog entry) {
    log.add(entry);
  }

  public boolean isAlive() {
    return lives > 0;
  }
}
