package com.bigbank.dragons.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bigbank.dragons.client.MugloarClient;
import com.bigbank.dragons.client.dto.MessageClientDto;
import com.bigbank.dragons.client.dto.SolveResponseClientDto;
import com.bigbank.dragons.client.mapper.ClientMapper;
import com.bigbank.dragons.decoder.AdDecoder;
import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.domain.SolveResponse;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.probability.ProbabilityEstimator;
import com.bigbank.dragons.service.validation.DomainValidator;
import com.bigbank.dragons.strategy.GameStrategy;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

  @Mock private MugloarClient client;
  @Mock private AdDecoder decoder;
  @Mock private ClientMapper mapper;

  @Mock private DomainValidator domainValidator;

  @InjectMocks private TaskServiceImpl taskService;

  @Test
  void getTasksShouldReturnFlatMappedDecodedMessages() {
    String gameId = "game-id";
    MessageClientDto dto = mock(MessageClientDto.class);
    Message domainMsg = mock(Message.class);
    Message decodedMsg = mock(Message.class);

    when(client.getMessages(gameId)).thenReturn(List.of(dto));
    when(mapper.toDomain(dto)).thenReturn(domainMsg);
    when(decoder.decode(domainMsg)).thenReturn(Optional.of(decodedMsg));

    List<Message> results = taskService.getTasks(gameId);

    assertEquals(1, results.size());
    assertEquals(decodedMsg, results.getFirst());
  }

  @Test
  void chooseTaskNullStrategyOrEstimatorShouldThrowNullPointerException() {
    GameState state = mock(GameState.class);
    ProbabilityEstimator estimator = mock(ProbabilityEstimator.class);

    assertThrows(
        NullPointerException.class,
        () -> taskService.chooseTask(List.of(), state, estimator, null));
    assertThrows(
        NullPointerException.class,
        () -> taskService.chooseTask(List.of(), state, null, mock(GameStrategy.class)));
  }

  @Test
  void chooseTaskNullOrEmptyAdsShouldThrowIllegalArgumentException() {
    GameState state = mock(GameState.class);
    ProbabilityEstimator estimator = mock(ProbabilityEstimator.class);
    GameStrategy strategy = mock(GameStrategy.class);

    assertThrows(
        IllegalArgumentException.class,
        () -> taskService.chooseTask(null, state, estimator, strategy));
    assertThrows(
        IllegalArgumentException.class,
        () -> taskService.chooseTask(Collections.emptyList(), state, estimator, strategy));
  }

  @Test
  void solveUncheckedAdDecodingShouldThrowIllegalArgumentException() {
    GameState state = mock(GameState.class);
    Message ad = mock(Message.class);
    when(decoder.decode(ad)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> taskService.solve(state, ad));
  }

  @Test
  void solveValidAdShouldExecuteAndReturnSolveResponse() {
    GameState state = mock(GameState.class);
    Message ad = new Message("ad-id", "1", 1, 1, 0, "0.9");
    Message decoded = mock(Message.class);
    SolveResponseClientDto resDto = mock(SolveResponseClientDto.class);
    SolveResponse expectedResponse = mock(SolveResponse.class);

    when(state.getGameId()).thenReturn("game-id");
    when(decoder.decode(ad)).thenReturn(Optional.of(decoded));
    when(decoded.adId()).thenReturn("ad-id");
    when(client.solve("game-id", "ad-id")).thenReturn(resDto);
    when(mapper.toDomain(resDto)).thenReturn(expectedResponse);

    SolveResponse actualResponse = taskService.solve(state, ad);

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void chooseTaskValidInputsDecodesAdsAndDelegatesToStrategy() {
    GameState state = mock(GameState.class);
    ProbabilityEstimator estimator = mock(ProbabilityEstimator.class);
    GameStrategy strategy = mock(GameStrategy.class);

    Message rawAd1 = mock(Message.class);
    Message rawAd2 = mock(Message.class);
    Message decodedAd1 = mock(Message.class);

    List<Message> rawAds = List.of(rawAd1, rawAd2);

    when(decoder.decode(rawAd1)).thenReturn(Optional.of(decodedAd1));
    when(decoder.decode(rawAd2)).thenReturn(Optional.empty());

    Message expectedChosenAd = mock(Message.class);
    when(strategy.chooseAd(List.of(decodedAd1), state, estimator)).thenReturn(expectedChosenAd);

    Message result = taskService.chooseTask(rawAds, state, estimator, strategy);

    assertEquals(expectedChosenAd, result);
    verify(domainValidator).validate(state);
    verify(decoder).decode(rawAd1);
    verify(decoder).decode(rawAd2);
  }
}
