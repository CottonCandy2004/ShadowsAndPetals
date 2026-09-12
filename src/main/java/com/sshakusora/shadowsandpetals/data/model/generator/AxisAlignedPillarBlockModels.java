package com.sshakusora.shadowsandpetals.data.model.generator;

import com.sshakusora.shadowsandpetals.data.model.BlockModelContext;
import com.sshakusora.shadowsandpetals.data.model.SAPBlockModelGenerator;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

/** 1.21.1 compatibility callbacks for the 26.x model generator. */
public final class AxisAlignedPillarBlockModels {
    private AxisAlignedPillarBlockModels() {}
    public static void withItem(BlockModelContext<? extends Block> context, SAPBlockModelGenerator generator) {
        withItem(context, generator, generator.blockModelId(context.get()));
    }

    public static void withItem(BlockModelContext<? extends Block> context,
                                SAPBlockModelGenerator generator,
                                net.minecraft.resources.ResourceLocation model) {
        Block block = context.get();
        generator.provider().getVariantBuilder(block).forAllStates(state -> {
            Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
            int x = axis == Direction.Axis.Y ? 0 : 90;
            int y = axis == Direction.Axis.X ? 90 : 0;
            return ConfiguredModel.builder()
                    .modelFile(generator.uncheckedModel(model))
                    .rotationX(x)
                    .rotationY(y)
                    .uvLock(true)
                    .build();
        });
        StandardBlockModels.parentBlockItem(block, generator, model);
    }
}
