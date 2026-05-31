package com.bigbank.dragons.client;

import com.bigbank.dragons.client.annotation.MugloarRetry;
import com.bigbank.dragons.client.dto.BuyResponseClientDto;
import com.bigbank.dragons.client.dto.MessageClientDto;
import com.bigbank.dragons.client.dto.ReputationClientDto;
import com.bigbank.dragons.client.dto.ShopItemClientDto;
import com.bigbank.dragons.client.dto.SolveResponseClientDto;
import com.bigbank.dragons.client.dto.StartGameResponseClientDto;
import com.bigbank.dragons.client.exception.MugloarApiException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
@MugloarRetry
public class MugloarClient {

  private final RestClient restClient;

  public StartGameResponseClientDto startGame() {
    return Optional.ofNullable(
            restClient
                .post()
                .uri("/api/v2/game/start")
                .retrieve()
                .body(StartGameResponseClientDto.class))
        .orElseThrow(() -> new MugloarApiException("Start game returned an empty response"));
  }

  public ReputationClientDto investigate(String gameId) {
    requireNotBlank(gameId, "gameId");
    return Optional.ofNullable(
            restClient
                .post()
                .uri("/api/v2/{gameId}/investigate/reputation", gameId)
                .retrieve()
                .body(ReputationClientDto.class))
        .orElseThrow(
            () ->
                new MugloarApiException(
                    "Investigation returned an empty response for game " + gameId));
  }

  public List<MessageClientDto> getMessages(String gameId) {
    requireNotBlank(gameId, "gameId");
    return Optional.ofNullable(
            restClient
                .get()
                .uri("/api/v2/{gameId}/messages", gameId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<MessageClientDto>>() {}))
        .orElseThrow(
            () -> new MugloarApiException("Messageboard returned no body for game " + gameId));
  }

  public SolveResponseClientDto solve(String gameId, String adId) {
    requireNotBlank(gameId, "gameId");
    requireNotBlank(adId, "adId");
    return Optional.ofNullable(
            restClient
                .post()
                .uri("/api/v2/{gameId}/solve/{adId}", gameId, adId)
                .retrieve()
                .body(SolveResponseClientDto.class))
        .orElseThrow(
            () -> new MugloarApiException("Solve returned an empty response for ad " + adId));
  }

  public List<ShopItemClientDto> getShop(String gameId) {
    requireNotBlank(gameId, "gameId");
    return Optional.ofNullable(
            restClient
                .get()
                .uri("/api/v2/{gameId}/shop", gameId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ShopItemClientDto>>() {}))
        .orElseThrow(() -> new MugloarApiException("Shop returned no items for game " + gameId));
  }

  public BuyResponseClientDto buy(String gameId, String itemId) {
    requireNotBlank(gameId, "gameId");
    requireNotBlank(itemId, "itemId");
    return Optional.ofNullable(
            restClient
                .post()
                .uri("/api/v2/{gameId}/shop/buy/{itemId}", gameId, itemId)
                .retrieve()
                .body(BuyResponseClientDto.class))
        .orElseThrow(
            () ->
                new MugloarApiException("Purchase returned an empty response for item " + itemId));
  }

  // If any more validation methods are required then I would do a separate validator
  private static void requireNotBlank(String value, String name) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(name + " must not be blank");
    }
  }
}
