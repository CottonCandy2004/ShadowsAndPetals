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
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
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
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) {
        ChunkRenderTypeSet result = super.getRenderTypes(state, random, data);
        WoodPostBlock.Connections connections = data.get(CONNECTIONS);
        if (connections == null) {
            return result;
        }

        List<ChunkRenderTypeSet> renderTypes = new ArrayList<>();
        renderTypes.add(result);
        for (Direction direction : Direction.values()) {
            BakedModel connection = connectionModel(state, connections, direction);
            if (connection != null) {
                renderTypes.add(connection.getRenderTypes(state, random, data));
            }
        }
        return ChunkRenderTypeSet.union(renderTypes);
    }

    @Override
    public List<BakedQuad> getQuads(
            BlockState state,
            @Nullable Direction face,
            RandomSource random,
            ModelData data,
            @Nullable RenderType renderType
    ) {
        List<BakedQuad> result = new ArrayList<>(getQuadsForRenderType(
                originalModel, state, face, random, data, renderType));
        WoodPostBlock.Connections connections = data.get(CONNECTIONS);
        if (connections == null) {
            return result;
        }
        for (Direction direction : Direction.values()) {
            BakedModel connection = connectionModel(state, connections, direction);
            if (connection != null) {
                result.addAll(getQuadsForRenderType(
                        connection, state, face, random, data, renderType));
            }
        }
        return result;
    }

    private @Nullable BakedModel connectionModel(
            BlockState state,
            WoodPostBlock.Connections connections,
            Direction direction
    ) {
        WoodPostBlock.ConnectionType type = connections.get(direction);
        if (type == WoodPostBlock.ConnectionType.NONE || isRedundantPostConnection(state, type, direction)) {
            return null;
        }
        return BlockModelRegistry.getWoodPostConnectionModel(expectedBlock, type, direction);
    }

    private static boolean isRedundantPostConnection(
            BlockState state,
            WoodPostBlock.ConnectionType type,
            Direction direction
    ) {
        return type == WoodPostBlock.ConnectionType.OTHER_POST
                && state.hasProperty(WoodPostBlock.AXIS)
                && state.getValue(WoodPostBlock.AXIS) == direction.getAxis();
    }

    private static List<BakedQuad> getQuadsForRenderType(
            BakedModel model,
            BlockState state,
            @Nullable Direction face,
            RandomSource random,
            ModelData data,
            @Nullable RenderType renderType
    ) {
        if (renderType != null && !model.getRenderTypes(state, random, data).contains(renderType)) {
            return List.of();
        }
        return BakedModelSupport.getQuads(model, state, face, random, data, renderType);
    }
}
