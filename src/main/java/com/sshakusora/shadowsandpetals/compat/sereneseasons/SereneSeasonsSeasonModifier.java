package com.sshakusora.shadowsandpetals.compat.sereneseasons;

import com.sshakusora.shadowsandpetals.world.excavation.SandExcavationChanceRules;
import com.sshakusora.shadowsandpetals.world.excavation.SandExcavationSeasonModifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

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
            this.getSeasonState = helper.getMethod("getSeasonState", Level.class);
            Class<?> stateType = Class.forName("sereneseasons.api.season.ISeasonState");
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
            return modifySeafoodChance(currentChance, String.valueOf(season));
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException("Unable to read Serene Seasons season", exception);
        }
    }

    static float getMultiplier(String seasonName) {
        return switch (seasonName.toUpperCase(Locale.ROOT)) {
            case "SPRING" -> SPRING_MULTIPLIER;
            case "SUMMER" -> SUMMER_MULTIPLIER;
            case "AUTUMN", "FALL" -> AUTUMN_MULTIPLIER;
            case "WINTER" -> WINTER_MULTIPLIER;
            default -> 1.0F;
        };
    }

    static float modifySeafoodChance(float currentChance, String seasonName) {
        return Math.clamp(
                currentChance * getMultiplier(seasonName),
                SandExcavationChanceRules.MINIMUM_SEAFOOD_CHANCE,
                1.0F - SandExcavationChanceRules.TRASH_CHANCE
        );
    }
}
