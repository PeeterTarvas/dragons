package com.bigbank.dragons.client.exception;

import java.time.Duration;
import lombok.Getter;

@Getter
public class MugloarRateLimitException extends RuntimeException {

  private final Duration retryAfter;

  public MugloarRateLimitException(String message, Duration retryAfter) {
    super(message);
    this.retryAfter = retryAfter;
  }
}
