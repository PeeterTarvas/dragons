package com.bigbank.dragons.client.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mugloar")
public record MugloarProperties(String baseUrl) {

  public MugloarProperties {
    if (baseUrl == null || baseUrl.isBlank()) {
      baseUrl = "https://dragonsofmugloar.com";
    }
  }
}
