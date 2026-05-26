package com.bigbank.dragons.game.state;

import com.bigbank.dragons.api.dto.TurnLogDto;
import com.bigbank.dragons.client.dto.StartGameResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class GameState {

  private final String gameId;
  private int lives;
  private int gold;
  private int level;
  private int score;
  private int turn;
  private final List<TurnLogDto> log = new ArrayList<>();

  public GameState(StartGameResponse start) {
    this.gameId = start.gameId();
    this.lives = start.lives();
    this.gold = start.gold();
    this.level = start.level();
    this.score = start.score();
    this.turn = start.turn();
  }

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

  public void addLog(TurnLogDto entry) {
    log.add(entry);
  }

  public boolean isAlive() {
    return lives > 0;
  }
}
