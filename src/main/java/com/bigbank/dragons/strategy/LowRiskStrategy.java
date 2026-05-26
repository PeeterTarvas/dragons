package com.bigbank.dragons.strategy;

import com.bigbank.dragons.client.dto.MessageDto;
import com.bigbank.dragons.client.dto.ShopItemDto;
import com.bigbank.dragons.game.ProbabilityEstimator;
import com.bigbank.dragons.game.config.GameProperties;
import com.bigbank.dragons.game.state.GameState;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "game.strategy", havingValue = "low-risk")
public class LowRiskStrategy implements GameStrategy {

    private final GameProperties properties;

    /**
     * Sort first by highest success probability (lowest risk), then by highest reward
     */
    @Override
    public Optional<MessageDto> chooseAd(
            List<MessageDto> ads, GameState state, ProbabilityEstimator estimator) {
        return ads.stream()
                .max(Comparator.comparingDouble((MessageDto ad) -> estimator.estimate(ad.probability()))
                        .thenComparingDouble(MessageDto::reward));
    }

    /**
     * Risk-averse: heal if missing ANY lives or if below the standard threshold.
     * Only invest gold in upgrades if we have a significant financial cushion <= 300
     */
    @Override
    public Optional<ShopItemDto> choosePurchase(List<ShopItemDto> shopItemDtos, GameState state) {
        if (state.getLives() <= properties.lowLivesThreshold() + 1) {
            Optional<ShopItemDto> potion =
                    shopItemDtos.stream()
                            .filter(i -> isHealingPotion(i.name()))
                            .filter(i -> i.cost() <= state.getGold())
                            .filter(i -> i.cost() <= properties.healingPotionMaxCost())
                            .min(Comparator.comparingInt(ShopItemDto::cost));
            if (potion.isPresent()) return potion;
        }

        if (state.getGold() >= 300) {
            return shopItemDtos.stream()
                    .filter(i -> !isHealingPotion(i.name()))
                    .filter(i -> i.cost() <= state.getGold())
                    .min(Comparator.comparingInt(ShopItemDto::cost));
        }

        return Optional.empty();
    }

    private static boolean isHealingPotion(String name) {
        return name != null && name.toLowerCase(Locale.ROOT).contains("healing");
    }
}
