package com.bigbank.dragons.service;


import com.bigbank.dragons.client.dto.ReputationDto;

public interface InvestigateService {

    ReputationDto investigate(String gameId);

    double calculateScore(String gameId);
}
