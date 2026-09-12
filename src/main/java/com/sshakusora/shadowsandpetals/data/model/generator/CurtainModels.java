package com.sshakusora.shadowsandpetals.data.model.generator;

import com.sshakusora.shadowsandpetals.block.decoration.curtain.CurtainBlock;
import com.sshakusora.shadowsandpetals.data.model.BlockModelContext;
import com.sshakusora.shadowsandpetals.data.model.SAPBlockModelGenerator;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/** 1.21.1 compatibility callbacks for the 26.x model generator. */
public final class CurtainModels {
    private CurtainModels() {}
    public static void block(BlockModelContext<? extends CurtainBlock> context, SAPBlockModelGenerator generator) {
        CurtainBlock block = context.get();
        String path = context.id().getPath();
        String color = path.endsWith("_curtain") ? path.substring(0, path.length() - 8) : path;
        generator.provider().getVariantBuilder(block).forAllStatesExcept(state -> {
            String half = state.getValue(CurtainBlock.HALF) == DoubleBlockHalf.UPPER ? "upper" : "lower";
            String side = state.getValue(CurtainBlock.SIDE) == CurtainBlock.Side.RIGHT ? "right" : "left";
            String pose = state.getValue(CurtainBlock.OPEN) ? "open" : "closed";
            ResourceLocation id = generator.modLoc("block/curtain/static/" + side + "/" + pose + "/" + color + "/" + half);
            Direction facing = state.getValue(CurtainBlock.FACING);
            int y = switch (facing) { case EAST -> 90; case SOUTH -> 180; case WEST -> 270; default -> 0; };
            return ConfiguredModel.builder().modelFile(new ModelFile.UncheckedModelFile(id)).rotationY(y).build();
        }, CurtainBlock.POWERED);
        StandardBlockModels.parentBlockItem(block, generator, generator.modLoc("block/curtain/item/" + color));
    }
}
