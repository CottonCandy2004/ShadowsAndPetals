package com.sshakusora.shadowsandpetals.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sshakusora.shadowsandpetals.block.decoration.VanityBlock;
import com.sshakusora.shadowsandpetals.blockentity.VanityBlockEntity;
import com.sshakusora.shadowsandpetals.client.model.BlockModelRegistry;
import com.sshakusora.shadowsandpetals.util.MathUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/** 1.21.1 implementation of the animated vanity drawer renderer. */
public class VanityBlockEntityRenderer implements BlockEntityRenderer<VanityBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public VanityBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(VanityBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        BakedModel drawerModel = BlockModelRegistry.getVanityDrawerModel(state.getBlock());
        if (drawerModel == null) {
            return;
        }

        float fullTravelDistance = VanityBlock.BASE_DRAWER_TRAVEL_DISTANCE
                * blockEntity.getDrawerTravelScale();
        float targetTranslation = MathUtils.easeOutCubic(
                blockEntity.getDrawerProgress(partialTick)
        ) * fullTravelDistance;
        float actualTranslation = Math.min(
                targetTranslation,
                Mth.clamp(blockEntity.getDrawerTravelLimit(), 0.0F, fullTravelDistance)
        );

        Direction facing = state.getValue(VanityBlock.FACING);
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot() - 180.0F));
        poseStack.translate(-0.5D, 0.0D, -0.5D);
        poseStack.translate(0.0D, 0.0D, -actualTranslation);
        LegacyBlockEntityRenderSupport.renderModel(
                blockRenderer,
                drawerModel,
                state,
                poseStack,
                buffer,
                packedLight,
                packedOverlay
        );
        poseStack.popPose();
    }
}
