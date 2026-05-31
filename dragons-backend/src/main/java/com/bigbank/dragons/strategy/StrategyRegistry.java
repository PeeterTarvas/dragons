package com.bigbank.dragons.strategy;

import com.bigbank.dragons.game.config.GameProperties;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class StrategyRegistry {

  private final Map<StrategyType, GameStrategy> byType = new EnumMap<>(StrategyType.class);
  private final StrategyType defaultStrategy;
  private final GameProperties gameProperties;

  public StrategyRegistry(List<GameStrategy> strategies, GameProperties properties) {
    for (GameStrategy s : strategies) {
      byType.put(s.type(), s);
    }
    this.defaultStrategy =
        StrategyType.fromKey(properties.defaultStrategy()).orElse(StrategyType.EXPECTED_VALUE);
    this.gameProperties = properties;
  }

  public GameStrategy resolve(StrategyType type) {
    return Optional.ofNullable(byType.get(type)).orElseGet(() -> byType.get(defaultStrategy));
  }

  public List<StrategyType> available() {
    return List.copyOf(byType.keySet());
  }

  public StrategyType resolve(String strategy) {
    return StrategyType.fromKey(strategy)
        .orElseGet(
            () ->
                StrategyType.fromKey(gameProperties.defaultStrategy())
                    .orElse(StrategyType.EXPECTED_VALUE));
  }
}
