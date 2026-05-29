package com.bigbank.dragons.game.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchExecutorConfig {

  /**
   * Shared thread pool for concurrent batch game runs. Sized by {@code game.thread-pool-size}.
   * {@code destroyMethod = "shutdown"} tells Spring to drain the pool gracefully on context close.
   */
  @Bean(destroyMethod = "shutdown")
  public ExecutorService batchExecutorService(GameProperties props) {
    return Executors.newFixedThreadPool(props.threadPoolSize());
  }
}
