package com.bigbank.dragons.service.validation;

import static org.junit.jupiter.api.Assertions.*;

import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.domain.ShopItem;
import com.bigbank.dragons.game.state.GameState;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class DomainValidatorTest {

  private DomainValidator validator;

  @BeforeEach
  void setUp() {
    validator = new DomainValidator();
  }

  @Test
  void validateMessageNullThrowsException() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> validator.validate((Message) null));
    assertEquals("Message cannot be null", ex.getMessage());
  }

  static Stream<Arguments> invalidMessages() {
    return Stream.of(
        Arguments.of(new Message(null, "msg", 10, 10, 0, "p"), "Message adId cannot be blank"),
        Arguments.of(new Message("", "msg", 10, 10, 0, "p"), "Message adId cannot be blank"),
        Arguments.of(new Message("  ", "msg", 10, 10, 0, "p"), "Message adId cannot be blank"),
        Arguments.of(
            new Message("id", "msg", null, 10, 0, "p"), "Message reward must be zero or positive"),
        Arguments.of(
            new Message("id", "msg", -1, 10, 0, "p"), "Message reward must be zero or positive"));
  }

  @ParameterizedTest
  @MethodSource("invalidMessages")
  void validateMessageInvalidFieldsThrowsException(Message msg, String expectedError) {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> validator.validate(msg));
    assertEquals(expectedError, ex.getMessage());
  }

  @Test
  void validateMessageValidPasses() {
    assertDoesNotThrow(() -> validator.validate(new Message("id", "msg", 0, 10, 0, "p")));
  }

  @Test
  void validateShopItemNullThrowsException() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> validator.validate((ShopItem) null));
    assertEquals("ShopItem cannot be null", ex.getMessage());
  }

  static Stream<Arguments> invalidShopItems() {
    return Stream.of(
        Arguments.of(new ShopItem(null, "name", 10), "ShopItem id cannot be blank"),
        Arguments.of(new ShopItem("", "name", 10), "ShopItem id cannot be blank"),
        Arguments.of(new ShopItem("  ", "name", 10), "ShopItem id cannot be blank"),
        Arguments.of(new ShopItem("id", "name", null), "ShopItem cost must be zero or positive"),
        Arguments.of(new ShopItem("id", "name", -1), "ShopItem cost must be zero or positive"));
  }

  @ParameterizedTest
  @MethodSource("invalidShopItems")
  void validateShopItemInvalidFieldsThrowsException(ShopItem item, String expectedError) {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> validator.validate(item));
    assertEquals(expectedError, ex.getMessage());
  }

  @Test
  void validateShopItemValidPasses() {
    assertDoesNotThrow(() -> validator.validate(new ShopItem("id", "name", 0)));
  }

  @Test
  void validateGameStateNullThrowsException() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> validator.validate((GameState) null));
    assertEquals("GameState cannot be null", ex.getMessage());
  }

  static Stream<Arguments> invalidGameStates() {
    return Stream.of(
        Arguments.of(new GameState(null, 3, 0, 1, 0, 0, false), "GameState gameId cannot be blank"),
        Arguments.of(new GameState("", 3, 0, 1, 0, 0, false), "GameState gameId cannot be blank"),
        Arguments.of(
            new GameState("  ", 3, 0, 1, 0, 0, false), "GameState gameId cannot be blank"));
  }

  @ParameterizedTest
  @MethodSource("invalidGameStates")
  void validateGameStateInvalidFieldsThrowsException(GameState state, String expectedError) {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> validator.validate(state));
    assertEquals(expectedError, ex.getMessage());
  }

  @Test
  void validateGameStateValidPasses() {
    assertDoesNotThrow(() -> validator.validate(new GameState("id", 3, 0, 1, 0, 0, false)));
  }
}
