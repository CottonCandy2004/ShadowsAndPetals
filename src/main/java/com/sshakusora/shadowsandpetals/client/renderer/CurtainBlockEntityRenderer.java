package com.sshakusora.shadowsandpetals.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.sshakusora.shadowsandpetals.blockentity.CurtainBlockEntity;

public class CurtainBlockEntityRenderer implements BlockEntityRenderer<CurtainBlockEntity> {
    public CurtainBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CurtainBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
    }
}
