package com.sshakusora.shadowsandpetals.client.animation;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BlockAnimationDefinitionTest {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("test", "wind_chime");
    private static final AnimationResourceRef.Rig RIG = new AnimationResourceRef.Rig(
            ResourceLocation.fromNamespaceAndPath("test", "animation/wind_chime"));
    private static final AnimationResourceRef.Controller CONTROLLER = new AnimationResourceRef.Controller(
            ResourceLocation.fromNamespaceAndPath("test", "animation/wind_chime"));

    @Test
    void defaultStateMustBelongToController() {
        AnimationResourceRef.Controller other = new AnimationResourceRef.Controller(
                ResourceLocation.fromNamespaceAndPath("test", "other"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new BlockAnimationDefinition(
                        ID,
                        RIG,
                        CONTROLLER,
                        Set.of(),
                        new AnimationResourceRef.State(other, "idle")));

        assertEquals(
                "Block animation default state belongs to controller test:other, expected test:animation/wind_chime",
                exception.getMessage());
    }

    @Test
    void clipRegistrationIsImmutable() {
        AnimationResourceRef.Clip clip = new AnimationResourceRef.Clip(
                ResourceLocation.fromNamespaceAndPath("test", "animation/wind_chime"));
        BlockAnimationDefinition definition = new BlockAnimationDefinition(
                ID,
                RIG,
                CONTROLLER,
                Set.of(clip),
                new AnimationResourceRef.State(CONTROLLER, "idle"));

        assertEquals(Set.of(clip), definition.clips());
        definition.requireClip(clip);
    }

    @Test
    void reportsAnUnregisteredClip() {
        AnimationResourceRef.Clip missing = new AnimationResourceRef.Clip(
                ResourceLocation.fromNamespaceAndPath("test", "animation/missing"));
        BlockAnimationDefinition definition = new BlockAnimationDefinition(
                ID,
                RIG,
                CONTROLLER,
                Set.of(),
                new AnimationResourceRef.State(CONTROLLER, "idle"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> definition.requireClip(missing));

        assertEquals(
                "Block animation test:wind_chime does not register clip test:animation/missing",
                exception.getMessage());
    }

    @Test
    void registrationPublishesBlockTargetAndResources() {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                "shadowsandpetals_test", "block_animation_registration");
        AnimationResourceRef.Rig rig = new AnimationResourceRef.Rig(
                ResourceLocation.fromNamespaceAndPath("shadowsandpetals_test", "animation/block"));
        AnimationResourceRef.Controller controller = new AnimationResourceRef.Controller(
                ResourceLocation.fromNamespaceAndPath("shadowsandpetals_test", "animation/block"));
        AnimationResourceRef.Clip clip = new AnimationResourceRef.Clip(
                ResourceLocation.fromNamespaceAndPath("shadowsandpetals_test", "animation/block"));
        BlockAnimationDefinition definition = new BlockAnimationDefinition(
                id, rig, controller, Set.of(clip),
                new AnimationResourceRef.State(controller, "idle"));

        assertSame(definition, SAPAnimationRegistry.register(definition));
        assertSame(definition, SAPAnimationRegistry.findBlockAnimation(id));
        assertTrue(SAPAnimationRegistry.snapshot().blockAnimations().contains(definition));
        assertTrue(SAPAnimationRegistry.snapshot().registrations().stream()
                .anyMatch(registration -> registration.controller().equals(controller)
                        && registration.targets().equals(Set.of(SAPAnimationRegistry.Target.BLOCK_ENTITY))));
    }
}

