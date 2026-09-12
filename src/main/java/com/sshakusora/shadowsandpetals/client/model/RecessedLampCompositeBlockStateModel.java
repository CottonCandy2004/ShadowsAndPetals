package com.sshakusora.shadowsandpetals.client.model;

import com.sshakusora.shadowsandpetals.blockentity.RecessedLampBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/** Composes the lamp model with the slab stored in its block entity. */
public final class RecessedLampCompositeBlockStateModel extends BakedModelWrapper<BakedModel>
        implements IDynamicBakedModel {
    private final Block expectedBlock;

    public RecessedLampCompositeBlockStateModel(Block expectedBlock, BakedModel delegate) {
        super(delegate);
        this.expectedBlock = expectedBlock;
    }

    @Override
    public List<BakedQuad> getQuads(
            BlockState state,
            @Nullable Direction face,
            RandomSource random,
            ModelData data,
            @Nullable RenderType renderType
    ) {
        List<BakedQuad> result = new ArrayList<>(
                BakedModelSupport.getQuads(originalModel, state, face, random, data, renderType));
        if (state.getBlock() != expectedBlock) {
            return result;
        }
        BlockState storedSlab = data.get(RecessedLampBlockEntity.STORED_SLAB_MODEL_PROPERTY);
        if (!RecessedLampBlockEntity.isValidStoredSlab(storedSlab)) {
            return result;
        }
        BakedModel slabModel = BakedModelSupport.blockModel(storedSlab);
        result.addAll(BakedModelSupport.getQuads(
                slabModel, storedSlab, face, random, ModelData.EMPTY, renderType));
        return result;
    }
}
