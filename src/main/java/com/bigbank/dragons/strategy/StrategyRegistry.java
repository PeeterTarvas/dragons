package com.bigbank.dragons.strategy;

import com.bigbank.dragons.api.exception.InvalidStrategyException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class StrategyRegistry {

  private final Map<StrategyType, GameStrategy> byType = new EnumMap<>(StrategyType.class);

  public StrategyRegistry(List<GameStrategy> strategies) {
    for (GameStrategy s : strategies) {
      byType.put(s.type(), s);
    }
  }

  public GameStrategy resolve(StrategyType type) {
    return Optional.ofNullable(byType.get(type))
        .orElseThrow(
            () -> new InvalidStrategyException("Unknown or unsupported strategy: " + type));
  }

  public List<StrategyType> available() {
    return List.copyOf(byType.keySet());
  }
}
