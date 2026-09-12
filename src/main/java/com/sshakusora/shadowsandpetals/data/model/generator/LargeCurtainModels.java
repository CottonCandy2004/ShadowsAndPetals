package com.sshakusora.shadowsandpetals.data.model.generator;

import com.sshakusora.shadowsandpetals.data.model.BlockModelContext;
import com.sshakusora.shadowsandpetals.data.model.ItemModelContext;
import com.sshakusora.shadowsandpetals.data.model.SAPBlockModelGenerator;
import com.sshakusora.shadowsandpetals.data.model.SAPItemModelGenerator;
import com.sshakusora.shadowsandpetals.block.decoration.curtain.LargeCurtainBlock;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/** 1.21.1 compatibility callbacks for the 26.x model generator. */
public final class LargeCurtainModels {
    private LargeCurtainModels() {}
    public static void block(BlockModelContext<? extends LargeCurtainBlock> context, SAPBlockModelGenerator generator) {
        LargeCurtainBlock block = context.get();
        String path = context.id().getPath();
        String color = path.endsWith("_large_curtain") ? path.substring(0, path.length() - 14) : path;
        generator.provider().getVariantBuilder(block).forAllStatesExcept(state -> {
            String half = state.getValue(LargeCurtainBlock.HALF) == DoubleBlockHalf.UPPER ? "upper" : "lower";
            String column = state.getValue(LargeCurtainBlock.COLUMN) == LargeCurtainBlock.Column.OUTER ? "outer" : "inner";
            String side = state.getValue(LargeCurtainBlock.SIDE) == LargeCurtainBlock.Side.RIGHT ? "right" : "left";
            String pose = state.getValue(LargeCurtainBlock.OPEN) ? "open" : "closed";
            ResourceLocation id = generator.modLoc("block/large_curtain/static/" + side + "/" + pose + "/" + color + "/" + half + "_" + column);
            Direction facing = state.getValue(LargeCurtainBlock.FACING);
            int y = switch (facing) { case EAST -> 90; case SOUTH -> 180; case WEST -> 270; default -> 0; };
            return ConfiguredModel.builder().modelFile(new ModelFile.UncheckedModelFile(id)).rotationY(y).build();
        }, LargeCurtainBlock.POWERED);
        StandardBlockModels.parentBlockItem(block, generator, generator.modLoc("block/large_curtain/item/" + color));
    }
}
