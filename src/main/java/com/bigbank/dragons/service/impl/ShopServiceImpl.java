package com.bigbank.dragons.service.impl;

import com.bigbank.dragons.client.MugloarClient;
import com.bigbank.dragons.client.mapper.ClientMapper;
import com.bigbank.dragons.domain.BuyResponse;
import com.bigbank.dragons.domain.ShopItem;
import com.bigbank.dragons.domain.TurnLog;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.service.ShopService;
import com.bigbank.dragons.strategy.GameStrategy;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

  private final MugloarClient client;
  private final ClientMapper mapper;

  @Override
  public List<ShopItem> shop(GameState state, GameStrategy strategy) {
    List<ShopItem> items =
        client.getShop(state.getGameId()).stream().map(mapper::toDomain).toList();
    List<ShopItem> plan = strategy.choosePurchases(items, state);
    List<ShopItem> itemsBought = new ArrayList<>();

    for (ShopItem item : plan) {
      if (item.cost() > state.getGold()) {
        continue;
      }
      buy(state, item);
      itemsBought.add(item);
    }
    return itemsBought;
  }

  private void buy(GameState state, ShopItem item) {
    BuyResponse buy = mapper.toDomain(client.buy(state.getGameId(), item.id()));
    state.updateAfterBuy(buy);
    if (buy.shoppingSuccess()) {
      log.info(
          "Bought '{}'; now gold={}, lives={}, level={}",
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
  }
}
