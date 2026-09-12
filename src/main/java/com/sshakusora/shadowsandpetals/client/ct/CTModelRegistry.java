package com.sshakusora.shadowsandpetals.client.ct;

import com.sshakusora.shadowsandpetals.client.ct.CTRegistry.CTEntry;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.client.event.ModelEvent;

/** Installs the connected-texture model wrapper during the 1.21.1 bake pass. */
public final class CTModelRegistry {
    private CTModelRegistry() {
    }

    public static void wrapModels(ModelEvent.ModifyBakingResult event) {
        event.getModels().replaceAll((modelLocation, model) -> {
            CTEntry entry = CTRegistry.entries().get(blockId(modelLocation));
            return entry == null ? model : new CTBlockStateModel(
                    BuiltInRegistries.BLOCK.get(blockId(modelLocation)), model, entry);
        });
    }

    private static net.minecraft.resources.ResourceLocation blockId(ModelResourceLocation modelLocation) {
        return modelLocation.id();
    }
}