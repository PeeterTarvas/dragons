package com.bigbank.dragons.client.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.bigbank.dragons.client.exception.MugloarApiException;
import com.bigbank.dragons.client.exception.MugloarRateLimitException;
import com.bigbank.dragons.client.exception.MugloarUnavailableException;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

class MugloarResponseErrorHandlerTest {

  private static final URI URL = URI.create("/api/v2/game/start");

  private MugloarResponseErrorHandler handler;

  @BeforeEach
  void setUp() {
    handler = new MugloarResponseErrorHandler();
  }

  @Test
  void hasErrorTrueForErrorStatus() throws IOException {
    ClientHttpResponse response = mock(ClientHttpResponse.class);
    when(response.getStatusCode()).thenReturn(HttpStatus.TOO_MANY_REQUESTS);
    assertTrue(handler.hasError(response));
  }

  @Test
  void hasErrorFalseForSuccessStatus() throws IOException {
    ClientHttpResponse response = mock(ClientHttpResponse.class);
    when(response.getStatusCode()).thenReturn(HttpStatus.OK);
    assertFalse(handler.hasError(response));
  }

  @Test
  void translates429ToRateLimitWithParsedRetryAfter() throws IOException {
    ClientHttpResponse response = mock(ClientHttpResponse.class);
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.RETRY_AFTER, "7");
    when(response.getStatusCode()).thenReturn(HttpStatus.TOO_MANY_REQUESTS);
    when(response.getHeaders()).thenReturn(headers);

    MugloarRateLimitException ex =
        assertThrows(
            MugloarRateLimitException.class,
            () -> handler.handleError(URL, HttpMethod.GET, response));

    assertEquals(7, ex.getRetryAfter().toSeconds());
  }

  @Test
  void translates429WithoutRetryAfterHeaderToZeroDuration() throws IOException {
    ClientHttpResponse response = mock(ClientHttpResponse.class);
    when(response.getStatusCode()).thenReturn(HttpStatus.TOO_MANY_REQUESTS);
    when(response.getHeaders()).thenReturn(new HttpHeaders());

    MugloarRateLimitException ex =
        assertThrows(
            MugloarRateLimitException.class,
            () -> handler.handleError(URL, HttpMethod.GET, response));

    assertEquals(Duration.ZERO, ex.getRetryAfter());
  }

  @Test
  void translates429WithUnparseableRetryAfterToGenericApiException() throws IOException {
    ClientHttpResponse response = mock(ClientHttpResponse.class);
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.RETRY_AFTER, "Wed, 21 Oct 2025 07:28:00 GMT");
    when(response.getStatusCode()).thenReturn(HttpStatus.TOO_MANY_REQUESTS);
    when(response.getHeaders()).thenReturn(headers);

    MugloarApiException ex =
        assertThrows(
            MugloarApiException.class, () -> handler.handleError(URL, HttpMethod.GET, response));

    assertEquals(MugloarApiException.class, ex.getClass());
    assertEquals("No upstream error response that is resolvable", ex.getMessage());
  }

  @Test
  void translates503ToUnavailable() throws IOException {
    ClientHttpResponse response = mock(ClientHttpResponse.class);
    when(response.getStatusCode()).thenReturn(HttpStatus.SERVICE_UNAVAILABLE);

    assertThrows(
        MugloarUnavailableException.class,
        () -> handler.handleError(URL, HttpMethod.GET, response));
  }

  @Test
  void translatesOtherErrorsToGenericApiException() throws IOException {
    ClientHttpResponse response = mock(ClientHttpResponse.class);
    when(response.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

    MugloarApiException ex =
        assertThrows(
            MugloarApiException.class, () -> handler.handleError(URL, HttpMethod.GET, response));

    assertEquals(MugloarApiException.class, ex.getClass());
  }
}
