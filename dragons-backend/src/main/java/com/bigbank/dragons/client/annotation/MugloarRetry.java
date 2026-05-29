package com.bigbank.dragons.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Retryable(
    includes = {
      HttpClientErrorException.TooManyRequests.class,
      HttpServerErrorException.ServiceUnavailable.class
    },
    maxRetriesString = "${mugloar.max-attempts}",
    delayString = "${mugloar.initial-backoff-ms}",
    multiplier = 2)
public @interface MugloarRetry {}
