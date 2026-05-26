package com.bigbank.dragons.client.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mugloar")
public record MugloarProperties(String baseUrl, int connectionTimeout, int readTimeout) {

  public MugloarProperties {
    if (baseUrl == null || baseUrl.isBlank()) baseUrl = "https://dragonsofmugloar.com";
    if (connectionTimeout <= 0) connectionTimeout = 10;
    if (readTimeout <= 0) readTimeout = 30;
  }
}
