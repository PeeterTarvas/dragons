package com.bigbank.dragons.service.impl;

import com.bigbank.dragons.client.MugloarClient;
import com.bigbank.dragons.client.dto.ReputationDto;
import com.bigbank.dragons.service.InvestigateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvestigateServiceImpl implements InvestigateService {

    private final MugloarClient mugloarClient;

    @Override
    public ReputationDto investigate(String gameId) {
        return mugloarClient.investigate(gameId);
    }

    @Override
    public double calculateScore(String gameId) {
        ReputationDto reputationDto = investigate(gameId);
        return  reputationDto.state() + reputationDto.people() + reputationDto.underworld();
    }
}
