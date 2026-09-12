package com.sshakusora.shadowsandpetals.client.animation;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AnimatedBlockModelTest {
    private static final AnimationResourceRef.Rig RIG = new AnimationResourceRef.Rig(
            ResourceLocation.fromNamespaceAndPath("test", "rig"));
    private static final AnimationResourceRef.Rig OTHER_RIG = new AnimationResourceRef.Rig(
            ResourceLocation.fromNamespaceAndPath("test", "other_rig"));

    @Test
    void bindingValidatesBoneBeforeResolvingClientRenderTypes() {
        assertThrows(IllegalArgumentException.class, () -> new AnimatedBlockModel.Binding(
                RIG, " ", null, null, false));
    }

    @Test
    void bindingRejectsNullBakedModelBeforeRender() {
        assertThrows(NullPointerException.class, () -> new AnimatedBlockModel.Binding(
                OTHER_RIG, "body", null, null, false));
    }

    @Test
    void modelRejectsNullRigWithoutLoadingClientRegistries() {
        assertThrows(NullPointerException.class, () -> new AnimatedBlockModel(null, List.of()));
    }
}
