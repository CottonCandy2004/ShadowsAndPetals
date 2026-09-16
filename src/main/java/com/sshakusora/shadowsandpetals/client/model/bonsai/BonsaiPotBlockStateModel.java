package com.sshakusora.shadowsandpetals.client.model.bonsai;

import com.sshakusora.shadowsandpetals.block.decoration.bonsai.BonsaiBlock;
import com.sshakusora.shadowsandpetals.blockentity.BonsaiBlockEntity;
import com.sshakusora.shadowsandpetals.client.model.BakedModelSupport;
import com.sshakusora.shadowsandpetals.client.model.BlockModelRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) {
        List<ChunkRenderTypeSet> renderTypes = new ArrayList<>();
        renderTypes.add(super.getRenderTypes(state, random, data));
        if (state.getBlock() != expectedBlock) {
            return ChunkRenderTypeSet.union(renderTypes);
        }

        BonsaiBlockEntity.RenderData renderData = data.get(BonsaiBlockEntity.RENDER_DATA);
        if (renderData == null || !renderData.planted()) {
            return ChunkRenderTypeSet.union(renderTypes);
        }

        addTargetRenderTypes(renderTypes, renderData.trunkBlockId(), random);
        if (!renderData.dead()) {
            addTargetRenderTypes(renderTypes, renderData.leavesBlockId(), random);
        }
        return ChunkRenderTypeSet.union(renderTypes);
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
        // The 16-step rotation is not limited to quadrant rotations.  Keep all
        // rotated pot faces in the general bucket so ModelBlockRenderer does
        // not apply an incorrect cardinal-face neighbor cull.
        if (face != null) {
            return result;
        }

        if (renderType == null || originalModel.getRenderTypes(state, random, data).contains(renderType)) {
            result.addAll(BonsaiTreeGeometryCache.rotateQuads(
                    collectAllQuads(originalModel, state, random, data, renderType), rotation));
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
                ? BlockModelRegistry.BONSAI_DEAD_SHAPES
                .get(renderData.shape())
                : BlockModelRegistry.BONSAI_SHAPES
                .get(renderData.shape());
        if (treeModel == null) {
            return result;
        }

        BonsaiTreeGeometryCache.TreeQuads treeParts = BonsaiTreeGeometryCache.getTreeParts(
                treeModel, state, data, renderData);
        if (renderType == null || targetRenderTypes(renderData.trunkBlockId(), random).contains(renderType)) {
            result.addAll(BonsaiTreeGeometryCache.rotateQuads(treeParts.trunk(), rotation));
        }
        if (!renderData.dead()
                && (renderType == null || targetRenderTypes(renderData.leavesBlockId(), random).contains(renderType))) {
            result.addAll(BonsaiTreeGeometryCache.rotateQuads(treeParts.leaves(), rotation));
        }
        return result;
    }

    private static List<BakedQuad> collectAllQuads(
            BakedModel model,
            BlockState state,
            RandomSource random,
            ModelData data,
            @Nullable RenderType renderType
    ) {
        List<BakedQuad> result = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            result.addAll(BakedModelSupport.getQuads(
                    model, state, direction, random, data, renderType));
        }
        result.addAll(BakedModelSupport.getQuads(model, state, null, random, data, renderType));
        return result;
    }

    private static void addTargetRenderTypes(
            List<ChunkRenderTypeSet> renderTypes,
            @Nullable ResourceLocation blockId,
            RandomSource random
    ) {
        if (blockId != null) {
            renderTypes.add(targetRenderTypes(blockId, random));
        }
    }

    private static ChunkRenderTypeSet targetRenderTypes(
            @Nullable ResourceLocation blockId,
            RandomSource random
    ) {
        if (blockId == null) {
            return ChunkRenderTypeSet.none();
        }
        Block block = BuiltInRegistries.BLOCK.get(blockId);
        if (block == Blocks.AIR) {
            return ChunkRenderTypeSet.none();
        }
        BlockState state = block.defaultBlockState();
        return BakedModelSupport.blockModel(state).getRenderTypes(state, random, ModelData.EMPTY);
    }
}
