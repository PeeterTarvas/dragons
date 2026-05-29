package com.bigbank.dragons.game.config;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.Test;

public class BatchExecutorConfigTest {

  @Test
  void batchExecutorServiceCreatesFixedPoolOfCorrectSize() throws Exception {
    GameProperties props =
        new GameProperties(1000.0, 100, 6, 50, "expected-value", 0.2, 2, 50, 200);
    ExecutorService pool = new BatchExecutorConfig().batchExecutorService(props);

    assertFalse(pool.isShutdown());
    pool.shutdown();
  }
}
