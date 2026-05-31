package com.bigbank.dragons.api.handler;

import com.bigbank.dragons.api.exception.GameNotFoundException;
import com.bigbank.dragons.api.exception.InvalidStrategyException;
import com.bigbank.dragons.client.exception.MugloarApiException;
import com.bigbank.dragons.client.exception.MugloarRateLimitException;
import com.bigbank.dragons.client.exception.MugloarUnavailableException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ProblemDetail> handleConstraintViolations(ConstraintViolationException ex) {
    List<Violation> violations =
        ex.getConstraintViolations().stream().map(GlobalExceptionHandler::toViolation).toList();
    ProblemDetail pd =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Validation failed for " + violations.size() + " parameter(s)");
    pd.setTitle("Invalid request");
    pd.setProperty("timestamp", Instant.now());
    pd.setProperty("violations", violations);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleBodyValidation(MethodArgumentNotValidException ex) {
    List<Violation> violations =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> new Violation(fe.getField(), fe.getDefaultMessage()))
            .toList();
    ProblemDetail pd =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Validation failed for " + violations.size() + " field(s)");
    pd.setTitle("Invalid request body");
    pd.setProperty("timestamp", Instant.now());
    pd.setProperty("violations", violations);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ProblemDetail> handleBadInput(IllegalArgumentException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    pd.setTitle("Invalid request");
    pd.setProperty("timestamp", Instant.now());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
  }

  @ExceptionHandler(GameNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleNotFound(GameNotFoundException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    pd.setTitle("Game not found");
    pd.setProperty("timestamp", Instant.now());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
  }

  @ExceptionHandler(InvalidStrategyException.class)
  public ResponseEntity<ProblemDetail> handleInvalidStrategy(InvalidStrategyException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    pd.setTitle("Invalid strategy");
    pd.setProperty("timestamp", Instant.now());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
  }

  @ExceptionHandler(MugloarApiException.class)
  public ResponseEntity<ProblemDetail> handleUpstream(MugloarApiException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.getMessage());
    pd.setTitle("Game API error");
    pd.setProperty("timestamp", Instant.now());

    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(pd);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex) {
    ProblemDetail pd =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage());
    pd.setTitle("Internal error");
    pd.setProperty("timestamp", Instant.now());
    log.error(Arrays.toString(ex.getStackTrace())); // Only log for internal server error

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(pd);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ProblemDetail> handleIllegalState(IllegalStateException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    pd.setTitle("Invalid game action");
    pd.setProperty("timestamp", Instant.now());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
  }

  @ExceptionHandler(MugloarRateLimitException.class)
  public ResponseEntity<ProblemDetail> handleRateLimited(MugloarRateLimitException ex) {
    ProblemDetail pd =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.SERVICE_UNAVAILABLE,
            "The game service is rate limiting requests. Please retry shortly.");
    pd.setTitle("Upstream rate limited");
    pd.setProperty("timestamp", Instant.now());

    ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE);
    if (ex.getRetryAfter() != null) {
      long seconds = Math.max(0, ex.getRetryAfter().toSeconds());
      pd.setProperty("retryAfterSeconds", seconds);
      builder.header(HttpHeaders.RETRY_AFTER, Long.toString(seconds));
    }
    return builder.body(pd);
  }

  @ExceptionHandler(MugloarUnavailableException.class)
  public ResponseEntity<ProblemDetail> handleUpstreamUnavailable(MugloarUnavailableException ex) {
    ProblemDetail pd =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.SERVICE_UNAVAILABLE, "The game service is temporarily unavailable.");
    pd.setTitle("Upstream unavailable");
    pd.setProperty("timestamp", Instant.now());
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(pd);
  }

  @ExceptionHandler(RestClientException.class)
  public ResponseEntity<ProblemDetail> handleTransport(RestClientException ex) {
    ProblemDetail pd =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_GATEWAY, "Could not reach the game service: " + ex.getMessage());
    pd.setTitle("Upstream connection error");
    pd.setProperty("timestamp", Instant.now());
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(pd);
  }

  /**
   * A broken client connection (an SSE client closed the stream). The response is already
   * committed/aborted, so there is nothing to write — returning void tells Spring not to render a
   * ProblemDetail (which can't be serialized to text/event-stream).
   */
  @ExceptionHandler(java.io.IOException.class)
  public void handleClientDisconnect(java.io.IOException ex) {
    log.debug("Client disconnected before the response completed: {}", ex.getMessage());
  }

  private static Violation toViolation(ConstraintViolation<?> v) {
    String path = v.getPropertyPath().toString();
    String param = path.substring(path.lastIndexOf('.') + 1);
    return new Violation(param, v.getMessage());
  }

  public record Violation(String field, String message) {}
}
