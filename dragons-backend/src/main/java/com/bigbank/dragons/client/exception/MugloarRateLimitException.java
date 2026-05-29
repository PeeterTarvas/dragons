package com.bigbank.dragons.client.exception;

import java.time.Duration;

public class MugloarRateLimitException extends RuntimeException {

  private final Duration retryAfter; // nullable

  public MugloarRateLimitException(String message, Duration retryAfter) {
    super(message);
    this.retryAfter = retryAfter;
  }

  public Duration getRetryAfter() {
    return retryAfter;
  }
}
