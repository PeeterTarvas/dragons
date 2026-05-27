package com.bigbank.dragons.api.handler;

import com.bigbank.dragons.api.exception.InvalidStrategyException;
import com.bigbank.dragons.client.exception.MugloarApiException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  /** Bad input that reached us (blank ids, invalid args). -> 400 */
  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleBadInput(IllegalArgumentException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    pd.setTitle("Invalid request");
    pd.setProperty("timestamp", Instant.now());
    return pd;
  }

  /** Upstream game API failed or returned nothing useful. -> 502 */
  @ExceptionHandler(MugloarApiException.class)
  public ProblemDetail handleUpstream(MugloarApiException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.getMessage());
    pd.setTitle("Game API error");
    pd.setProperty("timestamp", Instant.now());
    return pd;
  }

  /** Anything unforeseen. -> 500, without leaking internals. */
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleUnexpected(Exception ex) {
    ProblemDetail pd =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage());
    pd.setTitle("Internal error");
    pd.setProperty("timestamp", Instant.now());
    return pd;
  }

  @ExceptionHandler(InvalidStrategyException.class)
  public ProblemDetail handleInvalidStrategy(InvalidStrategyException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    pd.setTitle("Invalid strategy");
    return pd;
  }
}
