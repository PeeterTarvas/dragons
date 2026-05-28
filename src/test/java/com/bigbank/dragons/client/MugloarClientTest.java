package com.bigbank.dragons.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.bigbank.dragons.client.dto.BuyResponseDto;
import com.bigbank.dragons.client.dto.MessageDto;
import com.bigbank.dragons.client.dto.ReputationDto;
import com.bigbank.dragons.client.dto.ShopItemDto;
import com.bigbank.dragons.client.dto.SolveResponseDto;
import com.bigbank.dragons.client.dto.StartGameResponseDto;
import com.bigbank.dragons.client.exception.MugloarApiException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class MugloarClientTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private RestClient restClient;

  @InjectMocks private MugloarClient client;

  @Test
  void startGameSuccessReturnsDto() {
    StartGameResponseDto expected = mock(StartGameResponseDto.class);
    when(restClient.post().uri("/api/v2/game/start").retrieve().body(StartGameResponseDto.class))
        .thenReturn(expected);

    StartGameResponseDto result = client.startGame();

    assertEquals(expected, result);
  }

  @Test
  void startGameEmptyBodyThrowsException() {
    when(restClient.post().uri("/api/v2/game/start").retrieve().body(StartGameResponseDto.class))
        .thenReturn(null);

    MugloarApiException ex = assertThrows(MugloarApiException.class, () -> client.startGame());

    assertEquals("Start game returned an empty response", ex.getMessage());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "   "})
  void investigateBlankGameIdThrowsException(String gameId) {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> client.investigate(gameId));

    assertEquals("gameId must not be blank", ex.getMessage());
  }

  @Test
  void investigateSuccessReturnsDto() {
    ReputationDto expected = mock(ReputationDto.class);
    when(restClient
            .post()
            .uri("/api/v2/{gameId}/investigate/reputation", "game123")
            .retrieve()
            .body(ReputationDto.class))
        .thenReturn(expected);

    ReputationDto result = client.investigate("game123");

    assertEquals(expected, result);
  }

  @Test
  void investigateEmptyBodyThrowsException() {
    when(restClient
            .post()
            .uri("/api/v2/{gameId}/investigate/reputation", "game123")
            .retrieve()
            .body(ReputationDto.class))
        .thenReturn(null);

    MugloarApiException ex =
        assertThrows(MugloarApiException.class, () -> client.investigate("game123"));

    assertEquals("Investigation returned an empty response for game game123", ex.getMessage());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "   "})
  void getMessagesBlankGameIdThrowsException(String gameId) {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> client.getMessages(gameId));

    assertEquals("gameId must not be blank", ex.getMessage());
  }

  @Test
  void getMessagesSuccessReturnsList() {
    List<MessageDto> expected = List.of(mock(MessageDto.class));
    when(restClient
            .get()
            .uri("/api/v2/{gameId}/messages", "game123")
            .retrieve()
            .body(any(ParameterizedTypeReference.class)))
        .thenReturn(expected);

    List<MessageDto> result = client.getMessages("game123");

    assertEquals(expected, result);
  }

  @Test
  void getMessagesEmptyBodyThrowsException() {
    when(restClient
            .get()
            .uri("/api/v2/{gameId}/messages", "game123")
            .retrieve()
            .body(any(ParameterizedTypeReference.class)))
        .thenReturn(null);

    MugloarApiException ex =
        assertThrows(MugloarApiException.class, () -> client.getMessages("game123"));

    assertEquals("Messageboard returned no body for game game123", ex.getMessage());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "   "})
  void solveBlankGameIdThrowsException(String gameId) {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> client.solve(gameId, "ad123"));

    assertEquals("gameId must not be blank", ex.getMessage());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "   "})
  void solveBlankAdIdThrowsException(String adId) {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> client.solve("game123", adId));

    assertEquals("adId must not be blank", ex.getMessage());
  }

  @Test
  void solveSuccessReturnsDto() {
    SolveResponseDto expected = mock(SolveResponseDto.class);
    when(restClient
            .post()
            .uri("/api/v2/{gameId}/solve/{adId}", "game123", "ad123")
            .retrieve()
            .body(SolveResponseDto.class))
        .thenReturn(expected);

    SolveResponseDto result = client.solve("game123", "ad123");

    assertEquals(expected, result);
  }

  @Test
  void solveEmptyBodyThrowsException() {
    when(restClient
            .post()
            .uri("/api/v2/{gameId}/solve/{adId}", "game123", "ad123")
            .retrieve()
            .body(SolveResponseDto.class))
        .thenReturn(null);

    MugloarApiException ex =
        assertThrows(MugloarApiException.class, () -> client.solve("game123", "ad123"));

    assertEquals("Solve returned an empty response for ad ad123", ex.getMessage());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "   "})
  void getShopBlankGameIdThrowsException(String gameId) {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> client.getShop(gameId));

    assertEquals("gameId must not be blank", ex.getMessage());
  }

  @Test
  void getShopSuccessReturnsList() {
    List<ShopItemDto> expected = List.of(mock(ShopItemDto.class));
    when(restClient
            .get()
            .uri("/api/v2/{gameId}/shop", "game123")
            .retrieve()
            .body(any(ParameterizedTypeReference.class)))
        .thenReturn(expected);

    List<ShopItemDto> result = client.getShop("game123");

    assertEquals(expected, result);
  }

  @Test
  void getShopEmptyBodyThrowsException() {
    when(restClient
            .get()
            .uri("/api/v2/{gameId}/shop", "game123")
            .retrieve()
            .body(any(ParameterizedTypeReference.class)))
        .thenReturn(null);

    MugloarApiException ex =
        assertThrows(MugloarApiException.class, () -> client.getShop("game123"));

    assertEquals("Shop returned no items for game game123", ex.getMessage());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "   "})
  void buyBlankGameIdThrowsException(String gameId) {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> client.buy(gameId, "item123"));

    assertEquals("gameId must not be blank", ex.getMessage());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "   "})
  void buyBlankItemIdThrowsException(String itemId) {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> client.buy("game123", itemId));

    assertEquals("itemId must not be blank", ex.getMessage());
  }

  @Test
  void buySuccessReturnsDto() {
    BuyResponseDto expected = mock(BuyResponseDto.class);
    when(restClient
            .post()
            .uri("/api/v2/{gameId}/shop/buy/{itemId}", "game123", "item123")
            .retrieve()
            .body(BuyResponseDto.class))
        .thenReturn(expected);

    BuyResponseDto result = client.buy("game123", "item123");

    assertEquals(expected, result);
  }

  @Test
  void buyEmptyBodyThrowsException() {
    when(restClient
            .post()
            .uri("/api/v2/{gameId}/shop/buy/{itemId}", "game123", "item123")
            .retrieve()
            .body(BuyResponseDto.class))
        .thenReturn(null);

    MugloarApiException ex =
        assertThrows(MugloarApiException.class, () -> client.buy("game123", "item123"));

    assertEquals("Purchase returned an empty response for item item123", ex.getMessage());
  }
}
