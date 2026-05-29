package com.bigbank.dragons.api.mapper;

import com.bigbank.dragons.api.dto.*;
import com.bigbank.dragons.domain.*;
import com.bigbank.dragons.game.state.GameState;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApiMapper {

  BatchStatsDto toDto(BatchStats domain);

  TurnLogDto toDto(TurnLog domain);

  GameStateDto toGameStateDto(GameState gameState);

  SolveResponseDto toDto(SolveResponse solveResponse);

  List<ShopItemDto> toListDto(List<ShopItem> shopItems);

  ShopItem toDomain(ShopItemDto shopItemDto);

  Message toDomain(AdDto adDto);

  @Mapping(source = "messages", target = "ads")
  BoardDto toDto(Board board);

  @Mapping(source = "message.adId", target = "adId")
  @Mapping(source = "message.message", target = "message")
  @Mapping(source = "message.reward", target = "reward")
  @Mapping(source = "message.expiresIn", target = "expiresIn")
  @Mapping(source = "message.probability", target = "probability")
  @Mapping(source = "message.encrypted", target = "encrypted")
  AdDto toAdDto(EvaluatedMessage domain);

  @Mapping(source = ".", target = "gameStateDto")
  GameResultDto toGameResultDto(GameState gameState);

  BuyResponseDto toDto(BuyResponse buyResponse);
}
