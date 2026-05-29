package com.bigbank.dragons.service.validation;

import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.domain.ShopItem;
import com.bigbank.dragons.game.state.GameState;
import org.springframework.stereotype.Component;

@Component
public class DomainValidator {

  public void validate(Message message) {
    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null");
    }
    if (message.adId() == null || message.adId().isBlank()) {
      throw new IllegalArgumentException("Message adId cannot be blank");
    }
    if (message.reward() == null || message.reward() < 0) {
      throw new IllegalArgumentException("Message reward must be zero or positive");
    }
  }

  public void validate(ShopItem item) {
    if (item == null) {
      throw new IllegalArgumentException("ShopItem cannot be null");
    }
    if (item.id() == null || item.id().isBlank()) {
      throw new IllegalArgumentException("ShopItem id cannot be blank");
    }
    if (item.cost() == null || item.cost() < 0) {
      throw new IllegalArgumentException("ShopItem cost must be zero or positive");
    }
  }

  public void validate(GameState state) {
    if (state == null) {
      throw new IllegalArgumentException("GameState cannot be null");
    }
    if (state.getGameId() == null || state.getGameId().isBlank()) {
      throw new IllegalArgumentException("GameState gameId cannot be blank");
    }
  }
}
