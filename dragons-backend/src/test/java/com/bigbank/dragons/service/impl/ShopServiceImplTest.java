package com.bigbank.dragons.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bigbank.dragons.client.MugloarClient;
import com.bigbank.dragons.client.dto.BuyResponseDto;
import com.bigbank.dragons.client.dto.ShopItemDto;
import com.bigbank.dragons.client.mapper.ClientMapper;
import com.bigbank.dragons.domain.BuyResponse;
import com.bigbank.dragons.domain.ShopItem;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.service.validation.DomainValidator;
import com.bigbank.dragons.strategy.GameStrategy;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShopServiceImplTest {

  @Mock private MugloarClient client;
  @Mock private ClientMapper mapper;
  @Mock private DomainValidator domainValidator;
  @InjectMocks private ShopServiceImpl shopService;

  @Test
  void shopNullStrategyShouldThrowNullPointerException() {
    GameState mockState = mock(GameState.class);
    assertThrows(NullPointerException.class, () -> shopService.shop(mockState, null));
    verify(domainValidator).validate(mockState);
  }

  @Test
  void shopShouldFilterAffordableItemsAndTrackPurchases() {
    GameState state = mock(GameState.class);
    GameStrategy strategy = mock(GameStrategy.class);
    ShopItem item1 = new ShopItem("1", "Potion", 50);
    ShopItem item2 = new ShopItem("2", "Expensive Shield", 500);

    ShopItemDto itemDto = mock(ShopItemDto.class);
    BuyResponseDto buyDto = mock(BuyResponseDto.class);

    when(state.getGameId()).thenReturn("game-id");
    when(state.getGold()).thenReturn(100);
    when(client.getShop("game-id")).thenReturn(List.of(itemDto));
    when(mapper.toDomain(itemDto)).thenReturn(item1);

    when(strategy.choosePurchases(anyList(), eq(state))).thenReturn(List.of(item1, item2));

    BuyResponse mockBuySuccess = new BuyResponse(true, 50, 3, 1, 1);
    when(client.buy("game-id", "1")).thenReturn(buyDto);
    when(mapper.toDomain(buyDto)).thenReturn(mockBuySuccess);

    List<ShopItem> bought = shopService.shop(state, strategy);

    assertEquals(1, bought.size());
    assertEquals("1", bought.getFirst().id());
    verify(state).updateAfterBuy(mockBuySuccess);
  }

  @Test
  void buyItemFailedShoppingShouldLogWarnAndAddLogEntry() {
    GameState state = mock(GameState.class);
    ShopItem item = new ShopItem("1", "Broken Potion", 10);
    BuyResponseDto buyDto = mock(BuyResponseDto.class);
    BuyResponse mockBuyFailure = new BuyResponse(false, 100, 3, 1, 2);

    when(state.getGameId()).thenReturn("game-id");
    when(client.buy("game-id", "1")).thenReturn(buyDto);
    when(mapper.toDomain(buyDto)).thenReturn(mockBuyFailure);

    BuyResponse result = shopService.buyItem(state, item);

    assertFalse(result.shoppingSuccess());
    verify(state).addLog(any());
  }

  @Test
  void buyItemLogsSuccessWhenPurchaseSucceeds() {
    GameState state = mock(GameState.class);
    BuyResponseDto buyDto = mock(BuyResponseDto.class);
    ShopItem shopItem = new ShopItem("item-1", "item-1", 1);
    BuyResponse success = new BuyResponse(true, 150, 3, 5, 100);
    when(client.buy(state.getGameId(), shopItem.id())).thenReturn(buyDto);
    when(mapper.toDomain(buyDto)).thenReturn(success);

    BuyResponse result = shopService.buyItem(state, shopItem);

    assertTrue(result.shoppingSuccess());
    verify(state).updateAfterBuy(success);
  }

  @Test
  void buyItemThrowsWhenStateIsInvalid() {
    GameState state = mock(GameState.class);
    ShopItem shopItem = new ShopItem("item-1", "item-1", 1);
    doThrow(new IllegalArgumentException("invalid")).when(domainValidator).validate(state);
    assertThrows(IllegalArgumentException.class, () -> shopService.buyItem(state, shopItem));
  }

  @Test
  void shopDoesNotTrackItemWhenPurchaseFails() {
    GameState state = mock(GameState.class);
    GameStrategy strategy = mock(GameStrategy.class);
    ShopItem item = new ShopItem("1", "Potion", 50);

    ShopItemDto itemDto = mock(ShopItemDto.class);
    BuyResponseDto buyDto = mock(BuyResponseDto.class);

    when(state.getGameId()).thenReturn("game-id");
    when(state.getGold()).thenReturn(100);
    when(client.getShop("game-id")).thenReturn(List.of(itemDto));
    when(mapper.toDomain(itemDto)).thenReturn(item);
    when(strategy.choosePurchases(anyList(), eq(state))).thenReturn(List.of(item));

    BuyResponse failed = new BuyResponse(false, 100, 3, 1, 1);
    when(client.buy("game-id", "1")).thenReturn(buyDto);
    when(mapper.toDomain(buyDto)).thenReturn(failed);

    List<ShopItem> bought = shopService.shop(state, strategy);

    assertEquals(0, bought.size());
    verify(state).updateAfterBuy(failed);
  }
}
