package com.bigbank.dragons.client.config;

import com.bigbank.dragons.client.properties.MugloarProperties;
import java.time.Duration;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class MugloarClientConfig {

  @Bean
  RestClient mugloarRestClient(MugloarProperties properties) {
    var settings =
        HttpClientSettings.defaults()
            .withConnectTimeout(Duration.ofSeconds(properties.connectionTimeout()))
            .withReadTimeout(Duration.ofSeconds(properties.readTimeout()));

    var requestFactory = ClientHttpRequestFactoryBuilder.detect().build(settings);

    return RestClient.builder()
        .baseUrl(properties.baseUrl())
        .requestFactory(requestFactory)
        .defaultStatusHandler(new MugloarResponseErrorHandler())
        .build();
  }
}
