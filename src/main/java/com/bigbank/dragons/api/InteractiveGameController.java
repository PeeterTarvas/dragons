package com.bigbank.dragons.api;

import com.bigbank.dragons.api.dto.*;
import com.bigbank.dragons.api.mapper.ApiMapper;
import com.bigbank.dragons.domain.Board;
import com.bigbank.dragons.service.InteractiveGameService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
@Validated
@Tag(name = "Interactive Game", description = "Turn-by-turn interactive play")
@RequiredArgsConstructor
public class InteractiveGameController {

  private final InteractiveGameService service;
  private final ApiMapper apiMapper;

  @GetMapping("/play")
  public GameStateDto start() {
    return apiMapper.toGameStateDto(service.startGame());
  }

  @GetMapping("/{gameId}/board")
  public BoardDto board(
      @PathVariable @NotBlank String gameId, @RequestParam(required = false) String strategy) {
    Board board =
        Optional.ofNullable(strategy)
            .map(strat -> service.getBoard(gameId, strat))
            .orElseGet(() -> service.getBoard(gameId));
    return apiMapper.toDto(board);
  }

  @PostMapping("/{gameId}/solve")
  public SolveResponseDto solve(
      @PathVariable @NotBlank String gameId, @Valid @RequestBody AdDto ad) {
    return apiMapper.toDto(service.solveAd(gameId, apiMapper.toDomain(ad)));
  }

  @GetMapping("/{gameId}/shop")
  public List<ShopItemDto> shop(@PathVariable @NotBlank String gameId) {
    return apiMapper.toListDto(service.getShop(gameId));
  }

  @PostMapping("/{gameId}/buy")
  public BuyResponseDto buy(
      @PathVariable @NotBlank String gameId, @Valid @RequestBody ShopItemDto itemId) {
    return apiMapper.toDto(service.buyItem(gameId, apiMapper.toDomain(itemId)));
  }
}
