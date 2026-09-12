package com.sshakusora.shadowsandpetals.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sshakusora.shadowsandpetals.blockentity.WindChimeBlockEntity;
import com.sshakusora.shadowsandpetals.item.chime.WindChimeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

public class WindChimeBlockEntityRenderer implements BlockEntityRenderer<WindChimeBlockEntity> {
    private static final float FULL_CIRCLE = (float) (Math.PI * 2.0D);
    private static final long PROFILE_SALT = 0x9E3779B97F4A7C15L;
    private static final long BODY_Y_WANDER_SALT = 0xD1B54A32D192ED03L;
    private static final long MAIN_Y_WANDER_SALT = 0xABC98388FB8FAC03L;
    private static final long WANDER_STEP = 0x9E3779B97F4A7C15L;
    private static final int BODY_Y_WANDER_INTERVAL = 120;
    private static final int MAIN_Y_WANDER_INTERVAL = 75;
    private static final float BODY_Y_WANDER_AMPLITUDE = 10.0F;
    private static final float MAIN_Y_WANDER_AMPLITUDE = 18.0F;
    private final BlockRenderDispatcher blockRenderer;

    public WindChimeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public AABB getRenderBoundingBox(WindChimeBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).expandTowards(0.0D, -0.5D, 0.0D);
    }

    @Override
    public void render(WindChimeBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (blockEntity.getLevel() == null) {
            return;
        }
        WindChimeColors colors = blockEntity.getColors();
        long time = blockEntity.getLevel().getGameTime();
        MotionProfile profile = MotionProfile.create(blockEntity.getBlockPos().asLong());
        float natural = blockEntity.getNaturalMotionWeight(partialTick);
        float secondaryWave = sampleWave(time, partialTick, profile.timeScale, 0.031F, profile.secondaryPhase);
        float bodyYWander = sampleWander(
                time, partialTick, blockEntity.getBlockPos().asLong(),
                BODY_Y_WANDER_SALT, BODY_Y_WANDER_INTERVAL
        ) * BODY_Y_WANDER_AMPLITUDE;
        float mainYWander = sampleWander(
                time, partialTick, blockEntity.getBlockPos().asLong(),
                MAIN_Y_WANDER_SALT, MAIN_Y_WANDER_INTERVAL
        ) * MAIN_Y_WANDER_AMPLITUDE;
        float bodyX = blockEntity.getBodyX(partialTick)
                + natural * profile.bodyAmplitude * (
                sampleWave(time, partialTick, profile.timeScale, 0.075F, profile.phaseX) * 2.5F
                        + secondaryWave * 0.8F * profile.secondaryAmplitude);
        float bodyY = blockEntity.getBodyY(partialTick)
                + natural * (
                sampleWave(time, partialTick, profile.timeScale, 0.024F, profile.phaseY) * 3.0F * profile.bodyAmplitude
                        + bodyYWander);
        float bodyZ = blockEntity.getBodyZ(partialTick)
                + natural * sampleWave(time, partialTick, profile.timeScale, 0.061F, profile.phaseZ + 1.8F)
                * 2.0F * profile.bodyAmplitude;
        float mainX = blockEntity.getMainX(partialTick)
                + natural * sampleWave(time, partialTick, profile.timeScale, 0.075F, profile.phaseX - 0.65F)
                * 4.0F * profile.mainAmplitude;
        float mainY = blockEntity.getMainY(partialTick)
                + natural * (
                sampleWave(time, partialTick, profile.timeScale, 0.029F, profile.phaseY - 0.8F) * 7.0F * profile.mainAmplitude
                        + mainYWander);
        float mainZ = blockEntity.getMainZ(partialTick)
                + natural * sampleWave(time, partialTick, profile.timeScale, 0.061F, profile.phaseZ + 1.15F)
                * 3.0F * profile.mainAmplitude;

        float axisRotation = blockEntity.getBlockState().getValue(
                com.sshakusora.shadowsandpetals.block.decoration.WindChimeBlock.HORIZONTAL_AXIS
        ) == Direction.Axis.X ? 90.0F : 0.0F;

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(axisRotation));
        poseStack.translate(-0.5D, 0.0D, -0.5D);
        rotateAround(poseStack, 0.5D, 1.0D, 0.5D, bodyX, bodyY, bodyZ);
        LegacyBlockEntityRenderSupport.renderStandalone(
                blockRenderer, WindChimeColors.blockBodyModelId(colors.ribbon()),
                blockEntity.getBlockState(), poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(axisRotation));
        poseStack.translate(-0.5D, 0.0D, -0.5D);
        rotateAround(poseStack, 0.5D, 1.0D - 4.0D / 16.0D, 0.5D, mainX, mainY, mainZ);
        poseStack.translate(0.0D, -4.0D / 16.0D, 0.0D);
        LegacyBlockEntityRenderSupport.renderStandalone(
                blockRenderer, WindChimeColors.blockMainRibbonModelId(colors.ribbon()),
                blockEntity.getBlockState(), poseStack, buffer, packedLight, packedOverlay);
        LegacyBlockEntityRenderSupport.renderStandalone(
                blockRenderer, WindChimeColors.blockVaneModelId(colors.vane()),
                blockEntity.getBlockState(), poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    private static void rotateAround(
            PoseStack poseStack, double x, double y, double z,
            float xDegrees, float yDegrees, float zDegrees
    ) {
        poseStack.translate(x, y, z);
        poseStack.mulPose(Axis.XP.rotationDegrees(xDegrees));
        poseStack.mulPose(Axis.YP.rotationDegrees(yDegrees));
        poseStack.mulPose(Axis.ZP.rotationDegrees(zDegrees));
        poseStack.translate(-x, -y, -z);
    }

    private static float sampleWave(long gameTime, float partialTick, float timeScale, float frequency, float phase) {
        double angularSpeed = (double) timeScale * frequency;
        double angle = ((double) gameTime * angularSpeed + phase) % (Math.PI * 2.0D);
        angle += partialTick * angularSpeed;
        return (float) Math.sin(angle);
    }

    private static float sampleWander(long gameTime, float partialTick, long position, long salt, int intervalLength) {
        long interval = Math.floorDiv(gameTime, intervalLength);
        float progress = (Math.floorMod(gameTime, intervalLength) + partialTick) / intervalLength;
        float smoothProgress = progress * progress * (3.0F - 2.0F * progress);
        float from = sampleSigned(mix64(position ^ salt ^ interval * WANDER_STEP));
        float to = sampleSigned(mix64(position ^ salt ^ (interval + 1L) * WANDER_STEP));
        return Mth.lerp(smoothProgress, from, to);
    }

    private static long mix64(long value) {
        value ^= value >>> 33;
        value *= 0xFF51AFD7ED558CCDL;
        value ^= value >>> 33;
        value *= 0xC4CEB9FE1A85EC53L;
        return value ^ value >>> 33;
    }

    private static float sample01(long bits, int shift) {
        return ((bits >>> shift) & 0xFFFFL) / 65535.0F;
    }

    private static float sampleSigned(long bits) {
        return ((bits >>> 40) & 0xFFFFFFL) / 8388607.5F - 1.0F;
    }

    private record MotionProfile(
            float phaseX,
            float phaseY,
            float phaseZ,
            float secondaryPhase,
            float timeScale,
            float bodyAmplitude,
            float mainAmplitude,
            float secondaryAmplitude
    ) {
        private static MotionProfile create(long position) {
            long phaseBits = mix64(position);
            long profileBits = mix64(phaseBits + PROFILE_SALT);
            return new MotionProfile(
                    sample01(phaseBits, 0) * FULL_CIRCLE,
                    sample01(phaseBits, 16) * FULL_CIRCLE,
                    sample01(phaseBits, 32) * FULL_CIRCLE,
                    sample01(phaseBits, 48) * FULL_CIRCLE,
                    sampleRange(profileBits, 0, 0.94F, 1.06F),
                    sampleRange(profileBits, 16, 0.90F, 1.10F),
                    sampleRange(profileBits, 32, 0.85F, 1.15F),
                    sampleRange(profileBits, 48, 0.85F, 1.15F)
            );
        }

        private static float sampleRange(long bits, int shift, float min, float max) {
            return min + (max - min) * sample01(bits, shift);
        }
    }
}
