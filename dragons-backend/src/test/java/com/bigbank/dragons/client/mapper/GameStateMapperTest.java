package com.bigbank.dragons.client.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bigbank.dragons.client.dto.StartGameResponseClientDto;
import com.bigbank.dragons.game.state.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameStateMapperTest {

  private GameStateMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new GameStateMapperImpl();
  }

  @Test
  void toEntityReturnsNullWhenInputIsNull() {
    assertNull(mapper.toEntity(null));
  }

  @Test
  void toEntityMapsAllMappedFields() {
    StartGameResponseClientDto dto =
        new StartGameResponseClientDto("g-99", 3, 100, 1, 400, 9999, 5);
    GameState state = mapper.toEntity(dto);
    assertEquals("g-99", state.getGameId());
    assertEquals(3, state.getLives());
    assertEquals(100, state.getGold());
    assertEquals(1, state.getLevel());
    assertEquals(400.0, state.getScore());
    assertEquals(5, state.getTurn());
  }

  @Test
  void toEntityAlwaysSetsReachedGoalFalseRegardlessOfDtoContent() {
    StartGameResponseClientDto dto = new StartGameResponseClientDto("g-1", 5, 200, 2, 999, 999, 10);
    GameState state = mapper.toEntity(dto);
    assertFalse(state.isReachedGoal());
  }

  @Test
  void toEntityInitializesLogAsEmptyNonNullList() {
    StartGameResponseClientDto dto = new StartGameResponseClientDto("g-1", 3, 100, 1, 0, 0, 0);
    GameState state = mapper.toEntity(dto);
    assertNotNull(state.getLog());
    assertTrue(state.getLog().isEmpty());
  }

  @Test
  void toEntityIgnoresHighScoreFieldFromDto() {
    StartGameResponseClientDto dto = new StartGameResponseClientDto("g-1", 3, 100, 1, 500, 9999, 5);
    GameState state = mapper.toEntity(dto);
    assertEquals(500.0, state.getScore());
  }
}
