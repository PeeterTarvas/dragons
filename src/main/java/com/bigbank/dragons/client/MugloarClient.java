package com.bigbank.dragons.client;

import com.bigbank.dragons.client.dto.*;
import com.bigbank.dragons.client.exception.MugloarApiException;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class MugloarClient {

  private final RestClient restClient;

  public MugloarClient(RestClient mugloarRestClient) {
    this.restClient = mugloarRestClient;
  }

  public StartGameResponse startGame() {
    return post("/api/v2/game/start", StartGameResponse.class);
  }

  public Reputation investigate(String gameId) {
    requireGameId(gameId);
    return post("/api/v2/{gameId}/investigate/reputation", Reputation.class, gameId);
  }

  public List<Message> getMessages(String gameId) {
    requireGameId(gameId);
    try {
      List<Message> messages =
          restClient
              .get()
              .uri("/api/v2/{gameId}/messages", gameId)
              .retrieve()
              .body(new ParameterizedTypeReference<List<Message>>() {});
      if (messages == null) {
        throw new MugloarApiException("Messageboard returned no body for game " + gameId);
      }
      return messages;
    } catch (RestClientException e) {
      throw new MugloarApiException("Failed to fetch messages for game " + gameId, e);
    }
  }

  public SolveResponse solve(String gameId, String adId) {
    requireGameId(gameId);
    if (adId == null || adId.isBlank()) {
      throw new IllegalArgumentException("adId must not be blank");
    }
    return post("/api/v2/{gameId}/solve/{adId}", SolveResponse.class, gameId, adId);
  }

  public List<ShopItem> getShop(String gameId) {
    requireGameId(gameId);
    try {
      ShopListing listing =
          restClient.get().uri("/api/v2/{gameId}/shop", gameId).retrieve().body(ShopListing.class);
      if (listing == null || listing.items() == null) {
        throw new MugloarApiException("Shop returned no items for game " + gameId);
      }
      return listing.items();
    } catch (RestClientException e) {
      throw new MugloarApiException("Failed to fetch shop for game " + gameId, e);
    }
  }

  public BuyResponse buy(String gameId, String itemId) {
    requireGameId(gameId);
    if (itemId == null || itemId.isBlank()) {
      throw new IllegalArgumentException("itemId must not be blank");
    }
    return post("/api/v2/{gameId}/shop/buy/{itemId}", BuyResponse.class, gameId, itemId);
  }

  private <T> T post(String uri, Class<T> type, Object... uriVars) {
    try {
      T body = restClient.post().uri(uri, uriVars).retrieve().body(type);
      if (body == null) {
        throw new MugloarApiException("Empty response body from " + uri);
      }
      return body;
    } catch (RestClientException e) {
      throw new MugloarApiException("Request failed: " + uri, e);
    }
  }

  private static void requireGameId(String gameId) {
    if (gameId == null || gameId.isBlank()) {
      throw new IllegalArgumentException("gameId must not be blank");
    }
  }

  /** The shop endpoint wraps its array in an {@code items} field. */
  private record ShopListing(List<ShopItem> items) {}
}
