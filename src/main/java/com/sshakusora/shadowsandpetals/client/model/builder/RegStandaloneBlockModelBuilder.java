package com.sshakusora.shadowsandpetals.client.model.builder;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.client.model.registry.ClientModelRegistry;
import com.sshakusora.shadowsandpetals.client.model.registry.StandaloneBlockModel;
import com.sshakusora.shadowsandpetals.client.model.registry.StandaloneModelRotation;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/** Fluent builder for one additional standalone model. */
public final class RegStandaloneBlockModelBuilder {
    private final String name;
    private ResourceLocation modelId;
    private StandaloneModelRotation rotation = StandaloneModelRotation.IDENTITY;

    public RegStandaloneBlockModelBuilder(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public RegStandaloneBlockModelBuilder model(ResourceLocation modelId) {
        this.modelId = Objects.requireNonNull(modelId, "modelId");
        return this;
    }

    public RegStandaloneBlockModelBuilder rotation(StandaloneModelRotation rotation) {
        this.rotation = Objects.requireNonNull(rotation, "rotation");
        return this;
    }

    public StandaloneBlockModel register() {
        if (modelId == null) {
            throw new IllegalStateException("Standalone model resource is required for '" + name + "'");
        }
        return ClientModelRegistry.register(new StandaloneBlockModel(
                ShadowsAndPetals.asResource(name), modelId, rotation));
    }
}
