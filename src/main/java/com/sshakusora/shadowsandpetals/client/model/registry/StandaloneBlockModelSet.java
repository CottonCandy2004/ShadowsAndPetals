package com.sshakusora.shadowsandpetals.client.model.registry;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/** Lazily materialized family of additional models sharing one key prefix. */
public final class StandaloneBlockModelSet<K> implements ClientModelEntry {
    private final String idPrefix;
    private final Supplier<? extends Iterable<K>> keys;
    private final Function<? super K, String> keyPathFactory;
    private final Function<? super K, ResourceLocation> modelFactory;
    private final Function<? super K, StandaloneModelRotation> rotationFactory;
    private final Map<K, StandaloneBlockModel> models = new HashMap<>();

    public StandaloneBlockModelSet(String idPrefix, Supplier<? extends Iterable<K>> keys,
                                   Function<? super K, String> keyPathFactory,
                                   Function<? super K, ResourceLocation> modelFactory,
                                   Function<? super K, StandaloneModelRotation> rotationFactory) {
        this.idPrefix = idPrefix;
        this.keys = keys;
        this.keyPathFactory = keyPathFactory;
        this.modelFactory = modelFactory;
        this.rotationFactory = rotationFactory;
    }

    public @Nullable BakedModel get(K key) {
        StandaloneBlockModel model = models.get(key);
        return model == null ? null : model.get();
    }

    @Override
    public void registerModels(ModelEvent.RegisterAdditional event,
                               Set<ModelResourceLocation> registeredIds) {
        models.clear();
        for (K key : keys.get()) {
            String keyPath = keyPathFactory.apply(key);
            ResourceLocation id = ShadowsAndPetals.asResource(idPrefix + "/" + keyPath);
            StandaloneBlockModel model = new StandaloneBlockModel(
                    id, modelFactory.apply(key), rotationFactory.apply(key));
            if (models.putIfAbsent(key, model) != null) {
                throw new IllegalStateException("Duplicate standalone model-set key in '" + idPrefix + "': " + key);
            }
            model.registerModels(event, registeredIds);
        }
    }

    @Override
    public void cacheModels(ModelEvent.BakingCompleted event) {
        for (StandaloneBlockModel model : models.values()) {
            model.cacheModels(event);
        }
    }
}
