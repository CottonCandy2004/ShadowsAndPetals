package com.sshakusora.shadowsandpetals.data.model.generator;

import com.sshakusora.shadowsandpetals.data.model.BlockModelContext;
import com.sshakusora.shadowsandpetals.data.model.ItemModelContext;
import com.sshakusora.shadowsandpetals.data.model.SAPBlockModelGenerator;
import com.sshakusora.shadowsandpetals.data.model.SAPItemModelGenerator;
import com.sshakusora.shadowsandpetals.block.WoodBlockList;
import com.sshakusora.shadowsandpetals.block.decoration.WoodPillarBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

/** 1.21.1 compatibility callbacks for the 26.x model generator. */
public final class WoodPillarBlockModels {
    private WoodPillarBlockModels() {}
    public static void strippedWoodPillar(BlockModelContext<? extends WoodPillarBlock> context,
                                          SAPBlockModelGenerator generator,
                                          WoodBlockList.WoodType woodType) {
        ResourceLocation blockModel = generator.blockModelId(context.get());
        ResourceLocation strippedLog = BuiltInRegistries.BLOCK.getKey(woodType.getStrippedLog());
        ResourceLocation side = ResourceLocation.fromNamespaceAndPath(strippedLog.getNamespace(),
                "block/" + strippedLog.getPath());
        ResourceLocation top = side.withSuffix("_top");
        generator.createObjModel(blockModel.getPath(), generator.modLoc("block/template/wood_pillar"),
                generator.modLoc("models/block/wood_pillar/stripped_wood_pillar.obj"),
                java.util.Map.of("top", top, "side", side, "particle", side), true);
        generator.provider().getVariantBuilder(context.get()).forAllStates(state -> {
            var axis = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS);
            int x = axis == net.minecraft.core.Direction.Axis.Y ? 0 : 90;
            int y = axis == net.minecraft.core.Direction.Axis.X ? 90 : 0;
            return ConfiguredModel.builder().modelFile(generator.uncheckedModel(blockModel))
                    .rotationX(x).rotationY(y).uvLock(true).build();
        });
        StandardBlockModels.parentBlockItem(context.get(), generator, blockModel);
    }
}
