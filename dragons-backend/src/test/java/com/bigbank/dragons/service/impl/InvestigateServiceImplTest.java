package com.bigbank.dragons.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bigbank.dragons.client.MugloarClient;
import com.bigbank.dragons.client.dto.ReputationClientDto;
import com.bigbank.dragons.client.mapper.ClientMapper;
import com.bigbank.dragons.domain.Reputation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InvestigateServiceImplTest {

  @Mock private MugloarClient mugloarClient;
  @Mock private ClientMapper mapper;
  @InjectMocks private InvestigateServiceImpl investigateService;

  @Test
  void investigate_ShouldReturnReputation() {
    String gameId = "game-123";
    ReputationClientDto mockDto = mock(ReputationClientDto.class);
    Reputation mockReputation = mock(Reputation.class);

    when(mugloarClient.investigate(gameId)).thenReturn(mockDto);
    when(mapper.toDomain(mockDto)).thenReturn(mockReputation);

    Reputation result = investigateService.investigate(gameId);

    assertEquals(mockReputation, result);
    verify(mugloarClient).investigate(gameId);
    verify(mapper).toDomain(mockDto);
  }
}
