package com.bigbank.dragons.service;

import com.bigbank.dragons.domain.Reputation;
import jakarta.validation.constraints.NotBlank;

public interface InvestigateService {

  Reputation investigate(@NotBlank String gameId);
}
