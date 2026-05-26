package com.bigbank.dragons.api;

import com.bigbank.dragons.api.dto.GameResultDto;
import com.bigbank.dragons.api.dto.GameStatusDto;
import com.bigbank.dragons.game.GameRunner;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Game", description = "Operations for managing game")
@RequiredArgsConstructor
public class GameController {

  private final GameRunner gameRunner;

  /** Start and play a full game to completion, returning the result + decision log. */
  @PostMapping("/play")
  public GameResultDto play() {
    return gameRunner.playGame();
  }

  /** Peek at whether a run is active and the last completed result. */
  @GetMapping("/status")
  public GameStatusDto status() {
    return gameRunner.status();
  }
}
