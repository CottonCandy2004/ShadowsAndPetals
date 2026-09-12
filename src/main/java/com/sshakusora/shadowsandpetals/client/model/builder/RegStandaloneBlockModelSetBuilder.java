package com.sshakusora.shadowsandpetals.client.model.builder;

import com.sshakusora.shadowsandpetals.client.model.registry.ClientModelRegistry;
import com.sshakusora.shadowsandpetals.client.model.registry.StandaloneBlockModelSet;
import com.sshakusora.shadowsandpetals.client.model.registry.StandaloneModelRotation;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/** Fluent builder for a keyed family of additional standalone models. */
public final class RegStandaloneBlockModelSetBuilder<K> {
    private final String name;
    private Supplier<? extends Iterable<K>> keys;
    private Function<? super K, String> keyPathFactory;
    private Function<? super K, ResourceLocation> modelFactory;
    private Function<? super K, StandaloneModelRotation> rotationFactory =
            key -> StandaloneModelRotation.IDENTITY;

    public RegStandaloneBlockModelSetBuilder(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public RegStandaloneBlockModelSetBuilder<K> keys(Supplier<? extends Iterable<K>> keys) {
        this.keys = Objects.requireNonNull(keys, "keys");
        return this;
    }

    public RegStandaloneBlockModelSetBuilder<K> keyPath(Function<? super K, String> factory) {
        this.keyPathFactory = Objects.requireNonNull(factory, "factory");
        return this;
    }

    public RegStandaloneBlockModelSetBuilder<K> model(Function<? super K, ResourceLocation> factory) {
        this.modelFactory = Objects.requireNonNull(factory, "factory");
        return this;
    }

    public RegStandaloneBlockModelSetBuilder<K> rotation(
            Function<? super K, StandaloneModelRotation> factory) {
        this.rotationFactory = Objects.requireNonNull(factory, "factory");
        return this;
    }

    public StandaloneBlockModelSet<K> register() {
        if (keys == null) {
            throw new IllegalStateException("Standalone model keys are required for '" + name + "'");
        }
        if (keyPathFactory == null) {
            throw new IllegalStateException("Standalone model key path is required for '" + name + "'");
        }
        if (modelFactory == null) {
            throw new IllegalStateException("Standalone model resource factory is required for '" + name + "'");
        }
        return ClientModelRegistry.register(new StandaloneBlockModelSet<>(
                name, keys, keyPathFactory, modelFactory, rotationFactory));
    }
}
