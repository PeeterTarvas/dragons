package com.bigbank.dragons.service;

import com.bigbank.dragons.domain.ShopItem;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.strategy.GameStrategy;
import java.util.List;

public interface ShopService {

  List<ShopItem> shop(GameState state, GameStrategy strategy);
}
