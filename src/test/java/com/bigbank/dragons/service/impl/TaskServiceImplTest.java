package com.bigbank.dragons.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bigbank.dragons.client.MugloarClient;
import com.bigbank.dragons.client.dto.MessageDto;
import com.bigbank.dragons.client.dto.SolveResponseDto;
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

  @Mock
  private DomainValidator domainValidator; // Needed actually but IntelliJ does not pick this up

  @InjectMocks private TaskServiceImpl taskService;

  @Test
  void getTasksShouldReturnFlatMappedDecodedMessages() {
    String gameId = "game-id";
    MessageDto dto = mock(MessageDto.class);
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
    SolveResponseDto resDto = mock(SolveResponseDto.class);
    SolveResponse expectedResponse = mock(SolveResponse.class);

    when(state.getGameId()).thenReturn("game-id");
    when(decoder.decode(ad)).thenReturn(Optional.of(decoded));
    when(decoded.adId()).thenReturn("ad-id");
    when(client.solve("game-id", "ad-id")).thenReturn(resDto);
    when(mapper.toDomain(resDto)).thenReturn(expectedResponse);

    SolveResponse actualResponse = taskService.solve(state, ad);

    assertEquals(expectedResponse, actualResponse);
  }
}
