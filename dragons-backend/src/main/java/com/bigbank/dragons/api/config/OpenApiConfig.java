package com.bigbank.dragons.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  OpenAPI dragonsOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Dragons of Mugloar API")
                .version("1.0.0")
                .description(
                    "Backend for the Dragons of Mugloar game. Wraps the upstream Mugloar API and "
                        + "exposes turn-by-turn interactive play plus automatic single/batch/streamed runs."));
  }
}
