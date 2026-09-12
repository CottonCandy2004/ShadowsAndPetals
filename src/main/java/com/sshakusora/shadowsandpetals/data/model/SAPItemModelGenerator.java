package com.sshakusora.shadowsandpetals.data.model;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
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
        if (name.equals("wind_chime") || name.equals("wooden_barrel")) {
            // 1.21.1 still dispatches custom item rendering through the
            // builtin/entity baked model.  The 26.x item-model type field does
            // not exist in this renderer, so retain the display transforms on
            // the compatibility model and expose the renderer through
            // IClientItemExtensions.
            ItemModelBuilder builder = provider.getBuilder(name).parent(new ModelFile.UncheckedModelFile(
                    ResourceLocation.withDefaultNamespace("builtin/entity")));
            addDisplayTransforms(builder, name);
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

    private static void addDisplayTransforms(ItemModelBuilder builder, String name) {
        var transforms = builder.transforms();
        if (name.equals("wind_chime")) {
            transforms.transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                    .translation(0.0F, -1.75F, -0.75F).scale(0.55F).end();
            transforms.transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                    .translation(0.0F, -3.75F, -0.75F).scale(0.55F).end();
            transforms.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                    .translation(0.25F, 0.0F, 0.0F).scale(0.68F).end();
            transforms.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                    .translation(-0.25F, 0.0F, 0.0F).scale(0.68F).end();
            transforms.transform(ItemDisplayContext.GROUND)
                    .translation(0.0F, 3.0F, 0.0F).scale(0.5F).end();
            transforms.transform(ItemDisplayContext.GUI)
                    .rotation(10.0F, -20.0F, 0.0F).translation(0.0F, 1.5F, 0.0F)
                    .scale(0.75F).end();
            transforms.transform(ItemDisplayContext.HEAD)
                    .rotation(0.0F, 90.0F, 0.0F).translation(-8.0F, -0.5F, -5.5F).end();
            transforms.transform(ItemDisplayContext.FIXED)
                    .translation(0.0F, -2.0F, 0.0F).end();
        } else {
            transforms.transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                    .rotation(90.0F, 90.0F, 0.0F).translation(0.0F, -2.0F, -2.25F).scale(0.5F).end();
            transforms.transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                    .rotation(90.0F, 90.0F, 0.0F).translation(0.0F, -2.0F, -2.25F).scale(0.5F).end();
            transforms.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                    .translation(0.0F, 4.0F, 0.0F).scale(0.4F).end();
            transforms.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                    .translation(0.0F, 4.0F, 0.0F).scale(0.4F).end();
            transforms.transform(ItemDisplayContext.GROUND)
                    .translation(0.0F, 2.5F, 0.0F).scale(0.5F).end();
            transforms.transform(ItemDisplayContext.GUI)
                    .rotation(30.0F, -135.0F, 0.0F).scale(0.7F).end();
            transforms.transform(ItemDisplayContext.HEAD)
                    .translation(0.0F, 14.5F, 0.0F).end();
            transforms.transform(ItemDisplayContext.FIXED)
                    .translation(0.0F, 0.0F, 0.5F).end();
        }
        transforms.end();
    }
}
