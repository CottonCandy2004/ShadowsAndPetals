package com.sshakusora.shadowsandpetals.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.sshakusora.shadowsandpetals.blockentity.VanityBlockEntity;

/**
 * Compatibility entry point retained for callers using the historical
 * renderer package.  The live 1.21.1 implementation is shared with the
 * client-package registration.
 */
public final class VanityBlockEntityRenderer implements BlockEntityRenderer<VanityBlockEntity> {
    private final com.sshakusora.shadowsandpetals.client.VanityBlockEntityRenderer delegate;

    public VanityBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.delegate = new com.sshakusora.shadowsandpetals.client.VanityBlockEntityRenderer(context);
    }

    @Override
    public void render(VanityBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        delegate.render(blockEntity, partialTick, poseStack, buffer, packedLight, packedOverlay);
    }
}
