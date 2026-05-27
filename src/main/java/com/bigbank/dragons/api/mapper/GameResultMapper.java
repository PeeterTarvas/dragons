package com.bigbank.dragons.api.mapper;

import com.bigbank.dragons.api.dto.GameResultDto;
import com.bigbank.dragons.game.state.GameState;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ApiMapper.class)
public interface GameResultMapper {

  @Mapping(source = ".", target = "gameStateDto")
  GameResultDto toDto(GameState gameState);
}
