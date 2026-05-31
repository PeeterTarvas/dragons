package com.bigbank.dragons.client.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClientMapperTest {

  private ClientMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new ClientMapperImpl();
  }

  @Test
  void toDomainMessageDtoReturnsNullWhenInputIsNull() {
    assertNull(mapper.toDomain((MessageDto) null));
  }

  @Test
  void toDomainMessageDtoMapsAllFields() {
    MessageDto dto = new MessageDto("ad-1", "Fight the king", 300, 5, 2, "Risky");
    Message domain = mapper.toDomain(dto);
    assertEquals("ad-1", domain.adId());
    assertEquals("Fight the king", domain.message());
    assertEquals(300, domain.reward());
    assertEquals(5, domain.expiresIn());
    assertEquals(2, domain.encrypted());
    assertEquals("Risky", domain.probability());
  }

  @Test
  void toDomainMessageDtoMapsNullEncryptedField() {
    MessageDto dto = new MessageDto("ad-2", "Plain task", 100, 3, null, "Sure thing");
    Message domain = mapper.toDomain(dto);
    assertNull(domain.encrypted());
  }

  @Test
  void toDomainReputationDtoReturnsNullWhenInputIsNull() {
    assertNull(mapper.toDomain((ReputationDto) null));
  }

  @Test
  void toDomainReputationDtoMapsAllFields() {
    ReputationDto dto = new ReputationDto(0.8, -0.3, 0.5);
    Reputation domain = mapper.toDomain(dto);
    assertEquals(0.8, domain.people());
    assertEquals(-0.3, domain.state());
    assertEquals(0.5, domain.underworld());
  }

  @Test
  void toDomainShopItemDtoReturnsNullWhenInputIsNull() {
    assertNull(mapper.toDomain((ShopItemDto) null));
  }

  @Test
  void toDomainShopItemDtoMapsAllFields() {
    ShopItemDto dto = new ShopItemDto("item-1", "Healing Potion", 50);
    ShopItem domain = mapper.toDomain(dto);
    assertEquals("item-1", domain.id());
    assertEquals("Healing Potion", domain.name());
    assertEquals(50, domain.cost());
  }

  @Test
  void toDomainBuyResponseDtoReturnsNullWhenInputIsNull() {
    assertNull(mapper.toDomain((BuyResponseClientDto) null));
  }

  @Test
  void toDomainBuyResponseDtoMapsAllFields() {
    BuyResponseClientDto dto = new BuyResponseClientDto(true, 200, 3, 2, 7);
    BuyResponse domain = mapper.toDomain(dto);
    assertTrue(domain.shoppingSuccess());
    assertEquals(200, domain.gold());
    assertEquals(3, domain.lives());
    assertEquals(2, domain.level());
    assertEquals(7, domain.turn());
  }

  @Test
  void toDomainSolveResponseDtoReturnsNullWhenInputIsNull() {
    assertNull(mapper.toDomain((SolveResponseDto) null));
  }

  @Test
  void toDomainSolveResponseDtoMapsAllFieldsPreservingDoubleScoreAndHighScore() {
    SolveResponseDto dto = new SolveResponseDto(true, 2, 150, 1500.5, 3000.75, 10, "Excellent!");
    SolveResponse domain = mapper.toDomain(dto);
    assertTrue(domain.success());
    assertEquals(2, domain.lives());
    assertEquals(150, domain.gold());
    assertEquals(1500.5, domain.score());
    assertEquals(3000.75, domain.highScore());
    assertEquals(10, domain.turn());
    assertEquals("Excellent!", domain.message());
  }

  @Test
  void toDtoReputationReturnsNullWhenInputIsNull() {
    assertNull(mapper.toDto(null));
  }

  @Test
  void toDtoReputationMapsAllNonNullFields() {
    Reputation domain = new Reputation(0.7, -0.2, 0.4);
    ReputationDto dto = mapper.toDto(domain);
    assertEquals(0.7, dto.people());
    assertEquals(-0.2, dto.state());
    assertEquals(0.4, dto.underworld());
  }

  @Test
  void toDtoReputationDefaultsPeopleToZeroWhenNull() {
    Reputation domain = new Reputation(null, 0.5, 0.3);
    ReputationDto dto = mapper.toDto(domain);
    assertEquals(0.0, dto.people());
    assertEquals(0.5, dto.state());
    assertEquals(0.3, dto.underworld());
  }

  @Test
  void toDtoReputationDefaultsStateToZeroWhenNull() {
    Reputation domain = new Reputation(0.5, null, 0.3);
    ReputationDto dto = mapper.toDto(domain);
    assertEquals(0.5, dto.people());
    assertEquals(0.0, dto.state());
    assertEquals(0.3, dto.underworld());
  }

  @Test
  void toDtoReputationDefaultsUnderworldToZeroWhenNull() {
    Reputation domain = new Reputation(0.5, 0.3, null);
    ReputationDto dto = mapper.toDto(domain);
    assertEquals(0.5, dto.people());
    assertEquals(0.3, dto.state());
    assertEquals(0.0, dto.underworld());
  }
}
