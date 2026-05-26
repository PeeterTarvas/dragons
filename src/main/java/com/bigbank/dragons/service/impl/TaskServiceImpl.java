package com.bigbank.dragons.service.impl;

import com.bigbank.dragons.client.MugloarClient;
import com.bigbank.dragons.client.dto.MessageDto;
import com.bigbank.dragons.client.dto.SolveResponseDto;
import com.bigbank.dragons.decoder.AdDecoder;
import com.bigbank.dragons.game.ProbabilityEstimator;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.service.TaskService;
import com.bigbank.dragons.strategy.GameStrategy;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

  private final MugloarClient client;
  private final AdDecoder decoder;
  private final GameStrategy strategy;

  @Override
  public List<MessageDto> getTasks(String gameId) {
    return client.getMessages(gameId).stream().map(decoder::decode).toList();
  }

  @Override
  public Optional<MessageDto> chooseTask(
      List<MessageDto> ads, GameState state, ProbabilityEstimator estimator) {
    return strategy.chooseAd(ads, state, estimator);
  }

  @Override
  public SolveResponseDto solve(GameState state, MessageDto ad) {
    return client.solve(state.getGameId(), ad.adId());
  }
}
