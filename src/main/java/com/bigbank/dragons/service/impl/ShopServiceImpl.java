package com.bigbank.dragons.service.impl;

import com.bigbank.dragons.api.dto.TurnLogDto;
import com.bigbank.dragons.client.MugloarClient;
import com.bigbank.dragons.client.dto.BuyResponseDto;
import com.bigbank.dragons.client.dto.ShopItemDto;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.service.ShopService;
import com.bigbank.dragons.strategy.GameStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final MugloarClient client;
    private final GameStrategy strategy;

    @Override
    public boolean shop(GameState state) {
        List<ShopItemDto> items = client.getShop(state.getGameId());
        Optional<ShopItemDto> pick = strategy.choosePurchase(items, state);
        pick.ifPresent(item -> buy(state, item));
        return pick.isPresent();
    }

    private void buy(GameState state, ShopItemDto item) {
        BuyResponseDto buy = client.buy(state.getGameId(), item.id());
        state.updateAfterBuy(buy.gold(), buy.lives(), buy.level(), buy.turn());
        log.debug(
                "Bought '{}'; now gold={}, lives={}, level={}",
                item.name(), buy.gold(), buy.lives(), buy.level());
        state.addLog(
                new TurnLogDto(
                        buy.turn(), "BUY", item.name(), null, buy.shoppingSuccess(),
                        state.getScore(), buy.lives(), buy.gold()));
    }
}
