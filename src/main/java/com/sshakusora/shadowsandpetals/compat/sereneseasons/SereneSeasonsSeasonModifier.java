package com.sshakusora.shadowsandpetals.compat.sereneseasons;

import com.sshakusora.shadowsandpetals.world.excavation.SandExcavationChanceRules;
import com.sshakusora.shadowsandpetals.world.excavation.SandExcavationSeasonModifier;
import net.minecraft.server.level.ServerLevel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Reflective Serene Seasons bridge so the base 1.21.1 jar has no hard
 * dependency on the optional mod.
 */
public final class SereneSeasonsSeasonModifier implements SandExcavationSeasonModifier {
    private static final float SPRING_MULTIPLIER = 1.00F;
    private static final float SUMMER_MULTIPLIER = 1.15F;
    private static final float AUTUMN_MULTIPLIER = 0.90F;
    private static final float WINTER_MULTIPLIER = 0.60F;

    private final Method getSeasonState;
    private final Method getSeason;

    public SereneSeasonsSeasonModifier() {
        try {
            Class<?> helper = Class.forName("sereneseasons.api.season.SeasonHelper");
            this.getSeasonState = helper.getMethod("getSeasonState", net.minecraft.world.level.Level.class);
            Class<?> stateType = Class.forName("sereneseasons.api.season.SeasonState");
            this.getSeason = stateType.getMethod("getSeason");
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Serene Seasons season API is unavailable", exception);
        }
    }

    @Override
    public float modifySeafoodChance(ServerLevel level, float currentChance) {
        try {
            Object state = getSeasonState.invoke(null, level);
            Object season = getSeason.invoke(state);
            float adjusted = currentChance * multiplier(String.valueOf(season));
            return Math.clamp(
                    adjusted,
                    SandExcavationChanceRules.MINIMUM_SEAFOOD_CHANCE,
                    1.0F - SandExcavationChanceRules.TRASH_CHANCE
            );
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException("Unable to read Serene Seasons season", exception);
        }
    }

    private static float multiplier(String seasonName) {
        return switch (seasonName.toUpperCase(java.util.Locale.ROOT)) {
            case "SPRING" -> SPRING_MULTIPLIER;
            case "SUMMER" -> SUMMER_MULTIPLIER;
            case "AUTUMN", "FALL" -> AUTUMN_MULTIPLIER;
            case "WINTER" -> WINTER_MULTIPLIER;
            default -> 1.0F;
        };
    }
}
