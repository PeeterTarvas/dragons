package com.bigbank.dragons.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bigbank.dragons.client.MugloarClient;
import com.bigbank.dragons.client.dto.StartGameResponseClientDto;
import com.bigbank.dragons.client.mapper.GameStateMapper;
import com.bigbank.dragons.game.state.GameState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GameServiceImplTest {

  @Mock private MugloarClient client;
  @Mock private GameStateMapper gameStateMapper;
  @InjectMocks private GameServiceImpl gameService;

  @Test
  void startShouldReturnMappedGameState() {
    StartGameResponseClientDto mockDto = mock(StartGameResponseClientDto.class);
    GameState mockState = mock(GameState.class);

    when(client.startGame()).thenReturn(mockDto);
    when(gameStateMapper.toEntity(mockDto)).thenReturn(mockState);

    GameState result = gameService.start();

    assertEquals(mockState, result);
    verify(client).startGame();
    verify(gameStateMapper).toEntity(mockDto);
  }
}
