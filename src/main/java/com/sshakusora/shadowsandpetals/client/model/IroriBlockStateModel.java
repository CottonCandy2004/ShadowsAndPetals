package com.sshakusora.shadowsandpetals.client.model;

import com.sshakusora.shadowsandpetals.block.decoration.irori.IroriBlock;
import com.sshakusora.shadowsandpetals.block.decoration.irori.IroriGrillBlock;
import com.sshakusora.shadowsandpetals.block.decoration.irori.IroriGrillPart;
import com.sshakusora.shadowsandpetals.block.decoration.irori.IroriGrillPartHolder;
import com.sshakusora.shadowsandpetals.registries.BlockRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/** Adds the baked lower grill geometry to an Irori base model. */
public final class IroriBlockStateModel extends BakedModelWrapper<BakedModel>
        implements IDynamicBakedModel {
    private static final ModelProperty<GrillData> GRILL_DATA = new ModelProperty<>();

    private final Block expectedBlock;

    public IroriBlockStateModel(Block expectedBlock, BakedModel delegate) {
        super(delegate);
        this.expectedBlock = expectedBlock;
    }

    @Override
    public ModelData getModelData(
            BlockAndTintGetter level,
            BlockPos pos,
            BlockState state,
            ModelData data
    ) {
        ModelData result = super.getModelData(level, pos, state, data);
        if (state.getBlock() != expectedBlock) {
            return result;
        }
        return result.derive().with(GRILL_DATA, new GrillData(grillPartAt(level, pos, state))).build();
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
        GrillData grillData = data.get(GRILL_DATA);
        if (grillData == null || grillData.part() == null) {
            return result;
        }

        BakedModel grillModel = BlockModelRegistry.IRORI_GRILL_LOWER.get(grillData.part());
        if (grillModel == null) {
            return result;
        }

        BlockState grillState = BlockRegistry.IRORI_GRILL.get().defaultBlockState()
                .setValue(IroriGrillBlock.GRILL_PART, grillData.part());
        result.addAll(BakedModelSupport.getQuads(
                grillModel, grillState, face, random, data, renderType));
        return result;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    private static @Nullable IroriGrillPart grillPartAt(
            BlockAndTintGetter level,
            BlockPos pos,
            BlockState state
    ) {
        if (!IroriBlock.hasGrill(state)) {
            return null;
        }
        BlockState upperState = level.getBlockState(pos.above());
        if (!IroriGrillPartHolder.isGrillPart(upperState)) {
            return null;
        }
        BlockPos masterPos = IroriGrillPartHolder.masterPosition(pos.above(), upperState);
        return IroriBlock.hasGrill(level.getBlockState(masterPos))
                ? IroriGrillPartHolder.getGrillPart(upperState) : null;
    }

    private record GrillData(@Nullable IroriGrillPart part) {
    }
}
