package com.sshakusora.shadowsandpetals.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.block.decoration.ShishiOdoshiBlock;
import com.sshakusora.shadowsandpetals.blockentity.ShishiOdoshiBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class ShishiOdoshiBlockEntityRenderer implements BlockEntityRenderer<ShishiOdoshiBlockEntity> {
    private static final float MAIN_PIVOT_X = 8.0F / 16.0F;
    private static final float MAIN_PIVOT_Y = 9.0F / 16.0F;
    private static final float MAIN_PIVOT_Z = 9.0F / 16.0F;
    private static final float MAIN_OUTLET_X = 8.0F;
    private static final float MAIN_OUTLET_Y = 10.881578F;
    private static final float MAIN_OUTLET_Z = 3.259766F;
    private static final float MAIN_INSIDE_Y = 8.111086F;
    private static final float MAIN_INSIDE_Z = 8.581831F;
    private static final float POUR_BOTTOM_Y = 3.02F / 16.0F;
    private static final float POUR_HALF_WIDTH = 0.48F / 16.0F;
    private static final float POUR_STRIP_LENGTH = 3.5F / 16.0F;
    private static final float POUR_FACE_OFFSET = 0.01F / 16.0F;
    private static final float TUBE_EXIT_DURATION =
            ShishiOdoshiBlockEntity.TIPPING_DURATION - ShishiOdoshiBlockEntity.POUR_START_TICK;
    private static final float FLOW_UV_SCALE = 0.5F;
    private static final float FLOW_U_CENTER = 0.5F;
    private final ClientFluidRenderInfo.Cache<ShishiOdoshiBlockEntity> fluidRenderInfoCache =
            new ClientFluidRenderInfo.Cache<>();
    private final BlockRenderDispatcher blockRenderer;

    public ShishiOdoshiBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(ShishiOdoshiBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (blockEntity.getLevel() == null) {
            return;
        }

        BlockState state = blockEntity.getBlockState();
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.getValue(ShishiOdoshiBlock.FACING).toYRot() + 180.0F));
        poseStack.translate(-0.5D, 0.0D, -0.5D);

        poseStack.pushPose();
        poseStack.translate(8.0D / 16.0D, 9.0D / 16.0D, 9.0D / 16.0D);
        poseStack.mulPose(Axis.XP.rotationDegrees(blockEntity.getTipAngle(partialTick)));
        poseStack.translate(-8.0D / 16.0D, -9.0D / 16.0D, -9.0D / 16.0D);
        LegacyBlockEntityRenderSupport.renderStandalone(
                blockRenderer,
                ShadowsAndPetals.asResource("block/shishi_odoshi/main"),
                state,
                poseStack,
                buffer,
                packedLight,
                packedOverlay
        );
        poseStack.popPose();

        float pourProgress = blockEntity.getPourProgress(partialTick);
        Fluid fluid = blockEntity.getFluid();
        if (pourProgress >= 0.0F && pourProgress < 1.0F && fluid != Fluids.EMPTY) {
            ClientFluidRenderInfo.Info renderInfo = fluidRenderInfoCache.get(
                    blockEntity,
                    fluid,
                    (BlockAndTintGetter) blockEntity.getLevel(),
                    blockEntity.getBlockPos()
            );
            TextureAtlasSprite sprite = renderInfo.sprite();
            if (sprite != null) {
                int light = ClientFluidRenderInfo.applyLightEmission(
                        packedLight,
                        renderInfo.lightEmission()
                );
                renderPouringStream(
                        buffer.getBuffer(RenderType.translucent()),
                        poseStack.last(),
                        sprite,
                        blockEntity.getTipAngle(partialTick),
                        pourProgress,
                        renderInfo.color(),
                        light
                );
            }
        }
        poseStack.popPose();
    }

    private static void renderPouringStream(
            VertexConsumer buffer,
            PoseStack.Pose pose,
            TextureAtlasSprite sprite,
            float tipAngle,
            float pourProgress,
            int color,
            int lightCoords
    ) {
        float elapsedTicks = Math.max(0.0F, pourProgress) * ShishiOdoshiBlockEntity.POUR_DURATION;
        float pathAngle = elapsedTicks > TUBE_EXIT_DURATION
                ? ShishiOdoshiBlockEntity.MAX_TIP_ANGLE
                : tipAngle;
        float radians = pathAngle * Mth.DEG_TO_RAD;
        float cos = Mth.cos(radians);
        float sin = Mth.sin(radians);
        float insideY = rotateY(MAIN_INSIDE_Y / 16.0F, MAIN_INSIDE_Z / 16.0F, cos, sin);
        float insideZ = rotateZ(MAIN_INSIDE_Y / 16.0F, MAIN_INSIDE_Z / 16.0F, cos, sin);
        float outletY = rotateY(MAIN_OUTLET_Y / 16.0F, MAIN_OUTLET_Z / 16.0F, cos, sin);
        float outletZ = rotateZ(MAIN_OUTLET_Y / 16.0F, MAIN_OUTLET_Z / 16.0F, cos, sin);

        float minX = MAIN_OUTLET_X / 16.0F - POUR_HALF_WIDTH;
        float maxX = MAIN_OUTLET_X / 16.0F + POUR_HALF_WIDTH;
        float tubeLength = Mth.sqrt(
                Mth.square(outletY - insideY) + Mth.square(outletZ - insideZ)
        );
        float fallingLength = outletY - POUR_BOTTOM_Y;
        float totalLength = tubeLength + fallingLength;
        if (tubeLength <= 0.0F || fallingLength <= 0.0F || totalLength <= POUR_STRIP_LENGTH) {
            return;
        }

        float halfU = (maxX - minX) * FLOW_UV_SCALE * 0.5F;
        float minU = FLOW_U_CENTER - halfU;
        float maxU = FLOW_U_CENTER + halfU;
        float flowSpeed = tubeLength / TUBE_EXIT_DURATION;
        float headDistance = POUR_STRIP_LENGTH + elapsedTicks * flowSpeed;
        float tailDistance = headDistance - POUR_STRIP_LENGTH;

        float tubeStart = Math.max(0.0F, tailDistance);
        float tubeEnd = Math.min(tubeLength, headDistance);
        if (tubeEnd > tubeStart) {
            float startProgress = tubeStart / tubeLength;
            float endProgress = tubeEnd / tubeLength;
            renderWaterRibbonSegment(
                    buffer,
                    pose,
                    sprite,
                    minX,
                    maxX,
                    Mth.lerp(startProgress, insideY, outletY),
                    Mth.lerp(startProgress, insideZ, outletZ),
                    Mth.lerp(endProgress, insideY, outletY),
                    Mth.lerp(endProgress, insideZ, outletZ),
                    minU,
                    maxU,
                    (tubeStart - tailDistance) * FLOW_UV_SCALE,
                    (tubeEnd - tailDistance) * FLOW_UV_SCALE,
                    color,
                    lightCoords
            );
        }

        float fallingStart = Math.max(tubeLength, tailDistance);
        float fallingEnd = Math.min(totalLength, headDistance);
        if (fallingEnd > fallingStart) {
            renderWaterRibbonSegment(
                    buffer,
                    pose,
                    sprite,
                    minX,
                    maxX,
                    outletY - (fallingStart - tubeLength),
                    outletZ,
                    outletY - (fallingEnd - tubeLength),
                    outletZ,
                    minU,
                    maxU,
                    (fallingStart - tailDistance) * FLOW_UV_SCALE,
                    (fallingEnd - tailDistance) * FLOW_UV_SCALE,
                    color,
                    lightCoords
            );
        }
    }

    private static float rotateY(float y, float z, float cos, float sin) {
        return MAIN_PIVOT_Y + cos * (y - MAIN_PIVOT_Y) - sin * (z - MAIN_PIVOT_Z);
    }

    private static float rotateZ(float y, float z, float cos, float sin) {
        return MAIN_PIVOT_Z + sin * (y - MAIN_PIVOT_Y) + cos * (z - MAIN_PIVOT_Z);
    }

    private static void renderWaterRibbonSegment(
            VertexConsumer buffer,
            PoseStack.Pose pose,
            TextureAtlasSprite sprite,
            float minX,
            float maxX,
            float startY,
            float startZ,
            float endY,
            float endZ,
            float minU,
            float maxU,
            float startV,
            float endV,
            int color,
            int lightCoords
    ) {
        float deltaY = endY - startY;
        float deltaZ = endZ - startZ;
        float inverseLength = Mth.invSqrt(deltaY * deltaY + deltaZ * deltaZ);
        float normalY = -deltaZ * inverseLength;
        float normalZ = deltaY * inverseLength;
        float frontOffsetY = -normalY * POUR_FACE_OFFSET;
        float frontOffsetZ = -normalZ * POUR_FACE_OFFSET;

        addWaterVertex(buffer, pose, sprite, minX, startY + frontOffsetY, startZ + frontOffsetZ,
                color, minU, startV, lightCoords, -normalY, -normalZ);
        addWaterVertex(buffer, pose, sprite, minX, endY + frontOffsetY, endZ + frontOffsetZ,
                color, minU, endV, lightCoords, -normalY, -normalZ);
        addWaterVertex(buffer, pose, sprite, maxX, endY + frontOffsetY, endZ + frontOffsetZ,
                color, maxU, endV, lightCoords, -normalY, -normalZ);
        addWaterVertex(buffer, pose, sprite, maxX, startY + frontOffsetY, startZ + frontOffsetZ,
                color, maxU, startV, lightCoords, -normalY, -normalZ);

        float backOffsetY = normalY * POUR_FACE_OFFSET;
        float backOffsetZ = normalZ * POUR_FACE_OFFSET;
        addWaterVertex(buffer, pose, sprite, maxX, startY + backOffsetY, startZ + backOffsetZ,
                color, maxU, startV, lightCoords, normalY, normalZ);
        addWaterVertex(buffer, pose, sprite, maxX, endY + backOffsetY, endZ + backOffsetZ,
                color, maxU, endV, lightCoords, normalY, normalZ);
        addWaterVertex(buffer, pose, sprite, minX, endY + backOffsetY, endZ + backOffsetZ,
                color, minU, endV, lightCoords, normalY, normalZ);
        addWaterVertex(buffer, pose, sprite, minX, startY + backOffsetY, startZ + backOffsetZ,
                color, minU, startV, lightCoords, normalY, normalZ);
    }

    private static void addWaterVertex(
            VertexConsumer buffer,
            PoseStack.Pose pose,
            TextureAtlasSprite sprite,
            float x,
            float y,
            float z,
            int color,
            float u,
            float v,
            int lightCoords,
            float normalY,
            float normalZ
    ) {
        buffer.addVertex(pose, x, y, z)
                .setColor(color)
                .setUv(sprite.getU(u), sprite.getV(v))
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(lightCoords)
                .setNormal(pose, 0.0F, normalY, normalZ);
    }
}
