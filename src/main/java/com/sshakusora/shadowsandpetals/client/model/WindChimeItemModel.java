package com.sshakusora.shadowsandpetals.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sshakusora.shadowsandpetals.item.chime.WindChimeColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

public final class WindChimeItemModel implements IClientItemExtensions {
    private final BlockEntityWithoutLevelRenderer renderer;

    public WindChimeItemModel() {
        Minecraft minecraft = Minecraft.getInstance();
        this.renderer = new Renderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
    }

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return renderer;
    }

    private static final class Renderer extends BlockEntityWithoutLevelRenderer {
        private Renderer(
                BlockEntityRenderDispatcher dispatcher,
                EntityModelSet entityModels
        ) {
            super(dispatcher, entityModels);
        }

        @Override
        public void renderByItem(
                ItemStack stack,
                ItemDisplayContext displayContext,
                PoseStack poseStack,
                MultiBufferSource buffers,
                int packedLight,
                int packedOverlay
        ) {
            WindChimeColors colors = WindChimeColors.fromStack(stack);
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            renderModel(itemRenderer, stack, poseStack, buffers, packedLight, packedOverlay,
                    BlockModelRegistry.WIND_CHIME_ITEM_BODY.get());
            renderModel(itemRenderer, stack, poseStack, buffers, packedLight, packedOverlay,
                    BlockModelRegistry.WIND_CHIME_ITEM_RIBBON.get(colors.ribbon()));
            renderModel(itemRenderer, stack, poseStack, buffers, packedLight, packedOverlay,
                    BlockModelRegistry.WIND_CHIME_ITEM_VANE.get(colors.vane()));
        }

        private static void renderModel(
                ItemRenderer itemRenderer,
                ItemStack stack,
                PoseStack poseStack,
                MultiBufferSource buffers,
                int packedLight,
                int packedOverlay,
                @Nullable BakedModel model
        ) {
            if (model == null) {
                return;
            }
            for (RenderType renderType : model.getRenderTypes(stack, stack.hasFoil())) {
                var consumer = stack.hasFoil()
                        ? ItemRenderer.getFoilBuffer(buffers, renderType, true, true)
                        : buffers.getBuffer(renderType);
                itemRenderer.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, consumer);
            }
        }
    }
}
