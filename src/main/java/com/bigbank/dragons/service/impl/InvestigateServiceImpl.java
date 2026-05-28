package com.bigbank.dragons.service.impl;

import com.bigbank.dragons.client.MugloarClient;
import com.bigbank.dragons.client.mapper.ClientMapper;
import com.bigbank.dragons.domain.Reputation;
import com.bigbank.dragons.service.InvestigateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class InvestigateServiceImpl implements InvestigateService {

  private final MugloarClient mugloarClient;
  private final ClientMapper mapper;

  @Override
  public Reputation investigate(String gameId) {
    return mapper.toDomain(mugloarClient.investigate(gameId));
  }
}
