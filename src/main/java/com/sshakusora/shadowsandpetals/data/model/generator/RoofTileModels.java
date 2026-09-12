package com.sshakusora.shadowsandpetals.data.model.generator;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.block.decoration.RoofTileSlabBlock;
import com.sshakusora.shadowsandpetals.block.decoration.RoofTileVerticalSlabBlock;
import com.sshakusora.shadowsandpetals.block.decoration.VerticalSlabBlock;
import com.sshakusora.shadowsandpetals.data.model.BlockModelContext;
import com.sshakusora.shadowsandpetals.data.model.SAPBlockModelGenerator;
import com.sshakusora.shadowsandpetals.registries.BlockRegistry;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import java.util.Map;

/** 1.21.1 implementation of the colored roof-tile model graph. */
public final class RoofTileModels {
    private RoofTileModels() {}

    public static void base(BlockModelContext context, SAPBlockModelGenerator generator, ResourceLocation texture) {
        Block block = context.get();
        ModelFile model = generator.createModel(
                generator.blockModelId(block).getPath(),
                generator.modLoc("block/template/roof_tile_block"),
                Map.of("all", texture));
        generator.provider().getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(model)
                .rotationY(horizontalRotation(state.getValue(BlockStateProperties.HORIZONTAL_FACING)))
                .build());
        StandardBlockModels.parentBlockItem(block, generator, generator.blockModelId(block));
    }

    public static void base(Object... ignored) {}

    public static void shapes(BlockModelContext context, SAPBlockModelGenerator generator,
                             net.minecraft.world.item.DyeColor color) {
        Block base = BlockRegistry.ROOF_TILES.get(color).get();
        RoofTileSlabBlock slab = (RoofTileSlabBlock) context.get();
        RoofTileVerticalSlabBlock vertical = BlockRegistry.ROOF_TILE_VERTICAL_SLABS.get(color).get();
        StairBlock stairs = BlockRegistry.ROOF_TILE_STAIRS.get(color).get();
        ResourceLocation texture = ShadowsAndPetals.asResource("block/roof_tile/" + color.getName());

        ModelFile slabBottom = generatedModel(generator, slab, "block/template/roof_tile_slab", "", texture);
        ModelFile slabTop = generatedModel(generator, slab, "block/template/roof_tile_slab_top", "_top", texture);
        ModelFile full = generator.provider().models().getExistingFile(generator.blockModelId(base));
        generator.provider().getVariantBuilder(slab).forAllStates(state -> {
            SlabType type = state.getValue(BlockStateProperties.SLAB_TYPE);
            ModelFile model = type == SlabType.BOTTOM ? slabBottom : type == SlabType.TOP ? slabTop : full;
            return ConfiguredModel.builder().modelFile(model)
                    .rotationY(horizontalRotation(state.getValue(RoofTileSlabBlock.FACING)))
                    .build();
        });
        generator.suggestItemModel(slab.asItem(), slabBottom.getLocation());

        ModelFile verticalNorth = generatedModel(generator, vertical, "block/template/roof_tile_vertical_slab", "", texture);
        ModelFile verticalSouth = generatedModel(generator, vertical, "block/template/roof_tile_vertical_slab_south", "_south", texture);
        ModelFile verticalWest = generatedModel(generator, vertical, "block/template/roof_tile_vertical_slab_west", "_west", texture);
        ModelFile verticalEast = generatedModel(generator, vertical, "block/template/roof_tile_vertical_slab_east", "_east", texture);
        generator.provider().getVariantBuilder(vertical).forAllStates(state -> {
            Direction facing = state.getValue(RoofTileVerticalSlabBlock.FACING);
            VerticalSlabBlock.VerticalSlabType type = state.getValue(VerticalSlabBlock.TYPE);
            ModelFile model;
            if (type == VerticalSlabBlock.VerticalSlabType.DOUBLE) {
                model = full;
            } else {
                model = switch (relativeType(type, facing)) {
                    case NORTH -> verticalNorth;
                    case SOUTH -> verticalSouth;
                    case WEST -> verticalWest;
                    case EAST -> verticalEast;
                    case DOUBLE -> throw new IllegalStateException("A half slab cannot use the double model");
                };
            }
            return ConfiguredModel.builder().modelFile(model)
                    .rotationY(horizontalRotation(facing))
                    .build();
        });
        generator.suggestItemModel(vertical.asItem(), verticalNorth.getLocation());

        ModelFile inner = generatedModel(generator, stairs, "block/template/roof_tile_inner_stairs", "_inner", texture);
        ModelFile straight = generatedModel(generator, stairs, "block/template/roof_tile_stairs", "", texture);
        ModelFile outer = generatedModel(generator, stairs, "block/template/roof_tile_outer_stairs", "_outer", texture);
        generator.provider().getVariantBuilder(stairs).forAllStatesExcept(state -> {
            StairsShape shape = state.getValue(StairBlock.SHAPE);
            ModelFile model = switch (shape) {
                case STRAIGHT -> straight;
                case INNER_LEFT, INNER_RIGHT -> inner;
                case OUTER_LEFT, OUTER_RIGHT -> outer;
            };
            int y = horizontalRotation(state.getValue(StairBlock.FACING));
            if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
                y = (y + 270) % 360;
            }
            return ConfiguredModel.builder().modelFile(model)
                    .rotationX(state.getValue(StairBlock.HALF) == Half.TOP ? 180 : 0)
                    .rotationY(y)
                    .build();
        }, StairBlock.WATERLOGGED);
        generator.suggestItemModel(stairs.asItem(), straight.getLocation());
    }

    public static void shapes(Object... ignored) {}

    private static ModelFile generatedModel(SAPBlockModelGenerator generator, Block block,
                                             String parentPath, String modelSuffix,
                                             ResourceLocation texture) {
        String path = generator.blockModelId(block).getPath() + modelSuffix;
        return generator.createModel(path, generator.modLoc(parentPath), Map.of(
                "bottom", texture,
                "top", texture,
                "side", texture));
    }

    private static int horizontalRotation(Direction facing) {
        return switch (facing) {
            case EAST -> 0;
            case SOUTH -> 90;
            case WEST -> 180;
            case NORTH -> 270;
            default -> throw new IllegalArgumentException("Roof tile facing must be horizontal: " + facing);
        };
    }

    private static VerticalSlabBlock.VerticalSlabType relativeType(
            VerticalSlabBlock.VerticalSlabType type, Direction facing) {
        Rotation inverse = switch (facing) {
            case NORTH -> Rotation.NONE;
            case EAST -> Rotation.COUNTERCLOCKWISE_90;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.CLOCKWISE_90;
            default -> throw new IllegalArgumentException("Roof tile facing must be horizontal: " + facing);
        };
        return VerticalSlabBlock.VerticalSlabType.fromDirection(inverse.rotate(type.direction()));
    }
}
