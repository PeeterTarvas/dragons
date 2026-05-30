package com.bigbank.dragons.api.controller.impl;

import com.bigbank.dragons.api.controller.InteractiveGameApi;
import com.bigbank.dragons.api.dto.AdDto;
import com.bigbank.dragons.api.dto.BoardDto;
import com.bigbank.dragons.api.dto.BuyResponseDto;
import com.bigbank.dragons.api.dto.GameResultDto;
import com.bigbank.dragons.api.dto.GameStateDto;
import com.bigbank.dragons.api.dto.ShopItemDto;
import com.bigbank.dragons.api.dto.SolveResponseDto;
import com.bigbank.dragons.api.mapper.ApiMapper;
import com.bigbank.dragons.domain.Board;
import com.bigbank.dragons.service.InteractiveGameService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
public class InteractiveGameController implements InteractiveGameApi {

  private final InteractiveGameService service;
  private final ApiMapper apiMapper;

  @Override
  public GameStateDto start() {
    return apiMapper.toGameStateDto(service.startGame());
  }

  @Override
  public BoardDto board(String gameId, String strategy) {
    Board board =
        Optional.ofNullable(strategy)
            .map(strat -> service.getBoard(gameId, strat))
            .orElseGet(() -> service.getBoard(gameId));
    return apiMapper.toDto(board);
  }

  @Override
  public SolveResponseDto solve(String gameId, AdDto ad) {
    return apiMapper.toDto(service.solveAd(gameId, apiMapper.toDomain(ad)));
  }

  @Override
  public List<ShopItemDto> shop(String gameId) {
    return apiMapper.toListDto(service.getShop(gameId));
  }

  @Override
  public BuyResponseDto buy(String gameId, ShopItemDto itemId) {
    return apiMapper.toDto(service.buyItem(gameId, apiMapper.toDomain(itemId)));
  }

  @Override
  public GameResultDto state(String gameId) {
    return apiMapper.toGameResultDto(service.getGameState(gameId));
  }
}
