package com.sshakusora.shadowsandpetals.client.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Small compatibility helpers shared by the 1.21.1 baked-model wrappers. */
public final class BakedModelSupport {
    private BakedModelSupport() {
    }

    public static List<BakedQuad> getQuads(
            BakedModel model,
            BlockState state,
            @Nullable Direction face,
            RandomSource random,
            ModelData data,
            @Nullable RenderType renderType
    ) {
        return model instanceof IDynamicBakedModel dynamic
                ? dynamic.getQuads(state, face, random, data, renderType)
                : model.getQuads(state, face, random);
    }

    public static BakedModel blockModel(BlockState state) {
        ModelResourceLocation location = BlockModelShaper.stateToModelLocation(state);
        return net.minecraft.client.Minecraft.getInstance().getModelManager().getModel(location);
    }
}
