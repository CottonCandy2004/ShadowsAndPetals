package com.sshakusora.shadowsandpetals.client.model;

import com.sshakusora.shadowsandpetals.block.decoration.CopperTeapotBlock;
import com.sshakusora.shadowsandpetals.block.decoration.irori.IroriGrillBlock;
import com.sshakusora.shadowsandpetals.registries.BlockRegistry;
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

/** Adds the upper grill mesh to the special copper-teapot block model. */
public final class IroriGrillCopperTeapotBlockStateModel extends BakedModelWrapper<BakedModel>
        implements IDynamicBakedModel {
    private final Block expectedBlock;

    public IroriGrillCopperTeapotBlockStateModel(Block expectedBlock, BakedModel delegate) {
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
        if (state.getBlock() != expectedBlock || !state.hasProperty(IroriGrillBlock.GRILL_PART)) {
            return result;
        }
        BlockState grillState = grillState(state);
        BakedModel grillModel = BakedModelSupport.blockModel(grillState);
        result.addAll(BakedModelSupport.getQuads(
                grillModel, grillState, face, random, ModelData.EMPTY, renderType));
        return result;
    }

    private static BlockState grillState(BlockState teapotState) {
        BlockState grillState = BlockRegistry.IRORI_GRILL.get().defaultBlockState()
                .setValue(IroriGrillBlock.GRILL_PART,
                        teapotState.getValue(IroriGrillBlock.GRILL_PART));
        if (teapotState.hasProperty(CopperTeapotBlock.WATERLOGGED)) {
            grillState = grillState.setValue(
                    IroriGrillBlock.WATERLOGGED,
                    teapotState.getValue(CopperTeapotBlock.WATERLOGGED));
        }
        return grillState;
    }
}
