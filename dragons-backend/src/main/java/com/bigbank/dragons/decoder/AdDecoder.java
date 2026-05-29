package com.bigbank.dragons.decoder;

import com.bigbank.dragons.domain.Message;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class AdDecoder {

  private static final int ENCRYPTION_BASE64 = 1;
  private static final int ENCRYPTION_ROT13 = 2;

  private static final int ROT_OFFSET = 13;
  private static final int ALPHABET_SIZE = 26;

  public Optional<Message> decode(Message ad) {
    if (ad == null) {
      return Optional.empty();
    }
    int encryptionType = Optional.ofNullable(ad.encrypted()).orElse(0);
    return Optional.of(
        switch (encryptionType) {
          case ENCRYPTION_BASE64 ->
              new Message(
                  base64(ad.adId()),
                  base64(ad.message()),
                  ad.reward(),
                  ad.expiresIn(),
                  null,
                  base64(ad.probability()));
          case ENCRYPTION_ROT13 ->
              new Message(
                  rot13(ad.adId()),
                  rot13(ad.message()),
                  ad.reward(),
                  ad.expiresIn(),
                  null,
                  rot13(ad.probability()));
          default -> ad;
        });
  }

  private static String base64(String s) {
    return new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8);
  }

  private static String rot13(String s) {
    StringBuilder out = new StringBuilder(s.length());
    for (char c : s.toCharArray()) {
      if (c >= 'a' && c <= 'z') out.append((char) ('a' + (c - 'a' + ROT_OFFSET) % ALPHABET_SIZE));
      else if (c >= 'A' && c <= 'Z')
        out.append((char) ('A' + (c - 'A' + ROT_OFFSET) % ALPHABET_SIZE));
      else out.append(c);
    }
    return out.toString();
  }
}
