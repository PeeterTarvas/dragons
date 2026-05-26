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
    StartGameResponseDto response =
        restClient.post().uri("/api/v2/game/start").retrieve().body(StartGameResponseDto.class);

    if (response == null) {
      throw new MugloarApiException("Start game returned an empty response");
    }
    return response;
  }

  public ReputationDto investigate(String gameId) {
    requireNotBlank(gameId, "gameId");
    ReputationDto response =
        restClient
            .post()
            .uri("/api/v2/{gameId}/investigate/reputation", gameId)
            .retrieve()
            .body(ReputationDto.class);

    if (response == null) {
      throw new MugloarApiException("Investigation returned an empty response for game " + gameId);
    }
    return response;
  }

  public List<MessageDto> getMessages(String gameId) {
    requireNotBlank(gameId, "gameId");
    List<MessageDto> messages =
        restClient
            .get()
            .uri("/api/v2/{gameId}/messages", gameId)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});

    if (messages == null) {
      throw new MugloarApiException("Messageboard returned no body for game " + gameId);
    }
    return messages;
  }

  public SolveResponseDto solve(String gameId, String adId) {
    requireNotBlank(gameId, "gameId");
    requireNotBlank(adId, "adId");
    SolveResponseDto response =
        restClient
            .post()
            .uri("/api/v2/{gameId}/solve/{adId}", gameId, adId)
            .retrieve()
            .body(SolveResponseDto.class);

    if (response == null) {
      throw new MugloarApiException("Solve returned an empty response for ad " + adId);
    }
    return response;
  }

  public List<ShopItemDto> getShop(String gameId) {
    requireNotBlank(gameId, "gameId");
    List<ShopItemDto> items =
        restClient
            .get()
            .uri("/api/v2/{gameId}/shop", gameId)
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});

    if (items == null) {
      throw new MugloarApiException("Shop returned no items for game " + gameId);
    }
    return items;
  }

  public BuyResponseDto buy(String gameId, String itemId) {
    requireNotBlank(gameId, "gameId");
    requireNotBlank(itemId, "itemId");
    BuyResponseDto response =
        restClient
            .post()
            .uri("/api/v2/{gameId}/shop/buy/{itemId}", gameId, itemId)
            .retrieve()
            .body(BuyResponseDto.class);

    if (response == null) {
      throw new MugloarApiException("Purchase returned an empty response for item " + itemId);
    }
    return response;
  }

  private static void requireNotBlank(String value, String name) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(name + " must not be blank");
    }
  }
}
