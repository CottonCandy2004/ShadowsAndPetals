package com.sshakusora.shadowsandpetals.compat.sereneseasons;

import com.sshakusora.shadowsandpetals.world.excavation.SandExcavationChanceRules;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SereneSeasonsSeasonModifierTest {
    private static final float EPSILON = 0.0001F;

    @Test
    void mapsEachSeasonToItsConfiguredMultiplier() {
        assertEquals(1.00F, SereneSeasonsSeasonModifier.getMultiplier("SPRING"), EPSILON);
        assertEquals(1.15F, SereneSeasonsSeasonModifier.getMultiplier("SUMMER"), EPSILON);
        assertEquals(0.90F, SereneSeasonsSeasonModifier.getMultiplier("AUTUMN"), EPSILON);
        assertEquals(0.60F, SereneSeasonsSeasonModifier.getMultiplier("WINTER"), EPSILON);
    }

    @Test
    void keepsSeafoodChanceWithinTheExistingCategoryBounds() {
        assertEquals(
                SandExcavationChanceRules.MINIMUM_SEAFOOD_CHANCE,
                SereneSeasonsSeasonModifier.modifySeafoodChance(
                        SandExcavationChanceRules.NEAP_TIDE_SEAFOOD_CHANCE, "WINTER"),
                EPSILON
        );
        assertEquals(
                1.0F - SandExcavationChanceRules.TRASH_CHANCE,
                SereneSeasonsSeasonModifier.modifySeafoodChance(
                        SandExcavationChanceRules.SPRING_TIDE_SEAFOOD_CHANCE, "SUMMER"),
                EPSILON
        );
    }
}
