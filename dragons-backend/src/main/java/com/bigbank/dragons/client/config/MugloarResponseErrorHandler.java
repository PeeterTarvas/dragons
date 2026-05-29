package com.bigbank.dragons.client.config;

import com.bigbank.dragons.client.exception.MugloarApiException;
import com.bigbank.dragons.client.exception.MugloarRateLimitException;
import com.bigbank.dragons.client.exception.MugloarUnavailableException;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class MugloarResponseErrorHandler implements ResponseErrorHandler {

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return response.getStatusCode().isError();
  }

  @Override
  public void handleError(@NonNull URI url, @NonNull HttpMethod method, ClientHttpResponse response)
      throws IOException {
    int status = response.getStatusCode().value();
    if (status == HttpStatus.TOO_MANY_REQUESTS.value()) {
      throw new MugloarRateLimitException(
          "Upstream rate limit (HTTP " + status + ") on " + method + " " + url,
          parseRetryAfter(response)
              .orElseThrow(
                  () -> new MugloarApiException("No upstream error response that is resolvable")));
    }
    if (status == HttpStatus.SERVICE_UNAVAILABLE.value()) {
      throw new MugloarUnavailableException(
          "Upstream unavailable (HTTP " + status + ") on " + method + " " + url);
    }
    throw new MugloarApiException("Upstream returned HTTP " + status);
  }

  private static Optional<Duration> parseRetryAfter(ClientHttpResponse response) {
    Optional<String> value =
        Optional.ofNullable(response.getHeaders().getFirst(HttpHeaders.RETRY_AFTER));
    try {
      return Optional.ofNullable(Duration.ofSeconds(Long.parseLong(value.orElse("0").trim())));
    } catch (NumberFormatException ex) {
      return Optional.empty();
    }
  }
}
