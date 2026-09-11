package com.sshakusora.shadowsandpetals.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.sshakusora.shadowsandpetals.blockentity.WindChimeBlockEntity;

public class WindChimeBlockEntityRenderer implements BlockEntityRenderer<WindChimeBlockEntity> {
    public WindChimeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(WindChimeBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
    }
}
