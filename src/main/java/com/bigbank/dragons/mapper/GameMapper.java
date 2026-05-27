package com.bigbank.dragons.mapper;

import com.bigbank.dragons.api.dto.BatchStatsDto;
import com.bigbank.dragons.api.dto.TurnLogDto;
import com.bigbank.dragons.client.dto.BuyResponseDto;
import com.bigbank.dragons.client.dto.MessageDto;
import com.bigbank.dragons.client.dto.ReputationDto;
import com.bigbank.dragons.client.dto.ShopItemDto;
import com.bigbank.dragons.client.dto.SolveResponseDto;
import com.bigbank.dragons.domain.BatchStats;
import com.bigbank.dragons.domain.BuyResponse;
import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.domain.Reputation;
import com.bigbank.dragons.domain.ShopItem;
import com.bigbank.dragons.domain.SolveResponse;
import com.bigbank.dragons.domain.TurnLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameMapper {
  Message toDomain(MessageDto dto);

  Reputation toDomain(ReputationDto dto);

  ShopItem toDomain(ShopItemDto dto);

  BuyResponse toDomain(BuyResponseDto dto);

  SolveResponse toDomain(SolveResponseDto dto);

  BatchStatsDto toDto(BatchStats domain);

  TurnLogDto toDto(TurnLog domain);

  ReputationDto toDto(Reputation investigate);
}
