package com.bigbank.dragons.service.impl;

import com.bigbank.dragons.client.MugloarClient;
import com.bigbank.dragons.client.mapper.ClientMapper;
import com.bigbank.dragons.decoder.AdDecoder;
import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.domain.SolveResponse;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.probability.ProbabilityEstimator;
import com.bigbank.dragons.service.TaskService;
import com.bigbank.dragons.strategy.GameStrategy;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

  private final MugloarClient client;
  private final AdDecoder decoder;
  private final ClientMapper mapper;

  @Override
  public List<Message> getTasks(String gameId) {
    return client.getMessages(gameId).stream()
        .map(mapper::toDomain)
        .map(decoder::decode)
        .filter(Objects::nonNull)
        .toList();
  }

  @Override
  public Message chooseTask(
      List<Message> ads, GameState state, ProbabilityEstimator estimator, GameStrategy strategy) {
    return strategy.chooseAd(ads, state, estimator);
  }

  @Override
  public SolveResponse solve(GameState state, Message ad) {
    return mapper.toDomain(client.solve(state.getGameId(), ad.adId()));
  }
}
