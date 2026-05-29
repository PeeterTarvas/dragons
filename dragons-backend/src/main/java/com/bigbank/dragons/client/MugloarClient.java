package com.bigbank.dragons.client;

import com.bigbank.dragons.client.annotation.MugloarRetry;
import com.bigbank.dragons.client.dto.BuyResponseDto;
import com.bigbank.dragons.client.dto.MessageDto;
import com.bigbank.dragons.client.dto.ReputationDto;
import com.bigbank.dragons.client.dto.ShopItemDto;
import com.bigbank.dragons.client.dto.SolveResponseDto;
import com.bigbank.dragons.client.dto.StartGameResponseDto;
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

  public StartGameResponseDto startGame() {
    return Optional.ofNullable(
            restClient.post().uri("/api/v2/game/start").retrieve().body(StartGameResponseDto.class))
        .orElseThrow(() -> new MugloarApiException("Start game returned an empty response"));
  }

  public ReputationDto investigate(String gameId) {
    requireNotBlank(gameId, "gameId");
    return Optional.ofNullable(
            restClient
                .post()
                .uri("/api/v2/{gameId}/investigate/reputation", gameId)
                .retrieve()
                .body(ReputationDto.class))
        .orElseThrow(
            () ->
                new MugloarApiException(
                    "Investigation returned an empty response for game " + gameId));
  }

  public List<MessageDto> getMessages(String gameId) {
    requireNotBlank(gameId, "gameId");
    return Optional.ofNullable(
            restClient
                .get()
                .uri("/api/v2/{gameId}/messages", gameId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<MessageDto>>() {}))
        .orElseThrow(
            () -> new MugloarApiException("Messageboard returned no body for game " + gameId));
  }

  public SolveResponseDto solve(String gameId, String adId) {
    requireNotBlank(gameId, "gameId");
    requireNotBlank(adId, "adId");
    return Optional.ofNullable(
            restClient
                .post()
                .uri("/api/v2/{gameId}/solve/{adId}", gameId, adId)
                .retrieve()
                .body(SolveResponseDto.class))
        .orElseThrow(
            () -> new MugloarApiException("Solve returned an empty response for ad " + adId));
  }

  public List<ShopItemDto> getShop(String gameId) {
    requireNotBlank(gameId, "gameId");
    return Optional.ofNullable(
            restClient
                .get()
                .uri("/api/v2/{gameId}/shop", gameId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ShopItemDto>>() {}))
        .orElseThrow(() -> new MugloarApiException("Shop returned no items for game " + gameId));
  }

  public BuyResponseDto buy(String gameId, String itemId) {
    requireNotBlank(gameId, "gameId");
    requireNotBlank(itemId, "itemId");
    return Optional.ofNullable(
            restClient
                .post()
                .uri("/api/v2/{gameId}/shop/buy/{itemId}", gameId, itemId)
                .retrieve()
                .body(BuyResponseDto.class))
        .orElseThrow(
            () ->
                new MugloarApiException("Purchase returned an empty response for item " + itemId));
  }

  private static void requireNotBlank(String value, String name) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(name + " must not be blank");
    }
  }
}
