package com.bigbank.dragons.mapper;

import com.bigbank.dragons.client.dto.StartGameResponseDto;
import com.bigbank.dragons.game.state.GameState;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameStateMapper {


    @Mapping(target = "log", ignore = true)
    @Mapping(target = "reachedGoal", constant = "false")
    GameState toEntity(StartGameResponseDto response);
}
