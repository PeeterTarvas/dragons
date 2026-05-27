package com.bigbank.dragons.service;

import com.bigbank.dragons.domain.Reputation;

public interface InvestigateService {

  Reputation investigate(String gameId);

  double calculateReputation(String gameId);
}
