package com.bigbank.dragons.api.handler;

import com.bigbank.dragons.api.exception.GameNotFoundException;
import com.bigbank.dragons.api.exception.InvalidStrategyException;
import com.bigbank.dragons.client.exception.MugloarApiException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
  }

  private static Violation toViolation(ConstraintViolation<?> v) {
    String path = v.getPropertyPath().toString();
    String param = path.substring(path.lastIndexOf('.') + 1);
    return new Violation(param, v.getMessage());
  }

  public record Violation(String field, String message) {}
}
