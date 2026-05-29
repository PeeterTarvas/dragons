package com.bigbank.dragons.service.impl;

import com.bigbank.dragons.client.MugloarClient;
import com.bigbank.dragons.client.mapper.ClientMapper;
import com.bigbank.dragons.decoder.AdDecoder;
import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.domain.SolveResponse;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.probability.ProbabilityEstimator;
import com.bigbank.dragons.service.TaskService;
import com.bigbank.dragons.service.validation.DomainValidator;
import com.bigbank.dragons.strategy.GameStrategy;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

  private final MugloarClient client;
  private final AdDecoder decoder;
  private final ClientMapper mapper;
  private final DomainValidator domainValidator;

  @Override
  public List<Message> getTasks(String gameId) {
    return client.getMessages(gameId).stream()
        .map(mapper::toDomain)
        .flatMap(m -> decoder.decode(m).stream())
        .toList();
  }

  @Override
  public Message chooseTask(
      List<Message> ads, GameState state, ProbabilityEstimator estimator, GameStrategy strategy) {
    Objects.requireNonNull(strategy, "GameStrategy cannot be null");
    Objects.requireNonNull(estimator, "ProbabilityEstimator cannot be null");
    domainValidator.validate(state);
    if (ads == null || ads.isEmpty()) {
      throw new IllegalArgumentException("Cannot choose a task from an empty or null list of ads");
    }
    return strategy.chooseAd(
        ads.stream().flatMap(m -> decoder.decode(m).stream()).toList(), state, estimator);
  }

  @Override
  public SolveResponse solve(GameState state, Message ad) {
    domainValidator.validate(state);
    domainValidator.validate(ad);
    String adId =
        decoder
            .decode(ad)
            .map(Message::adId)
            .orElseThrow(() -> new IllegalArgumentException("Cannot decode ad for solving"));
    return mapper.toDomain(client.solve(state.getGameId(), adId));
  }
}
