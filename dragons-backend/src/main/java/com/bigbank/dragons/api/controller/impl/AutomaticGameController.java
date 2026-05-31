package com.bigbank.dragons.api.controller.impl;

import com.bigbank.dragons.api.controller.AutomaticGameApiTemplate;
import com.bigbank.dragons.api.dto.BatchStatsDto;
import com.bigbank.dragons.api.dto.GameResultDto;
import com.bigbank.dragons.api.mapper.ApiMapper;
import com.bigbank.dragons.service.AutomaticGameRunnerService;
import com.bigbank.dragons.strategy.StrategyRegistry;
import com.bigbank.dragons.strategy.StrategyType;
import java.util.List;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class AutomaticGameController implements AutomaticGameApiTemplate {

  private final AutomaticGameRunnerService automaticGameRunnerService;
  private final ApiMapper apiMapper;
  private final StrategyRegistry strategyRegistry;
  private final ExecutorService batchExecutorService;

  @Value("${emitter.sse-emitter-timeout}")
  private Long sseEmitterTimeout;

  @Override
  public GameResultDto play(String strategy) {
    return apiMapper.toGameResultDto(
        automaticGameRunnerService.playGame(strategyRegistry.resolve(strategy)));
  }

  @Override
  public BatchStatsDto playBatch(int games, String strategy) {
    return apiMapper.toDto(
        automaticGameRunnerService.playBatch(games, strategyRegistry.resolve(strategy)));
  }

  @Override
  public List<String> strategies() {
    return strategyRegistry.available().stream().map(StrategyType::key).toList();
  }

  @Override
  public SseEmitter streamGame(String strategy) {
    SseEmitter emitter = new SseEmitter(sseEmitterTimeout);
    batchExecutorService.submit(
        () ->
            automaticGameRunnerService.playGameStreaming(
                strategyRegistry.resolve(strategy), emitter));
    return emitter;
  }
}
