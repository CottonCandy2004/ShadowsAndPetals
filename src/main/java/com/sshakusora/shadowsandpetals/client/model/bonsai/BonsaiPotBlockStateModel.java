package com.sshakusora.shadowsandpetals.client.model.bonsai;

import com.sshakusora.shadowsandpetals.block.decoration.bonsai.BonsaiBlock;
import com.sshakusora.shadowsandpetals.blockentity.BonsaiBlockEntity;
import com.sshakusora.shadowsandpetals.client.model.BakedModelSupport;
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

/** Baked 1.21.1 wrapper for the rotated pot and its block-entity tree mesh. */
public final class BonsaiPotBlockStateModel extends BakedModelWrapper<BakedModel>
        implements IDynamicBakedModel {
    private final Block expectedBlock;
    private final int rotation;

    public BonsaiPotBlockStateModel(Block expectedBlock, BlockState bakedState, BakedModel delegate) {
        super(delegate);
        this.expectedBlock = expectedBlock;
        this.rotation = bakedState.getBlock() == expectedBlock
                && bakedState.hasProperty(BonsaiBlock.ROTATION)
                ? bakedState.getValue(BonsaiBlock.ROTATION) : 0;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, @Nullable Direction face, RandomSource random) {
        return getQuadsInternal(state, face, random, ModelData.EMPTY, null);
    }

    @Override
    public List<BakedQuad> getQuads(
            BlockState state,
            @Nullable Direction face,
            RandomSource random,
            ModelData data,
            @Nullable RenderType renderType
    ) {
        return getQuadsInternal(state, face, random, data, renderType);
    }

    private List<BakedQuad> getQuadsInternal(
            BlockState state,
            @Nullable Direction face,
            RandomSource random,
            ModelData data,
            @Nullable RenderType renderType
    ) {
        List<BakedQuad> result = new ArrayList<>();
        if (face == null) {
            result.addAll(BonsaiTreeGeometryCache.rotateQuads(
                    BakedModelSupport.getQuads(originalModel, state, null, random, data, renderType), rotation));
        } else {
            for (Direction sourceFace : Direction.values()) {
                List<BakedQuad> transformed = BonsaiTreeGeometryCache.rotateQuads(
                        BakedModelSupport.getQuads(originalModel, state, sourceFace, random, data, renderType),
                        rotation);
                for (BakedQuad quad : transformed) {
                    if (quad.getDirection() == face) {
                        result.add(quad);
                    }
                }
            }
        }

        // The vanilla block-breaking pass calls the model with a null render
        // type.  The tree is submitted by BonsaiBreakingOverlay in that pass
        // so the crack texture can cover the dynamic mesh without duplicating
        // it here.
        if (face != null || state.getBlock() != expectedBlock || renderType == null) {
            return result;
        }
        BonsaiBlockEntity.RenderData renderData = data.get(BonsaiBlockEntity.RENDER_DATA);
        if (renderData == null || !renderData.planted()) {
            return result;
        }
        BakedModel treeModel = renderData.dead()
                ? com.sshakusora.shadowsandpetals.client.model.BlockModelRegistry.BONSAI_DEAD_SHAPES
                .get(renderData.shape())
                : com.sshakusora.shadowsandpetals.client.model.BlockModelRegistry.BONSAI_SHAPES
                .get(renderData.shape());
        if (treeModel == null) {
            return result;
        }
        result.addAll(BonsaiTreeGeometryCache.rotateQuads(
                BonsaiTreeGeometryCache.getTreeQuads(
                        treeModel, state, data, renderData, renderType), rotation));
        return result;
    }
}
