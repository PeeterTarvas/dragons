package com.bigbank.dragons.client.exception;

public class MugloarApiException extends RuntimeException {

  public MugloarApiException(String message) {
    super(message);
  }

  public MugloarApiException(String message, Throwable cause) {
    super(message, cause);
  }
}
