package com.sshakusora.shadowsandpetals.client.model;

import com.sshakusora.shadowsandpetals.block.decoration.WoodPostBlock;
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

/** Adds the connection arms that depend on neighboring blocks. */
public final class WoodPostBlockStateModel extends BakedModelWrapper<BakedModel>
        implements IDynamicBakedModel {
    private static final ModelProperty<WoodPostBlock.Connections> CONNECTIONS = new ModelProperty<>();
    private final Block expectedBlock;

    public WoodPostBlockStateModel(Block expectedBlock, BakedModel delegate) {
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
        return result.derive().with(CONNECTIONS, WoodPostBlock.connections(level, pos, state)).build();
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
        WoodPostBlock.Connections connections = data.get(CONNECTIONS);
        if (connections == null) {
            return result;
        }
        for (Direction direction : Direction.values()) {
            WoodPostBlock.ConnectionType type = connections.get(direction);
            if (type == WoodPostBlock.ConnectionType.NONE) {
                continue;
            }
            BakedModel connection = BlockModelRegistry.getWoodPostConnectionModel(
                    expectedBlock, type, direction);
            if (connection != null) {
                result.addAll(BakedModelSupport.getQuads(
                        connection, state, face, random, data, renderType));
            }
        }
        return result;
    }
}
