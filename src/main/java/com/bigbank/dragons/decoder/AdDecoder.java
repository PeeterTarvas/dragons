package com.bigbank.dragons.decoder;

import com.bigbank.dragons.domain.Message;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class AdDecoder {

  public Message decode(Message ad) {
    if (ad == null) {
      return null;
    }
    int encryptionType = Optional.ofNullable(ad.encrypted()).orElse(0);
    return switch (encryptionType) {
      case 1 ->
          new Message(
              base64(ad.adId()),
              base64(ad.message()),
              ad.reward(),
              ad.expiresIn(),
              null,
              base64(ad.probability()));
      case 2 ->
          new Message(
              rot13(ad.adId()),
              rot13(ad.message()),
              ad.reward(),
              ad.expiresIn(),
              null,
              rot13(ad.probability()));
      default -> ad;
    };
  }

  private static String base64(String s) {
    return new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8);
  }

  private static String rot13(String s) {
    StringBuilder out = new StringBuilder(s.length());
    for (char c : s.toCharArray()) {
      if (c >= 'a' && c <= 'z') out.append((char) ('a' + (c - 'a' + 13) % 26));
      else if (c >= 'A' && c <= 'Z') out.append((char) ('A' + (c - 'A' + 13) % 26));
      else out.append(c);
    }
    return out.toString();
  }
}
