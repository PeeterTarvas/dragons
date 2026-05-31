package com.bigbank.dragons.client.mapper;

import com.bigbank.dragons.client.dto.*;
import com.bigbank.dragons.domain.BuyResponse;
import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.domain.Reputation;
import com.bigbank.dragons.domain.ShopItem;
import com.bigbank.dragons.domain.SolveResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {
  Message toDomain(MessageClientDto dto);

  Reputation toDomain(ReputationClientDto dto);

  ShopItem toDomain(ShopItemClientDto dto);

  BuyResponse toDomain(BuyResponseClientDto dto);

  SolveResponse toDomain(SolveResponseClientDto dto);

  ReputationClientDto toDto(Reputation investigate);
}
