package com.bigbank.dragons.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bigbank.dragons.api.dto.*;
import com.bigbank.dragons.domain.BatchStats;
import com.bigbank.dragons.domain.Board;
import com.bigbank.dragons.domain.BuyResponse;
import com.bigbank.dragons.domain.EvaluatedMessage;
import com.bigbank.dragons.domain.Message;
import com.bigbank.dragons.domain.ShopItem;
import com.bigbank.dragons.domain.SolveResponse;
import com.bigbank.dragons.domain.TurnLog;
import com.bigbank.dragons.game.state.GameState;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ApiMapperTest {

  private ApiMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new ApiMapperImpl();
  }

  @Test
  void toDtoBatchStatsReturnsNullWhenInputIsNull() {
    assertNull(mapper.toDto((BatchStats) null));
  }

  @Test
  void toDtoBatchStatsMapsAllFields() {
    BatchStats domain = new BatchStats(5, 1200.0, 1500.0, 900.0, 3L, 60.0);
    BatchStatsDto dto = mapper.toDto(domain);
    assertEquals(5, dto.games());
    assertEquals(1200.0, dto.averageScore());
    assertEquals(1500.0, dto.maxScore());
    assertEquals(900.0, dto.minScore());
    assertEquals(3L, dto.gamesReachedTarget());
    assertEquals(60.0, dto.reachedTargetPercent());
  }

  @Test
  void toDtoTurnLogReturnsNullWhenInputIsNull() {
    assertNull(mapper.toDto((TurnLog) null));
  }

  @Test
  void toDtoTurnLogMapsAllFields() {
    TurnLog domain = new TurnLog(3, "You won!", "Sure thing", true, 150.0, 2, 100);
    TurnLogDto dto = mapper.toDto(domain);
    assertEquals(3, dto.turn());
    assertEquals("You won!", dto.message());
    assertEquals("Sure thing", dto.probability());
    assertTrue(dto.success());
    assertEquals(150.0, dto.score());
    assertEquals(2, dto.lives());
    assertEquals(100, dto.gold());
  }

  @Test
  void toGameStateDtoReturnsNullWhenInputIsNull() {
    assertNull(mapper.toGameStateDto(null));
  }

  @Test
  void toGameStateDtoMapsAllFields() {
    GameState state = new GameState("g-1", 3, 200, 2, 750.0, 5, false);
    GameStateDto dto = mapper.toGameStateDto(state);
    assertEquals("g-1", dto.gameId());
    assertEquals(3, dto.lives());
    assertEquals(200, dto.gold());
    assertEquals(2, dto.level());
    assertEquals(750.0, dto.score());
    assertEquals(5, dto.turn());
    assertFalse(dto.reachedGoal());
  }

  @Test
  void toDtoSolveResponseReturnsNullWhenInputIsNull() {
    assertNull(mapper.toDto((SolveResponse) null));
  }

  @Test
  void toDtoSolveResponseConvertsDoubleScoreAndHighScoreToInteger() {
    SolveResponse domain = new SolveResponse(true, 3, 150, 1234.7, 5678.9, 7, "Well done");
    SolveResponseDto dto = mapper.toDto(domain);
    assertTrue(dto.success());
    assertEquals(3, dto.lives());
    assertEquals(150, dto.gold());
    assertEquals(1234, dto.score());
    assertEquals(5678, dto.highScore());
    assertEquals(7, dto.turn());
    assertEquals("Well done", dto.message());
  }

  @Test
  void toDtoSolveResponseLeavesScoreNullWhenDomainScoreIsNull() {
    SolveResponse domain = new SolveResponse(false, 0, 0, null, 100.0, 1, "Fail");
    SolveResponseDto dto = mapper.toDto(domain);
    assertNull(dto.score());
    assertEquals(100, dto.highScore());
  }

  @Test
  void toDtoSolveResponseLeavesHighScoreNullWhenDomainHighScoreIsNull() {
    SolveResponse domain = new SolveResponse(false, 0, 0, 200.0, null, 1, "Fail");
    SolveResponseDto dto = mapper.toDto(domain);
    assertEquals(200, dto.score());
    assertNull(dto.highScore());
  }

  @Test
  void toListDto_returnsNullWhenInputIsNull() {
    assertNull(mapper.toListDto(null));
  }

  @Test
  void toListDtoReturnsEmptyListWhenInputIsEmpty() {
    List<ShopItemDto> result = mapper.toListDto(List.of());
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void toListDtoMapsItemsAndNullElementPassesThroughAsNull() {
    ShopItem item = new ShopItem("s-1", "Sword", 30);
    List<ShopItem> input = Arrays.asList(item, null);
    List<ShopItemDto> result = mapper.toListDto(input);
    assertEquals(2, result.size());
    assertEquals("s-1", result.get(0).id());
    assertEquals("Sword", result.get(0).name());
    assertEquals(30, result.get(0).cost());
    assertNull(result.get(1));
  }

  @Test
  void toDomainShopItemDtoReturnsNullWhenInputIsNull() {
    assertNull(mapper.toDomain((ShopItemDto) null));
  }

  @Test
  void toDomainShopItemDtoMapsAllFields() {
    ShopItemDto dto = new ShopItemDto("s-1", "Shield", 45);
    ShopItem domain = mapper.toDomain(dto);
    assertEquals("s-1", domain.id());
    assertEquals("Shield", domain.name());
    assertEquals(45, domain.cost());
  }

  @Test
  void toDomainAdDtoReturnsNullWhenInputIsNull() {
    assertNull(mapper.toDomain((MessageDto) null));
  }

  @Test
  void toDomainAdDtoMapsAllFieldsAndIgnoresEstimatedSuccess() {
    MessageDto dto = new MessageDto("ad-1", "Fight a dragon", 500, 3, "Risky", 0, 0.35);
    Message domain = mapper.toDomain(dto);
    assertEquals("ad-1", domain.adId());
    assertEquals("Fight a dragon", domain.message());
    assertEquals(500, domain.reward());
    assertEquals(3, domain.expiresIn());
    assertEquals(0, domain.encrypted());
    assertEquals("Risky", domain.probability());
  }

  @Test
  void toDtoBoardReturnsNullWhenInputIsNull() {
    assertNull(mapper.toDto((Board) null));
  }

  @Test
  void toDtoBoardMapsNullMessageListToNullAds() {
    Board board = new Board(null, "ad-42");
    BoardDto dto = mapper.toDto(board);
    assertNull(dto.ads());
    assertEquals("ad-42", dto.recommendedAdId());
  }

  @Test
  void toDtoBoardMapsMessagesToAds() {
    Message msg = new Message("ad-1", "Hunt", 100, 5, null, "Piece of cake");
    EvaluatedMessage em = new EvaluatedMessage(msg, 0.9);
    Board board = new Board(List.of(em), "ad-1");
    BoardDto dto = mapper.toDto(board);
    assertEquals(1, dto.ads().size());
    assertEquals("ad-1", dto.ads().get(0).adId());
    assertEquals("ad-1", dto.recommendedAdId());
  }

  @Test
  void toAdDtoReturnsNullWhenInputIsNull() {
    assertNull(mapper.toAdDto(null));
  }

  @Test
  void toAdDtoMapsAllFieldsFromInnerMessage() {
    Message msg = new Message("ad-2", "Deliver goods", 200, 4, 1, "Quite likely");
    EvaluatedMessage em = new EvaluatedMessage(msg, 0.7);
    MessageDto dto = mapper.toAdDto(em);
    assertEquals("ad-2", dto.adId());
    assertEquals("Deliver goods", dto.message());
    assertEquals(200, dto.reward());
    assertEquals(4, dto.expiresIn());
    assertEquals("Quite likely", dto.probability());
    assertEquals(1, dto.encrypted());
    assertEquals(0.7, dto.estimatedSuccess());
  }

  @Test
  void toAdDtoReturnsNullFieldsWhenInnerMessageIsNull() {
    EvaluatedMessage em = new EvaluatedMessage(null, 0.5);
    MessageDto dto = mapper.toAdDto(em);
    assertNotNull(dto);
    assertNull(dto.adId());
    assertNull(dto.message());
    assertNull(dto.reward());
    assertNull(dto.expiresIn());
    assertNull(dto.probability());
    assertNull(dto.encrypted());
    assertEquals(0.5, dto.estimatedSuccess());
  }

  @Test
  void toGameResultDtoReturnsNullWhenInputIsNull() {
    assertNull(mapper.toGameResultDto(null));
  }

  @Test
  void toGameResultDtoMapsGameStateDtoAndEmptyLog() {
    GameState state = new GameState("g-2", 2, 100, 1, 500.0, 3, false);
    GameResultDto dto = mapper.toGameResultDto(state);
    assertNotNull(dto.gameStateDto());
    assertEquals("g-2", dto.gameStateDto().gameId());
    assertNotNull(dto.log());
    assertTrue(dto.log().isEmpty());
  }

  @Test
  void toGameResultDtoMapsLogEntries() {
    GameState state = new GameState("g-3", 3, 200, 1, 800.0, 5, true);
    state.addLog(new TurnLog(1, "Win", "Sure thing", true, 800.0, 3, 200));
    GameResultDto dto = mapper.toGameResultDto(state);
    assertEquals(1, dto.log().size());
    assertEquals(1, dto.log().getFirst().turn());
    assertEquals("Win", dto.log().getFirst().message());
  }

  @Test
  void toDtoBuyResponseReturnsNullWhenInputIsNull() {
    assertNull(mapper.toDto((BuyResponse) null));
  }

  @Test
  void toDtoBuyResponseMapsAllFields() {
    BuyResponse domain = new BuyResponse(true, 150, 4, 3, 8);
    BuyResponseDto dto = mapper.toDto(domain);
    assertTrue(dto.shoppingSuccess());
    assertEquals(150, dto.gold());
    assertEquals(4, dto.lives());
    assertEquals(3, dto.level());
    assertEquals(8, dto.turn());
  }

  @Test
  void turnLogListToTurnLogDtoListReturnsNullWhenInputIsNull() {
    ApiMapperImpl impl = (ApiMapperImpl) mapper;
    assertNull(impl.turnLogListToTurnLogDtoList(null));
  }
}
