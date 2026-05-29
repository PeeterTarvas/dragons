package com.bigbank.dragons.api.exception;

public class GameNotFoundException extends RuntimeException {
  public GameNotFoundException(String message) {
    super(message);
  }
}
