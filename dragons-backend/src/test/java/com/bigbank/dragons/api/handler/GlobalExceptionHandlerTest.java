package com.bigbank.dragons.api.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.bigbank.dragons.api.exception.GameNotFoundException;
import com.bigbank.dragons.api.exception.InvalidStrategyException;
import com.bigbank.dragons.client.exception.MugloarApiException;
import com.bigbank.dragons.client.exception.MugloarRateLimitException;
import com.bigbank.dragons.client.exception.MugloarUnavailableException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.ResourceAccessException;

public class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
  }

  @Test
  @SuppressWarnings("unchecked")
  void handleConstraintViolationsExtractsViolationsAndReturnsBadRequest() {
    ConstraintViolation<?> violation = mock(ConstraintViolation.class);
    Path path = mock(Path.class);

    when(path.toString()).thenReturn("class.method.paramName");
    when(violation.getPropertyPath()).thenReturn(path);
    when(violation.getMessage()).thenReturn("must not be blank");

    ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));
    ResponseEntity<ProblemDetail> response = handler.handleConstraintViolations(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    ProblemDetail pd = response.getBody();
    assertNotNull(pd);
    assertEquals("Validation failed for 1 parameter(s)", pd.getDetail());
    assertEquals("Invalid request", pd.getTitle());

    List<GlobalExceptionHandler.Violation> violations =
        (List<GlobalExceptionHandler.Violation>) pd.getProperties().get("violations");

    assertEquals(1, violations.size());
    assertEquals("paramName", violations.getFirst().field());
    assertEquals("must not be blank", violations.getFirst().message());
  }

  @Test
  @SuppressWarnings("unchecked")
  void handleBodyValidationExtractsViolationsAndReturnsBadRequest() {
    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    BindingResult bindingResult = mock(BindingResult.class);
    FieldError fieldError = new FieldError("objectName", "fieldName", "must not be null");

    when(ex.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

    ResponseEntity<ProblemDetail> response = handler.handleBodyValidation(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    ProblemDetail pd = response.getBody();
    assertNotNull(pd);
    assertEquals("Validation failed for 1 field(s)", pd.getDetail());
    assertEquals("Invalid request body", pd.getTitle());

    List<GlobalExceptionHandler.Violation> violations =
        (List<GlobalExceptionHandler.Violation>) pd.getProperties().get("violations");

    assertEquals(1, violations.size());
    assertEquals("fieldName", violations.getFirst().field());
    assertEquals("must not be null", violations.getFirst().message());
  }

  @Test
  void handleBadInputReturnsBadRequest() {
    IllegalArgumentException ex = new IllegalArgumentException("Bad input detected");
    ResponseEntity<ProblemDetail> response = handler.handleBadInput(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Bad input detected", response.getBody().getDetail());
    assertEquals("Invalid request", response.getBody().getTitle());
  }

  @Test
  void handleNotFoundReturnsNotFound() {
    GameNotFoundException ex = new GameNotFoundException("Game 123 not found");
    ResponseEntity<ProblemDetail> response = handler.handleNotFound(ex);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Game 123 not found", response.getBody().getDetail());
    assertEquals("Game not found", response.getBody().getTitle());
  }

  @Test
  void handleInvalidStrategyReturnsBadRequest() {
    InvalidStrategyException ex = new InvalidStrategyException("Unknown strategy key");
    ResponseEntity<ProblemDetail> response = handler.handleInvalidStrategy(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Unknown strategy key", response.getBody().getDetail());
    assertEquals("Invalid strategy", response.getBody().getTitle());
  }

  @Test
  void handleUpstreamReturnsBadGateway() {
    MugloarApiException ex = new MugloarApiException("Mugloar API is down");
    ResponseEntity<ProblemDetail> response = handler.handleUpstream(ex);

    assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
    assertEquals("Mugloar API is down", response.getBody().getDetail());
    assertEquals("Game API error", response.getBody().getTitle());
  }

  @Test
  void handleUnexpectedReturnsInternalServerError() {
    Exception ex = new Exception("Something blew up completely");
    ResponseEntity<ProblemDetail> response = handler.handleUnexpected(ex);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(
        "An unexpected error occurred: Something blew up completely",
        response.getBody().getDetail());
    assertEquals("Internal error", response.getBody().getTitle());
  }

  @Test
  void handleIllegalStateReturnsBadRequest() {
    IllegalStateException ex = new IllegalStateException("Game is already over");
    ResponseEntity<ProblemDetail> response = handler.handleIllegalState(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Game is already over", response.getBody().getDetail());
    assertEquals("Invalid game action", response.getBody().getTitle());
  }

  @Test
  void handleRateLimitedReturns503WithRetryAfterHeader() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    ResponseEntity<ProblemDetail> response =
        handler.handleRateLimited(new MugloarRateLimitException("429", Duration.ofSeconds(5)));

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    assertEquals("5", response.getHeaders().getFirst(HttpHeaders.RETRY_AFTER));
    assertEquals(5L, response.getBody().getProperties().get("retryAfterSeconds"));
  }

  @Test
  void handleRateLimitedWithoutRetryAfterOmitsHeader() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    ResponseEntity<ProblemDetail> response =
        handler.handleRateLimited(new MugloarRateLimitException("429", null));

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    assertNull(response.getHeaders().getFirst(HttpHeaders.RETRY_AFTER));
  }

  @Test
  void handleUpstreamUnavailableReturns503() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    assertEquals(
        HttpStatus.SERVICE_UNAVAILABLE,
        handler.handleUpstreamUnavailable(new MugloarUnavailableException("down")).getStatusCode());
  }

  @Test
  void handleTransportReturns502() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    assertEquals(
        HttpStatus.BAD_GATEWAY,
        handler.handleTransport(new ResourceAccessException("timeout")).getStatusCode());
  }

  @Test
  void handleClientDisconnectWritesNothing() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    assertDoesNotThrow(
        () -> handler.handleClientDisconnect(new java.io.IOException("broken pipe")));
  }
}
