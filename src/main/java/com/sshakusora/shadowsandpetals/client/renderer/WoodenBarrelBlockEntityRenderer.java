package com.sshakusora.shadowsandpetals.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sshakusora.shadowsandpetals.block.decoration.WoodenBarrelBlock;
import com.sshakusora.shadowsandpetals.blockentity.WoodenBarrelBlockEntity;
import com.sshakusora.shadowsandpetals.compat.transfer.fluid.FluidResource;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Map;
import java.util.WeakHashMap;

public class WoodenBarrelBlockEntityRenderer implements BlockEntityRenderer<WoodenBarrelBlockEntity> {
    private static final float FLUID_LEVEL_ANIMATION_RATE = 0.32F;
    private static final float FLUID_LEVEL_SNAP_EPSILON = 0.5F;
    private static final double MAX_ANIMATION_GAP_TICKS = 20.0D;
    private final ClientFluidRenderInfo.Cache<WoodenBarrelBlockEntity> fluidCache =
            new ClientFluidRenderInfo.Cache<>();
    private final Map<WoodenBarrelBlockEntity, FluidLevelAnimation> fluidLevelAnimations =
            new WeakHashMap<>();

    public WoodenBarrelBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(WoodenBarrelBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (blockEntity.getLevel() == null) {
            return;
        }
        FluidResource resource = blockEntity.getFluidTank().getResource(0);
        int amount = blockEntity.getFluidTank().getAmountAsInt(0);
        Fluid fluid = resource.isEmpty() || amount <= 0 ? Fluids.EMPTY : resource.getFluid();
        double currentTime = blockEntity.getLevel().getGameTime() + partialTick;
        FluidLevelAnimation animation = fluidLevelAnimations.computeIfAbsent(
                blockEntity,
                ignored -> new FluidLevelAnimation()
        );
        FluidLevelAnimation.Sample sample = animation.update(fluid, Math.max(0, amount), currentTime);
        if (sample.fluid() == Fluids.EMPTY || sample.displayedAmount() <= 0.0F) {
            return;
        }
        ClientFluidRenderInfo.Info info = fluidCache.getSurface(
                blockEntity,
                sample.fluid(),
                (BlockAndTintGetter) blockEntity.getLevel(),
                blockEntity.getBlockPos()
        );
        TextureAtlasSprite sprite = info.sprite();
        if (sprite == null) {
            return;
        }
        float fill = Mth.clamp(
                sample.displayedAmount() / WoodenBarrelBlockEntity.FLUID_CAPACITY,
                0.0F,
                1.0F
        );
        float surface = Mth.lerp(fill,
                WoodenBarrelFluidGeometry.MIN_SURFACE_Y,
                WoodenBarrelFluidGeometry.MAX_SURFACE_Y);
        int light = ClientFluidRenderInfo.applyLightEmission(packedLight, info.lightEmission());
        poseStack.pushPose();
        WoodenBarrelFluidGeometry.renderSurface(
                buffer.getBuffer(RenderType.translucent()),
                poseStack.last(),
                sprite,
                surface,
                info.color(),
                light,
                blockEntity.getBlockState().getValue(WoodenBarrelBlock.AXIS)
        );
        poseStack.popPose();
    }

    /** Client-only visual state; the server-side tank remains authoritative. */
    private static final class FluidLevelAnimation {
        private Fluid visualFluid = Fluids.EMPTY;
        private float displayedAmount;
        private double lastUpdateTime;
        private boolean initialized;

        private Sample update(Fluid targetFluid, int targetAmount, double currentTime) {
            if (!initialized) {
                initialized = true;
                lastUpdateTime = currentTime;
                visualFluid = targetAmount > 0 ? targetFluid : Fluids.EMPTY;
                displayedAmount = targetAmount;
                return new Sample(visualFluid, displayedAmount);
            }

            double deltaTicks = currentTime - lastUpdateTime;
            lastUpdateTime = currentTime;
            if (deltaTicks < 0.0D || deltaTicks > MAX_ANIMATION_GAP_TICKS) {
                visualFluid = targetAmount > 0 ? targetFluid : Fluids.EMPTY;
                displayedAmount = targetAmount;
                return new Sample(visualFluid, displayedAmount);
            }

            if (targetAmount > 0 && visualFluid != targetFluid) {
                visualFluid = targetFluid;
                displayedAmount = 0.0F;
            }

            float alpha = 1.0F - (float) Math.exp(-FLUID_LEVEL_ANIMATION_RATE * deltaTicks);
            displayedAmount += (targetAmount - displayedAmount) * alpha;
            if (Math.abs(displayedAmount - targetAmount) < FLUID_LEVEL_SNAP_EPSILON) {
                displayedAmount = targetAmount;
            }

            if (targetAmount == 0 && displayedAmount <= FLUID_LEVEL_SNAP_EPSILON) {
                displayedAmount = 0.0F;
                visualFluid = Fluids.EMPTY;
            }
            return new Sample(visualFluid, displayedAmount);
        }

        private record Sample(Fluid fluid, float displayedAmount) {
        }
    }
}
