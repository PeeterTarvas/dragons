package com.bigbank.dragons.game.state;

import com.bigbank.dragons.api.dto.TurnLogDto;
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
  private int score;
  private int turn;
  private boolean reachedGoal;

  private final List<TurnLogDto> log = new ArrayList<>();

  public void update(int lives, int gold, int score, int turn) {
    this.lives = lives;
    this.gold = gold;
    this.score = score;
    this.turn = turn;
  }

  public void updateAfterBuy(int gold, int lives, int level, int turn) {
    this.gold = gold;
    this.lives = lives;
    this.level = level;
    this.turn = turn;
  }

  public void markReachedGoal(boolean reached) {
    this.reachedGoal = reached;
  }

  public void addLog(TurnLogDto entry) {
    log.add(entry);
  }

  public boolean isAlive() {
    return lives > 0;
  }
}
