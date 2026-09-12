package com.sshakusora.shadowsandpetals.data.model.generator;

import com.sshakusora.shadowsandpetals.data.model.BlockModelContext;
import com.sshakusora.shadowsandpetals.data.model.ItemModelContext;
import com.sshakusora.shadowsandpetals.data.model.SAPBlockModelGenerator;
import com.sshakusora.shadowsandpetals.data.model.SAPItemModelGenerator;
import com.sshakusora.shadowsandpetals.block.WoodSetList;
import com.sshakusora.shadowsandpetals.block.decoration.WoodPostBlock;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/** 1.21.1 compatibility callbacks for the 26.x model generator. */
public final class WoodBlockModels {
    private WoodBlockModels() {}
    public static void woodSet(BlockModelContext<? extends Block> context, SAPBlockModelGenerator generator,
                               WoodSetList.WoodSet set) {
        var provider = generator.provider();
        RotatedPillarBlock log = (RotatedPillarBlock) set.log().get();
        RotatedPillarBlock strippedLog = (RotatedPillarBlock) set.strippedLog().get();
        RotatedPillarBlock wood = (RotatedPillarBlock) set.wood().get();
        RotatedPillarBlock strippedWood = (RotatedPillarBlock) set.strippedWood().get();
        ResourceLocation logSide = provider.blockTexture(log);
        ResourceLocation logTop = logSide.withSuffix("_top");
        ResourceLocation strippedSide = provider.blockTexture(strippedLog);
        ResourceLocation strippedTop = strippedSide.withSuffix("_top");
        provider.logBlock(log);
        provider.logBlock(strippedLog);
        provider.axisBlock(wood, logSide, logTop);
        provider.axisBlock(strippedWood, strippedSide, strippedTop);

        Block planks = set.planks().get();
        ResourceLocation plankTexture = provider.blockTexture(planks);
        ModelFile plankModel = provider.models().cubeAll(context.name(), plankTexture);
        provider.simpleBlockWithItem(planks, plankModel);
        provider.slabBlock(set.slab().get(), generator.blockModelId(planks), plankTexture);
        provider.stairsBlock(set.stairs().get(), plankTexture);
        provider.fenceBlock(set.fence().get(), plankTexture);
        provider.fenceGateBlock(set.fenceGate().get(), plankTexture);
        provider.pressurePlateBlock(set.pressurePlate().get(), plankTexture);
        provider.buttonBlock(set.button().get(), plankTexture);
        StandardBlockModels.parentBlockItem(log, generator, generator.blockModelId(log));
        StandardBlockModels.parentBlockItem(strippedLog, generator, generator.blockModelId(strippedLog));
        StandardBlockModels.parentBlockItem(wood, generator, generator.blockModelId(wood));
        StandardBlockModels.parentBlockItem(strippedWood, generator, generator.blockModelId(strippedWood));
        StandardBlockModels.parentBlockItem(planks, generator, generator.blockModelId(planks));
        StandardBlockModels.parentBlockItem(set.slab().get(), generator, generator.blockModelId(set.slab().get()));
        StandardBlockModels.parentBlockItem(set.stairs().get(), generator, generator.blockModelId(set.stairs().get()));
        StandardBlockModels.parentBlockItem(set.fence().get(), generator,
                ResourceLocation.withDefaultNamespace("block/fence_inventory"));
        StandardBlockModels.parentBlockItem(set.fenceGate().get(), generator,
                generator.blockModelId(set.fenceGate().get()));
        StandardBlockModels.parentBlockItem(set.pressurePlate().get(), generator,
                generator.blockModelId(set.pressurePlate().get()));
        StandardBlockModels.parentBlockItem(set.button().get(), generator,
                generator.blockModelId(set.button().get()));
    }
    public static void post(BlockModelContext<? extends WoodPostBlock> context, SAPBlockModelGenerator generator,
                            ResourceLocation sideTexture, ResourceLocation endTexture) {
        WoodPostBlock block = context.get();
        ResourceLocation core = generator.blockModelId(block);
        createPostModel(generator, core.getPath(), sideTexture, endTexture, 0, 16);
        createPostModel(generator, core.getPath() + "_link", sideTexture, endTexture, 0, 6);
        createPostModel(generator, core.getPath() + "_link_top", sideTexture, endTexture, 10, 16);
        for (WoodPostBlock.ConnectionType type : WoodPostBlock.ConnectionType.values()) {
            if (!type.isChain()) continue;
            ResourceLocation texture = type.texture();
            createChainModel(generator, "block/wood_post_" + type.getSerializedName() + "_link", texture, false);
            createChainModel(generator, "block/wood_post_" + type.getSerializedName() + "_link_top", texture, true);
        }
        generator.provider().getVariantBuilder(block).forAllStates(state -> {
            Direction.Axis axis = state.getValue(WoodPostBlock.AXIS);
            int x = axis == Direction.Axis.Y ? 0 : 90;
            int y = axis == Direction.Axis.X ? 90 : 0;
            return ConfiguredModel.builder().modelFile(generator.uncheckedModel(core))
                    .rotationX(x).rotationY(y).uvLock(true).build();
        });
        StandardBlockModels.parentBlockItem(block, generator, core);
    }

    private static void createPostModel(SAPBlockModelGenerator generator, String path,
                                        ResourceLocation side, ResourceLocation end,
                                        float fromY, float toY) {
        BlockModelBuilder builder = (BlockModelBuilder) generator.createModel(path, null,
                java.util.Map.of("side", side, "end", end, "particle", side), null);
        var element = builder.element().from(6, fromY, 6).to(10, toY, 10);
        element.face(Direction.DOWN).texture("#end");
        element.face(Direction.UP).texture("#end");
        element.face(Direction.NORTH).texture("#side");
        element.face(Direction.SOUTH).texture("#side");
        element.face(Direction.WEST).texture("#side");
        element.face(Direction.EAST).texture("#side");
        element.end();
    }

    private static void createChainModel(SAPBlockModelGenerator generator, String path,
                                         ResourceLocation texture, boolean upper) {
        BlockModelBuilder builder = (BlockModelBuilder) generator.createModel(path, null,
                java.util.Map.of("all", texture, "particle", texture), "cutout");
        float fromY = upper ? 10 : 0;
        float toY = upper ? 16 : 6;
        builder.element().from(6.5F, fromY, 7).to(9.5F, toY, 9).cube("#all").end();
    }
}
