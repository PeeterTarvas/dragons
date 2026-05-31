package com.bigbank.dragons.client.mapper;

import com.bigbank.dragons.client.dto.BuyResponseClientDto;
import com.bigbank.dragons.client.dto.MessageDto;
import com.bigbank.dragons.client.dto.ReputationDto;
import com.bigbank.dragons.client.dto.ShopItemDto;
import com.bigbank.dragons.client.dto.SolveResponseDto;
import com.bigbank.dragons.domain.BuyResponse;
import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.domain.Reputation;
import com.bigbank.dragons.domain.ShopItem;
import com.bigbank.dragons.domain.SolveResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {
  Message toDomain(MessageDto dto);

  Reputation toDomain(ReputationDto dto);

  ShopItem toDomain(ShopItemDto dto);

  BuyResponse toDomain(BuyResponseClientDto dto);

  SolveResponse toDomain(SolveResponseDto dto);

  ReputationDto toDto(Reputation investigate);
}
