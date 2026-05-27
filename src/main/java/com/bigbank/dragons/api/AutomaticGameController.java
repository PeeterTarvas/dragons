package com.bigbank.dragons.api;

import com.bigbank.dragons.api.dto.BatchStatsDto;
import com.bigbank.dragons.api.dto.GameResultDto;
import com.bigbank.dragons.api.mapper.ApiMapper;
import com.bigbank.dragons.api.mapper.GameResultMapper;
import com.bigbank.dragons.service.AutomaticGameRunnerService;
import com.bigbank.dragons.strategy.StrategyRegistry;
import com.bigbank.dragons.strategy.StrategyType;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Game", description = "Operations for managing game")
@RequiredArgsConstructor
public class AutomaticGameController {

  private final AutomaticGameRunnerService automaticGameRunnerService;
  private final GameResultMapper gameResultMapper;
  private final ApiMapper apiMapper;
  private final StrategyRegistry strategyRegistry;

  @PostMapping("/play")
  public GameResultDto play(@RequestParam(required = false) String strategy) {
    StrategyType type = StrategyType.fromKey(strategy).orElse(StrategyType.EXPECTED_VALUE);
    return gameResultMapper.toDto(automaticGameRunnerService.playGame(type));
  }

  @PostMapping("/play/batch")
  public BatchStatsDto playBatch(
      @RequestParam(defaultValue = "3") @Min(1) @Max(500) int games,
      @RequestParam(required = false) String strategy) {
    StrategyType type = StrategyType.fromKey(strategy).orElse(StrategyType.EXPECTED_VALUE);
    return apiMapper.toDto(automaticGameRunnerService.playBatch(games, type));
  }

  @GetMapping("/strategies")
  public List<String> strategies() {
    return strategyRegistry.available().stream().map(StrategyType::key).toList();
  }
}
