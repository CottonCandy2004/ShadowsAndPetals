package com.sshakusora.shadowsandpetals.data.model.generator;

import com.sshakusora.shadowsandpetals.data.model.BlockModelContext;
import com.sshakusora.shadowsandpetals.data.model.ItemModelContext;
import com.sshakusora.shadowsandpetals.data.model.SAPBlockModelGenerator;
import com.sshakusora.shadowsandpetals.data.model.SAPItemModelGenerator;
import com.sshakusora.shadowsandpetals.block.decoration.WindowPaneBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

/** 1.21.1 compatibility callbacks for the 26.x model generator. */
public final class WindowPaneModels {
    private WindowPaneModels() {}
    public static void block(BlockModelContext<? extends WindowPaneBlock> context, SAPBlockModelGenerator generator,
                              String modelName, Block planks) {
        ResourceLocation model = generator.modLoc("block/window_pane/" + modelName);
        ResourceLocation texture = BuiltInRegistries.BLOCK.getKey(planks);
        texture = ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), "block/" + texture.getPath());
        generator.createModel(model.getPath(), generator.modLoc("block/window_pane/window_pane"),
                java.util.Map.of("windowframes", texture, "particle", texture), null);
        registerStates(context.get(), generator, model);
        StandardBlockModels.parentBlockItem(context.get(), generator, model);
    }
    public static void block(Object... ignored) {}
    public static void redLacquered(BlockModelContext<? extends WindowPaneBlock> context,
                                    SAPBlockModelGenerator generator) {
        ResourceLocation model = generator.modLoc("block/window_pane/red");
        ResourceLocation texture = generator.modLoc("block/window_pane/red");
        generator.createModel(model.getPath(), generator.modLoc("block/window_pane/window_pane"),
                java.util.Map.of("windowframes", texture, "particle", texture), null);
        registerStates(context.get(), generator, model);
        StandardBlockModels.parentBlockItem(context.get(), generator, model);
    }

    private static void registerStates(WindowPaneBlock block, SAPBlockModelGenerator generator,
                                       ResourceLocation model) {
        generator.provider().getVariantBuilder(block).forAllStates(state -> {
            int x = state.getValue(WindowPaneBlock.AXIS) == Direction.Axis.Y ? 90 : 0;
            int y = state.getValue(WindowPaneBlock.AXIS) == Direction.Axis.X ? 90 : 0;
            return ConfiguredModel.builder().modelFile(generator.uncheckedModel(model))
                    .rotationX(x).rotationY(y).build();
        });
    }
    public static void redLacquered(Object... ignored) {}
}
