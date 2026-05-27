package com.bigbank.dragons.mapper;

import com.bigbank.dragons.api.dto.GameResultDto;
import com.bigbank.dragons.game.state.GameState;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = GameMapper.class)
public interface GameResultMapper {

  @Mapping(source = "score", target = "finalScore")
  @Mapping(source = "gold", target = "finalGold")
  @Mapping(source = "turn", target = "turns")
  @Mapping(source = "reachedGoal", target = "reachedTarget")
  GameResultDto toDto(GameState gameState);
}
