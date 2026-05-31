package com.bigbank.dragons.game.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchExecutorConfig {

  @Bean(destroyMethod = "shutdown")
  public ExecutorService batchExecutorService(GameProperties props) {
    return Executors.newFixedThreadPool(props.threadPoolSize());
  }
}
