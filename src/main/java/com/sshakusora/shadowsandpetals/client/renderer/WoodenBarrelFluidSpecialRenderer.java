package com.sshakusora.shadowsandpetals.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

/** Shared 1.21.1 implementation for the barrel's item fluid layer. */
public final class WoodenBarrelFluidSpecialRenderer {
    private WoodenBarrelFluidSpecialRenderer() {
    }

    public static void renderModel(
            ItemRenderer itemRenderer,
            BakedModel model,
            ItemStack stack,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            int packedOverlay
    ) {
        for (RenderType renderType : model.getRenderTypes(stack, stack.hasFoil())) {
            VertexConsumer consumer = stack.hasFoil()
                    ? ItemRenderer.getFoilBuffer(buffers, renderType, true, true)
                    : buffers.getBuffer(renderType);
            itemRenderer.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, consumer);
        }
    }

    public static void render(
            RenderData data,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight
    ) {
        if (data == null || data.sprite() == null || data.amount() <= 0 || data.capacity() <= 0) {
            return;
        }
        float fillRatio = Mth.clamp(data.amount() / (float) data.capacity(), 0.0F, 1.0F);
        float surfaceY = Mth.lerp(fillRatio,
                WoodenBarrelFluidGeometry.MIN_SURFACE_Y,
                WoodenBarrelFluidGeometry.MAX_SURFACE_Y)
                + WoodenBarrelFluidGeometry.ITEM_SURFACE_EPSILON;
        int light = ClientFluidRenderInfo.applyLightEmission(packedLight, data.lightEmission());
        poseStack.pushPose();
        WoodenBarrelFluidGeometry.renderSurface(
                buffers.getBuffer(RenderType.translucent()), poseStack.last(), data.sprite(),
                surfaceY, data.color(), light, Direction.Axis.Z);
        poseStack.popPose();
    }

    public record RenderData(
            TextureAtlasSprite sprite,
            int color,
            int amount,
            int capacity,
            int lightEmission
    ) {
    }
}
