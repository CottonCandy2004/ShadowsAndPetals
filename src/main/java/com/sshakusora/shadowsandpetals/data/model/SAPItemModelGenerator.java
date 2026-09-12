package com.sshakusora.shadowsandpetals.data.model;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * 1.21.1 compatibility facade for item model callbacks.
 */
public class SAPItemModelGenerator {
    private final @Nullable ItemModelProvider provider;

    public SAPItemModelGenerator() {
        this(null);
    }

    public SAPItemModelGenerator(@Nullable ItemModelProvider provider) {
        this.provider = provider;
    }

    public void generatedItem(Item item) {
        generatedItem(item, ShadowsAndPetals.asResource("item/" + item.builtInRegistryHolder().key().location().getPath()));
    }

    public void generatedItem(Item item, ResourceLocation texture) {
        if (provider == null) {
            return;
        }
        String name = item.builtInRegistryHolder().key().location().getPath();
        provider.withExistingParent(name, "item/generated")
                .texture("layer0", texture);
    }

    public void model(ResourceLocation id, Object model) {
    }

    public void parentModel(ResourceLocation id, ResourceLocation parent) {
        if (provider == null) {
            return;
        }
        provider.getBuilder(id.getPath()).parent(new ModelFile.UncheckedModelFile(parent));
    }

    public ModelFile createModel(String path, ResourceLocation parent, Map<String, ResourceLocation> textures) {
        if (provider == null) {
            throw new IllegalStateException("This generator is not attached to an ItemModelProvider");
        }
        var builder = provider.getBuilder(path).parent(new ModelFile.UncheckedModelFile(parent));
        textures.forEach(builder::texture);
        return builder;
    }

    public void finalizeClientItem(Item item, ResourceLocation clientModel, ResourceLocation customClientType) {
        if (provider == null) {
            return;
        }
        String name = item.builtInRegistryHolder().key().location().getPath();
        if (clientModel != null) {
            // Some client models are emitted by a later provider (curtain,
            // grill, and rockery asset providers).  The shared
            // ExistingFileHelper cannot see those outputs yet, so retain the
            // reference without asserting its existence at this stage.
            provider.getBuilder(name).parent(new ModelFile.UncheckedModelFile(clientModel));
            return;
        }
        if (customClientType == null) {
            return;
        }
        ResourceLocation fallback = switch (name) {
            case "tea_bucket" -> ResourceLocation.parse("minecraft:item/bucket");
            case "wind_chime" -> ShadowsAndPetals.asResource("item/wind_chime_body");
            case "wooden_barrel" -> ShadowsAndPetals.asResource("block/wooden_barrel/wooden_barrel");
            default -> ResourceLocation.parse("minecraft:item/generated");
        };
        provider.withExistingParent(name, fallback);
    }
}
