package com.bigbank.dragons.api.controller;

import com.bigbank.dragons.api.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/games")
@Tag(name = "Interactive Game", description = "Turn-by-turn interactive play")
public interface InteractiveGameApi {

  @Operation(
      summary = "Start a new game",
      description =
          "Starts a fresh game on the upstream Mugloar API and returns its initial state.")
  @ApiResponse(
      responseCode = "200",
      description = "Game started",
      content = @Content(schema = @Schema(implementation = GameStateDto.class)))
  @ApiResponse(
      responseCode = "502",
      description = "Upstream game API returned an error",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  @ApiResponse(
      responseCode = "503",
      description = "Upstream game API is rate limiting or unavailable",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  @GetMapping("/play")
  @ResponseStatus(HttpStatus.OK)
  GameStateDto start();

  @Operation(
      summary = "Get the message board",
      description =
          "Returns the ads available to solve, sorted by estimated success. When a strategy is "
              + "supplied, the recommended ad ID is populated.")
  @ApiResponse(
      responseCode = "200",
      description = "Board returned",
      content = @Content(schema = @Schema(implementation = BoardDto.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Unknown strategy or invalid game ID",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  @ApiResponse(
      responseCode = "404",
      description = "Game not found",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  @GetMapping("/{gameId}/board")
  @ResponseStatus(HttpStatus.OK)
  BoardDto board(
      @Parameter(description = "Unique game ID", example = "mAg6juyHkfBp") @PathVariable @NotBlank
          String gameId,
      @Parameter(description = "Recommendation strategy key", example = "expected-value")
          @RequestParam(required = false)
          String strategy);

  @Operation(
      summary = "Solve a message",
      description = "Attempts to solve the supplied ad and returns the outcome of the turn.")
  @ApiResponse(
      responseCode = "200",
      description = "Solve attempt processed",
      content = @Content(schema = @Schema(implementation = SolveResponseDto.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Validation error or the game is no longer active",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  @ApiResponse(
      responseCode = "404",
      description = "Game not found",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  @PostMapping("/{gameId}/solve")
  @ResponseStatus(HttpStatus.OK)
  SolveResponseDto solve(
      @Parameter(description = "Unique game ID", example = "mAg6juyHkfBp") @PathVariable @NotBlank
          String gameId,
      @Valid @RequestBody AdDto ad);

  @Operation(summary = "List shop items", description = "Returns the items available to purchase.")
  @ApiResponse(
      responseCode = "200",
      description = "Shop listing returned",
      content =
          @Content(array = @ArraySchema(schema = @Schema(implementation = ShopItemDto.class))))
  @ApiResponse(
      responseCode = "404",
      description = "Game not found",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  @GetMapping("/{gameId}/shop")
  @ResponseStatus(HttpStatus.OK)
  List<ShopItemDto> shop(
      @Parameter(description = "Unique game ID", example = "mAg6juyHkfBp") @PathVariable @NotBlank
          String gameId);

  @Operation(
      summary = "Purchase a shop item",
      description = "Buys the supplied item. The turn advances even if the purchase fails.")
  @ApiResponse(
      responseCode = "200",
      description = "Purchase processed",
      content = @Content(schema = @Schema(implementation = BuyResponseDto.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Validation error or the game is no longer active",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  @ApiResponse(
      responseCode = "404",
      description = "Game not found",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  @PostMapping("/{gameId}/buy")
  @ResponseStatus(HttpStatus.OK)
  BuyResponseDto buy(
      @Parameter(description = "Unique game ID", example = "mAg6juyHkfBp") @PathVariable @NotBlank
          String gameId,
      @Valid @RequestBody ShopItemDto itemId);

  @Operation(
      summary = "Get current state and log",
      description = "Returns the current game state together with the full per-turn log.")
  @ApiResponse(
      responseCode = "200",
      description = "State and log returned",
      content = @Content(schema = @Schema(implementation = GameResultDto.class)))
  @ApiResponse(
      responseCode = "404",
      description = "Game not found",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  @GetMapping("/{gameId}/state")
  @ResponseStatus(HttpStatus.OK)
  GameResultDto state(
      @Parameter(description = "Unique game ID", example = "mAg6juyHkfBp") @PathVariable @NotBlank
          String gameId);
}
