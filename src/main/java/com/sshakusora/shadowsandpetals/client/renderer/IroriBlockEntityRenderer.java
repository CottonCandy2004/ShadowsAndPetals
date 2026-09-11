package com.sshakusora.shadowsandpetals.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.sshakusora.shadowsandpetals.blockentity.irori.IroriBlockEntity;

public class IroriBlockEntityRenderer implements BlockEntityRenderer<IroriBlockEntity> {
    public IroriBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(IroriBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
    }
}
