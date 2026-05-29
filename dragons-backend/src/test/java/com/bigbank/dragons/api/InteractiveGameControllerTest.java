package com.bigbank.dragons.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bigbank.dragons.api.dto.*;
import com.bigbank.dragons.api.mapper.ApiMapper;
import com.bigbank.dragons.domain.Board;
import com.bigbank.dragons.domain.BuyResponse;
import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.domain.ShopItem;
import com.bigbank.dragons.domain.SolveResponse;
import com.bigbank.dragons.game.state.GameState;
import com.bigbank.dragons.service.InteractiveGameService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InteractiveGameControllerTest {

  @Mock private InteractiveGameService service;
  @Mock private ApiMapper apiMapper;

  @InjectMocks private InteractiveGameController controller;

  @Test
  void startMapsAndReturnsStartedGame() {
    GameState state = mock(GameState.class);
    GameStateDto expectedDto = mock(GameStateDto.class);

    when(service.startGame()).thenReturn(state);
    when(apiMapper.toGameStateDto(state)).thenReturn(expectedDto);

    assertEquals(expectedDto, controller.start());
    verify(service).startGame();
  }

  @Test
  void boardWithNullStrategyCallsServiceWithoutStrategy() {
    Board board = mock(Board.class);
    BoardDto expectedDto = mock(BoardDto.class);
    String gameId = "game-123";

    when(service.getBoard(gameId)).thenReturn(board);
    when(apiMapper.toDto(board)).thenReturn(expectedDto);

    BoardDto result = controller.board(gameId, null);

    assertEquals(expectedDto, result);
    verify(service).getBoard(gameId);
    verify(service, never()).getBoard(eq(gameId), anyString());
  }

  @Test
  void boardWithStrategyCallsServiceWithStrategy() {
    Board board = mock(Board.class);
    BoardDto expectedDto = mock(BoardDto.class);
    String gameId = "game-123";
    String strategy = "low-risk";

    when(service.getBoard(gameId, strategy)).thenReturn(board);
    when(apiMapper.toDto(board)).thenReturn(expectedDto);

    BoardDto result = controller.board(gameId, strategy);

    assertEquals(expectedDto, result);
    verify(service).getBoard(gameId, strategy);
  }

  @Test
  void solveMapsDtoExecutesAndMapsResponse() {
    String gameId = "game-123";
    AdDto requestDto = mock(AdDto.class);
    Message domainMessage = mock(Message.class);
    SolveResponse solveResponse = mock(SolveResponse.class);
    SolveResponseDto expectedDto = mock(SolveResponseDto.class);

    when(apiMapper.toDomain(requestDto)).thenReturn(domainMessage);
    when(service.solveAd(gameId, domainMessage)).thenReturn(solveResponse);
    when(apiMapper.toDto(solveResponse)).thenReturn(expectedDto);

    SolveResponseDto result = controller.solve(gameId, requestDto);

    assertEquals(expectedDto, result);
    verify(service).solveAd(gameId, domainMessage);
  }

  @Test
  void shopMapsAndReturnsShopList() {
    String gameId = "game-123";
    List<ShopItem> domainItems = List.of(mock(ShopItem.class));
    List<ShopItemDto> expectedDtos = List.of(mock(ShopItemDto.class));

    when(service.getShop(gameId)).thenReturn(domainItems);
    when(apiMapper.toListDto(domainItems)).thenReturn(expectedDtos);

    List<ShopItemDto> result = controller.shop(gameId);

    assertEquals(expectedDtos, result);
    verify(service).getShop(gameId);
  }

  @Test
  void buyMapsDtoExecutesAndMapsResponse() {
    String gameId = "game-123";
    ShopItemDto requestDto = mock(ShopItemDto.class);
    ShopItem domainItem = mock(ShopItem.class);
    BuyResponse buyResponse = mock(BuyResponse.class);
    BuyResponseDto expectedDto = mock(BuyResponseDto.class);

    when(apiMapper.toDomain(requestDto)).thenReturn(domainItem);
    when(service.buyItem(gameId, domainItem)).thenReturn(buyResponse);
    when(apiMapper.toDto(buyResponse)).thenReturn(expectedDto);

    BuyResponseDto result = controller.buy(gameId, requestDto);

    assertEquals(expectedDto, result);
    verify(service).buyItem(gameId, domainItem);
  }

  @Test
  void stateMapsDtoExecutesAndMapsResponse() {
    String gameId = "game-123";
    GameState state = mock(GameState.class);
    GameResultDto expectedDto = mock(GameResultDto.class);

    when(service.getGameState(gameId)).thenReturn(state);
    when(apiMapper.toGameResultDto(state)).thenReturn(expectedDto);

    GameResultDto result = controller.state(gameId);

    assertEquals(expectedDto, result);
    verify(service).getGameState(gameId);
  }
}
