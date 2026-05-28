package com.bigbank.dragons.service.impl;

import com.bigbank.dragons.client.MugloarClient;
import com.bigbank.dragons.client.mapper.ClientMapper;
import com.bigbank.dragons.domain.BuyResponse;
import com.bigbank.dragons.domain.ShopItem;
import com.bigbank.dragons.domain.TurnLog;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.service.ShopService;
import com.bigbank.dragons.service.validation.DomainValidator;
import com.bigbank.dragons.strategy.GameStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

  private final MugloarClient client;
  private final ClientMapper mapper;
  private final DomainValidator domainValidator;

  @Override
  public List<ShopItem> shop(GameState state, GameStrategy strategy) {
    domainValidator.validate(state);
    Objects.requireNonNull(strategy, "GameStrategy cannot be null");
    List<ShopItem> items = getShopItems(state);
    List<ShopItem> plan = strategy.choosePurchases(items, state);
    List<ShopItem> itemsBought = new ArrayList<>();

    for (ShopItem item : plan) {
      if (item.cost() > state.getGold()) {
        continue;
      }
      BuyResponse buyResponse = buyItem(state, item);
      if (buyResponse.shoppingSuccess()) {
        itemsBought.add(item);
      }
    }
    return itemsBought;
  }

  @Override
  public List<ShopItem> getShopItems(GameState state) {
    domainValidator.validate(state);
    return client.getShop(state.getGameId()).stream().map(mapper::toDomain).toList();
  }

  @Override
  public BuyResponse buyItem(GameState state, ShopItem item) {
    domainValidator.validate(state);
    domainValidator.validate(item);
    BuyResponse buy = mapper.toDomain(client.buy(state.getGameId(), item.id()));
    state.updateAfterBuy(buy);
    if (buy.shoppingSuccess()) {
      log.info(
          "Bought '{}'; now gold={}, lives={}, level={}",
          item.name(),
          buy.gold(),
          buy.lives(),
          buy.level());
    } else {
      log.warn(
          "Failed to buy '{}'; gold remains={}, lives={}, level={}",
          item.name(),
          buy.gold(),
          buy.lives(),
          buy.level());
    }
    state.addLog(
        new TurnLog(
            buy.turn(),
            String.format("BUY: %s", item.name()),
            null,
            buy.shoppingSuccess(),
            state.getScore(),
            buy.lives(),
            buy.gold()));

    return buy;
  }
}
