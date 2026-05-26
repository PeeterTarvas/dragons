package com.bigbank.dragons.api;

import com.bigbank.dragons.api.dto.GameResultDto;
import com.bigbank.dragons.api.dto.GameStatusDto;
import com.bigbank.dragons.mapper.GameResultMapper;
import com.bigbank.dragons.mapper.GameStateMapper;
import com.bigbank.dragons.service.GameRunnerService;
import io.swagger.v3.oas.annotations.tags.Tag;
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

  private final GameRunnerService gameRunnerService;
  private final GameResultMapper gameResultMapper;

  /** Start and play a full game to completion, returning the result + decision log. */
  @PostMapping("/play")
  public GameResultDto play() {
    return gameResultMapper.toDto(gameRunnerService.playGame());
  }
}
