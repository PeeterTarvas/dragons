package com.bigbank.dragons.api;

import com.bigbank.dragons.api.dto.BatchStatsDto;
import com.bigbank.dragons.api.dto.GameResultDto;
import com.bigbank.dragons.client.dto.ReputationDto;
import com.bigbank.dragons.mapper.GameResultMapper;
import com.bigbank.dragons.service.GameRunnerService;
import com.bigbank.dragons.service.InvestigateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Game", description = "Operations for managing game")
@RequiredArgsConstructor
public class GameController {

  private final GameRunnerService gameRunnerService;
  private final InvestigateService investigateService;
  private final GameResultMapper gameResultMapper;

  /** Play a single game to completion and return the result + decision log. */
  @PostMapping("/play")
  public GameResultDto play() {
    return gameResultMapper.toDto(gameRunnerService.playGame());
  }

  /** Run N games concurrently and return aggregate statistics (proves the 1000+ claim). */
  @PostMapping("/play/batch")
  public BatchStatsDto playBatch(@RequestParam(defaultValue = "50") @Min(1) @Max(500) int games) {
    return gameRunnerService.playBatch(games);
  }

  /** Investigate reputation for a given game. */
  @GetMapping("/investigate/{gameId}")
  public ReputationDto investigate(@PathVariable @NotBlank String gameId) {
    return investigateService.investigate(gameId);
  }
}
