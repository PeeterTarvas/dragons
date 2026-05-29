package com.bigbank.dragons.decoder;

import static org.junit.jupiter.api.Assertions.*;

import com.bigbank.dragons.domain.Message;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

public class AdDecoderTest {

  private AdDecoder decoder;

  @BeforeEach
  void setUp() {
    decoder = new AdDecoder();
  }

  @Test
  void decodeReturnsEmptyOptionalWhenInputIsNull() {
    assertTrue(
        decoder.decode(null).isEmpty(), "Decoding a null Message should return Optional.empty()");
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(ints = {0, 3, 99})
  void decodeReturnsOriginalMessageWhenEncryptionIsMissingOrUnknown(Integer encryptionType) {
    Message original = new Message("id-1", "Normal message", 100, 5, encryptionType, "Sure thing");

    Message decoded = decoder.decode(original).orElseThrow();

    assertEquals(original, decoded, "The Message should be returned completely untouched");
    assertEquals("Normal message", decoded.message());
  }

  @Test
  void decodeSuccessfullyDecodesBase64WhenEncryptionIs1() {
    String encodedId = Base64.getEncoder().encodeToString("ad-123".getBytes());
    String encodedMessage = Base64.getEncoder().encodeToString("Steal a diamond!".getBytes());
    String encodedProbability = Base64.getEncoder().encodeToString("Piece of cake".getBytes());

    Message encryptedAd = new Message(encodedId, encodedMessage, 500, 10, 1, encodedProbability);

    Message decoded = decoder.decode(encryptedAd).orElseThrow();

    assertEquals("ad-123", decoded.adId());
    assertEquals("Steal a diamond!", decoded.message());
    assertEquals("Piece of cake", decoded.probability());
    assertEquals(500, decoded.reward(), "Reward should be preserved");
    assertEquals(10, decoded.expiresIn(), "ExpiresIn should be preserved");
    assertNull(decoded.encrypted(), "Encryption flag should be nullified after decoding");
  }

  @Test
  void decodeSuccessfullyDecodesRot13WhenEncryptionIs2() {
    String encryptedId = "nq-123";
    String encryptedMessage = "Fgrny n qvnzbaq!";
    String encryptedProbability = "Cvrpr bs pnxr";

    Message encryptedAd =
        new Message(encryptedId, encryptedMessage, 250, 7, 2, encryptedProbability);

    Message decoded = decoder.decode(encryptedAd).orElseThrow();

    assertEquals("ad-123", decoded.adId());
    assertEquals(
        "Steal a diamond!",
        decoded.message(),
        "Should decode letters but leave spaces and punctuation untouched");
    assertEquals("Piece of cake", decoded.probability());
    assertEquals(250, decoded.reward());
    assertEquals(7, decoded.expiresIn());
    assertNull(decoded.encrypted());
  }

  @Test
  void decodeThrowsWhenBase64IsInvalid() {
    Message broken = new Message("not!!base64", "also!!not", 100, 5, 1, "bad");
    assertThrows(IllegalArgumentException.class, () -> decoder.decode(broken));
  }
}
