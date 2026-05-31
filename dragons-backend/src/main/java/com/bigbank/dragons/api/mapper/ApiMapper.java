package com.bigbank.dragons.api.mapper;

import com.bigbank.dragons.api.dto.BatchStatsDto;
import com.bigbank.dragons.api.dto.BoardDto;
import com.bigbank.dragons.api.dto.BuyResponseDto;
import com.bigbank.dragons.api.dto.GameResultDto;
import com.bigbank.dragons.api.dto.GameStateDto;
import com.bigbank.dragons.api.dto.MessageDto;
import com.bigbank.dragons.api.dto.ShopItemDto;
import com.bigbank.dragons.api.dto.SolveResponseDto;
import com.bigbank.dragons.api.dto.TurnLogDto;
import com.bigbank.dragons.domain.BatchStats;
import com.bigbank.dragons.domain.Board;
import com.bigbank.dragons.domain.BuyResponse;
import com.bigbank.dragons.domain.EvaluatedMessage;
import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.domain.ShopItem;
import com.bigbank.dragons.domain.SolveResponse;
import com.bigbank.dragons.domain.TurnLog;
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

  Message toDomain(MessageDto messageDto);

  @Mapping(source = "messages", target = "ads")
  BoardDto toDto(Board board);

  @Mapping(source = "message.adId", target = "adId")
  @Mapping(source = "message.message", target = "message")
  @Mapping(source = "message.reward", target = "reward")
  @Mapping(source = "message.expiresIn", target = "expiresIn")
  @Mapping(source = "message.probability", target = "probability")
  @Mapping(source = "message.encrypted", target = "encrypted")
  MessageDto toAdDto(EvaluatedMessage domain);

  @Mapping(source = ".", target = "gameStateDto")
  GameResultDto toGameResultDto(GameState gameState);

  BuyResponseDto toDto(BuyResponse buyResponse);
}
