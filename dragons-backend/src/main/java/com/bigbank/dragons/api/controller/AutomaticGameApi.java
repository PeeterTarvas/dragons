package com.bigbank.dragons.api.controller;

import com.bigbank.dragons.api.dto.BatchStatsDto;
import com.bigbank.dragons.api.dto.GameResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequestMapping("/api")
@Tag(name = "Game", description = "Automatic single, batch and streamed play")
public interface AutomaticGameApi {

  @Operation(
      summary = "Play one game automatically",
      description =
          "Runs a complete game using the given strategy (falling back to the configured default) "
              + "and returns the final state and log.")
  @ApiResponse(
      responseCode = "200",
      description = "Game completed",
      content = @Content(schema = @Schema(implementation = GameResultDto.class)))
  @ApiResponse(
      responseCode = "502",
      description = "Upstream game API returned an error",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  @ApiResponse(
      responseCode = "503",
      description = "Upstream game API is rate limiting or unavailable",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  @PostMapping("/play")
  @ResponseStatus(HttpStatus.OK)
  GameResultDto play(
      @Parameter(
              description = "Strategy key; defaults to the configured strategy",
              example = "expected-value")
          @RequestParam(required = false)
          String strategy);

  @Operation(
      summary = "Play a batch of games",
      description = "Runs the requested number of games concurrently and returns aggregate stats.")
  @ApiResponse(
      responseCode = "200",
      description = "Batch completed",
      content = @Content(schema = @Schema(implementation = BatchStatsDto.class)))
  @ApiResponse(
      responseCode = "400",
      description = "games out of the allowed 1..500 range",
      content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
  @PostMapping("/play/batch")
  @ResponseStatus(HttpStatus.OK)
  BatchStatsDto playBatch(
      @Parameter(description = "Number of games to run (1..500)", example = "50")
          @RequestParam(defaultValue = "2")
          @Min(1)
          @Max(500)
          int games,
      @Parameter(
              description = "Strategy key; defaults to the configured strategy",
              example = "expected-value")
          @RequestParam(required = false)
          String strategy);

  @Operation(
      summary = "List available strategies",
      description = "Returns the strategy keys accepted by the play/stream endpoints.")
  @ApiResponse(
      responseCode = "200",
      description = "Strategy keys",
      content =
          @Content(array = @ArraySchema(schema = @Schema(type = "string", example = "low-risk"))))
  @GetMapping("/strategies")
  @ResponseStatus(HttpStatus.OK)
  List<String> strategies();

  @Operation(
      summary = "Stream a game (Server-Sent Events)",
      description =
          "Plays a game and streams the game state after every turn as SSE `turn` events until the "
              + "game ends.")
  @ApiResponse(
      responseCode = "200",
      description = "SSE stream of game-state events",
      content =
          @Content(
              mediaType = MediaType.TEXT_EVENT_STREAM_VALUE,
              schema = @Schema(implementation = GameResultDto.class)))
  @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  @ResponseStatus(HttpStatus.OK)
  SseEmitter streamGame(
      @Parameter(
              description = "Strategy key; defaults to the configured strategy",
              example = "expected-value")
          @RequestParam(required = false)
          String strategy);
}
