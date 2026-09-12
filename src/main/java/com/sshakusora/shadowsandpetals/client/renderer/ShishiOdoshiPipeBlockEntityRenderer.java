package com.sshakusora.shadowsandpetals.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.sshakusora.shadowsandpetals.api.shishiOdoshi.ShishiOdoshiFluidRegistry;
import com.sshakusora.shadowsandpetals.block.decoration.ShishiOdoshiPipeBlock;
import com.sshakusora.shadowsandpetals.blockentity.ShishiOdoshiBlockEntity;
import com.sshakusora.shadowsandpetals.blockentity.ShishiOdoshiPipeBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ShishiOdoshiPipeBlockEntityRenderer implements BlockEntityRenderer<ShishiOdoshiPipeBlockEntity> {
    private static final float WATER_SURFACE_Y = 1.02F / 16.0F;
    private static final float STREAM_Y_TOP = WATER_SURFACE_Y;
    private static final float MAIN_OPENING_Y = 10.863334F;
    private static final float MAIN_OPENING_Z = 4.377651F;
    private static final float MAIN_PIVOT_Y = 9.0F;
    private static final float MAIN_PIVOT_Z = 9.0F;
    private static final float DEFAULT_STREAM_Y_BOTTOM = (MAIN_OPENING_Y - 16.0F) / 16.0F;
    private static final float SOURCE_Z = 15.98F / 16.0F;
    private static final float SURFACE_OFFSET = 0.02F / 16.0F;
    private static final float INNER_STREAM_HALF_WIDTH = 0.48F / 16.0F;
    private static final float FALLING_STREAM_HALF_WIDTH = 0.48F / 16.0F;
    private static final float STREAM_FADE_LENGTH = 2.0F / 16.0F;
    private static final float FLOW_UV_SCALE = 0.5F;
    private static final float FLOW_UV_SCROLL_PER_TICK = 1.0F / 40.0F;
    private static final float FLOW_U_CENTER = 0.5F;
    private static final float FLOW_UV_WRAP_EPSILON = 1.0E-4F;

    private final ClientFluidRenderInfo.Cache<ShishiOdoshiPipeBlockEntity> fluidRenderInfoCache =
            new ClientFluidRenderInfo.Cache<>();

    public ShishiOdoshiPipeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public AABB getRenderBoundingBox(ShishiOdoshiPipeBlockEntity blockEntity) {
        ShishiOdoshiBlockEntity shishiOdoshi = blockEntity.getConnectedShishiOdoshi();
        double streamBottomY;
        if (shishiOdoshi != null) {
            streamBottomY = shishiOdoshi.getBlockPos().getY();
        } else {
            Vec3 impactPosition = blockEntity.getFallbackImpactPosition();
            streamBottomY = impactPosition == null
                    ? blockEntity.getBlockPos().getY() - 1.0D
                    : impactPosition.y;
        }

        BlockPos pos = blockEntity.getBlockPos();
        double downwardExpansion = Math.max(1.0D, pos.getY() - streamBottomY);
        return new AABB(pos).expandTowards(0.0D, -downwardExpansion, 0.0D);
    }

    @Override
    public boolean shouldRender(ShishiOdoshiPipeBlockEntity blockEntity, Vec3 cameraPosition) {
        double viewDistance = getViewDistance();
        return getRenderBoundingBox(blockEntity).distanceToSqr(cameraPosition)
                < viewDistance * viewDistance;
    }

    @Override
    public boolean shouldRenderOffScreen(ShishiOdoshiPipeBlockEntity blockEntity) {
        return true;
    }

    @Override
    public void render(ShishiOdoshiPipeBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (blockEntity.getLevel() == null) {
            return;
        }

        var blockState = blockEntity.getBlockState();
        Direction facing = blockState.getValue(ShishiOdoshiPipeBlock.FACING);
        ShishiOdoshiPipeBlock.PipeLength length = blockState.getValue(ShishiOdoshiPipeBlock.LENGTH);
        float streamBottomY = getStreamBottomY(blockEntity, partialTick);
        BlockPos sourcePos = blockEntity.getBlockPos().relative(facing.getOpposite());
        Fluid fluid = ShishiOdoshiFluidRegistry.findSourceFluid(blockEntity.getLevel(), sourcePos);
        if (fluid == null) {
            return;
        }

        ClientFluidRenderInfo.Info renderInfo = fluidRenderInfoCache.get(
                blockEntity, fluid, (BlockAndTintGetter) blockEntity.getLevel(), sourcePos
        );
        TextureAtlasSprite sprite = renderInfo.sprite();
        if (sprite == null) {
            return;
        }

        double elapsedTicks = blockEntity.getLevel().getGameTime() + (double) partialTick;
        double phase = elapsedTicks * ShishiOdoshiFluidRegistry.getAnimationSpeed(fluid)
                * FLOW_UV_SCROLL_PER_TICK;
        float flowOffset = (float) (phase - Math.floor(phase));
        int lightCoords = ClientFluidRenderInfo.applyLightEmission(
                packedLight, renderInfo.lightEmission()
        );
        PipeChannel channel = PipeChannel.forLength(length);

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot() + 180.0F));
        poseStack.translate(-0.5D, 0.0D, -0.5D);
        VertexConsumer consumer = buffer.getBuffer(RenderType.translucent());
        renderInnerStreamQuad(
                consumer, poseStack.last(), sprite, lightCoords, renderInfo.color(), channel, flowOffset
        );
        renderFallingStreamQuad(
                consumer, poseStack.last(), sprite, lightCoords, renderInfo.color(),
                channel, streamBottomY, flowOffset
        );
        poseStack.popPose();
    }

    private static float getStreamBottomY(ShishiOdoshiPipeBlockEntity blockEntity, float partialTick) {
        ShishiOdoshiBlockEntity shishiOdoshi = blockEntity.getConnectedShishiOdoshi();
        if (shishiOdoshi != null) {
            int verticalDistance = blockEntity.getBlockPos().getY() - shishiOdoshi.getBlockPos().getY();
            return getAnimatedOpeningY(shishiOdoshi.getTipAngle(partialTick), verticalDistance);
        }
        Vec3 impactPosition = blockEntity.getFallbackImpactPosition();
        return impactPosition == null
                ? DEFAULT_STREAM_Y_BOTTOM
                : (float) (impactPosition.y - blockEntity.getBlockPos().getY());
    }

    private static void renderInnerStreamQuad(
            VertexConsumer buffer,
            PoseStack.Pose pose,
            TextureAtlasSprite sprite,
            int lightCoords,
            int color,
            PipeChannel channel,
            float flowOffset
    ) {
        float minX = channel.centerX - INNER_STREAM_HALF_WIDTH;
        float maxX = channel.centerX + INNER_STREAM_HALF_WIDTH;
        float minZ = channel.outletZ - SURFACE_OFFSET;
        float maxZ = SOURCE_Z;
        float halfU = (maxX - minX) * FLOW_UV_SCALE * 0.5F;
        float minU = FLOW_U_CENTER - halfU;
        float maxU = FLOW_U_CENTER + halfU;
        float streamLength = maxZ - minZ;
        float segmentStartDistance = 0.0F;
        while (segmentStartDistance < streamLength) {
            float startV = getWrappedFlowV(segmentStartDistance, flowOffset);
            float distanceToWrap = (1.0F - startV) / FLOW_UV_SCALE;
            float segmentEndDistance = Math.min(streamLength, segmentStartDistance + distanceToWrap);
            float endV = startV + (segmentEndDistance - segmentStartDistance) * FLOW_UV_SCALE;
            float startZ = maxZ - segmentStartDistance;
            float endZ = maxZ - segmentEndDistance;
            renderInnerStreamSegment(
                    buffer, pose, sprite, minX, maxX, startZ, endZ,
                    minU, maxU, startV, endV, color, lightCoords
            );
            segmentStartDistance = segmentEndDistance;
        }
    }

    private static void renderInnerStreamSegment(
            VertexConsumer buffer,
            PoseStack.Pose pose,
            TextureAtlasSprite sprite,
            float minX,
            float maxX,
            float startZ,
            float endZ,
            float minU,
            float maxU,
            float startV,
            float endV,
            int color,
            int lightCoords
    ) {
        addVertex(buffer, pose, sprite, minX, WATER_SURFACE_Y, endZ, color, minU, endV, lightCoords, 0.0F, 1.0F, 0.0F);
        addVertex(buffer, pose, sprite, minX, WATER_SURFACE_Y, startZ, color, minU, startV, lightCoords, 0.0F, 1.0F, 0.0F);
        addVertex(buffer, pose, sprite, maxX, WATER_SURFACE_Y, startZ, color, maxU, startV, lightCoords, 0.0F, 1.0F, 0.0F);
        addVertex(buffer, pose, sprite, maxX, WATER_SURFACE_Y, endZ, color, maxU, endV, lightCoords, 0.0F, 1.0F, 0.0F);

        addVertex(buffer, pose, sprite, maxX, WATER_SURFACE_Y, endZ, color, maxU, endV, lightCoords, 0.0F, -1.0F, 0.0F);
        addVertex(buffer, pose, sprite, maxX, WATER_SURFACE_Y, startZ, color, maxU, startV, lightCoords, 0.0F, -1.0F, 0.0F);
        addVertex(buffer, pose, sprite, minX, WATER_SURFACE_Y, startZ, color, minU, startV, lightCoords, 0.0F, -1.0F, 0.0F);
        addVertex(buffer, pose, sprite, minX, WATER_SURFACE_Y, endZ, color, minU, endV, lightCoords, 0.0F, -1.0F, 0.0F);
    }

    private static void renderFallingStreamQuad(
            VertexConsumer buffer,
            PoseStack.Pose pose,
            TextureAtlasSprite sprite,
            int lightCoords,
            int color,
            PipeChannel channel,
            float streamBottomY,
            float flowOffset
    ) {
        float minX = channel.centerX - FALLING_STREAM_HALF_WIDTH;
        float maxX = channel.centerX + FALLING_STREAM_HALF_WIDTH;
        float minY = streamBottomY;
        float maxY = STREAM_Y_TOP;
        float z = channel.outletZ - SURFACE_OFFSET;
        float halfU = (maxX - minX) * FLOW_UV_SCALE * 0.5F;
        float minU = FLOW_U_CENTER - halfU;
        float maxU = FLOW_U_CENTER + halfU;
        float fadeStartY = Math.min(maxY, minY + STREAM_FADE_LENGTH);
        float innerStreamLength = SOURCE_Z - (channel.outletZ - SURFACE_OFFSET);
        float fallingStreamLength = maxY - minY;
        float segmentStartDistance = innerStreamLength;
        float streamEndDistance = innerStreamLength + fallingStreamLength;
        while (segmentStartDistance < streamEndDistance) {
            float startV = getWrappedFlowV(segmentStartDistance, flowOffset);
            float distanceToWrap = (1.0F - startV) / FLOW_UV_SCALE;
            float segmentEndDistance = Math.min(streamEndDistance, segmentStartDistance + distanceToWrap);
            float endV = startV + (segmentEndDistance - segmentStartDistance) * FLOW_UV_SCALE;
            float segmentTopY = maxY - (segmentStartDistance - innerStreamLength);
            float segmentBottomY = maxY - (segmentEndDistance - innerStreamLength);
            renderFallingStreamSegment(
                    buffer, pose, sprite, minX, maxX, segmentTopY, segmentBottomY, z,
                    minU, maxU, startV, endV,
                    getFadedStreamColor(color, segmentTopY, minY, fadeStartY),
                    getFadedStreamColor(color, segmentBottomY, minY, fadeStartY),
                    lightCoords
            );
            segmentStartDistance = segmentEndDistance;
        }
    }

    private static float getWrappedFlowV(float pathDistance, float flowOffset) {
        float wrappedV = pathDistance * FLOW_UV_SCALE - flowOffset;
        wrappedV -= Mth.floor(wrappedV);
        return wrappedV > 1.0F - FLOW_UV_WRAP_EPSILON ? 0.0F : wrappedV;
    }

    private static int getFadedStreamColor(int color, float y, float bottomY, float fadeStartY) {
        if (y >= fadeStartY) {
            return color;
        }
        float alphaScale = fadeStartY == bottomY
                ? 0.0F
                : Mth.clamp((y - bottomY) / (fadeStartY - bottomY), 0.0F, 1.0F);
        int alpha = Math.round((color >>> 24) * alphaScale);
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    private static void renderFallingStreamSegment(
            VertexConsumer buffer,
            PoseStack.Pose pose,
            TextureAtlasSprite sprite,
            float minX,
            float maxX,
            float topY,
            float bottomY,
            float z,
            float minU,
            float maxU,
            float topV,
            float bottomV,
            int topColor,
            int bottomColor,
            int lightCoords
    ) {
        addVertex(buffer, pose, sprite, minX, topY, z, topColor, minU, topV, lightCoords, 0.0F, 0.0F, 1.0F);
        addVertex(buffer, pose, sprite, minX, bottomY, z, bottomColor, minU, bottomV, lightCoords, 0.0F, 0.0F, 1.0F);
        addVertex(buffer, pose, sprite, maxX, bottomY, z, bottomColor, maxU, bottomV, lightCoords, 0.0F, 0.0F, 1.0F);
        addVertex(buffer, pose, sprite, maxX, topY, z, topColor, maxU, topV, lightCoords, 0.0F, 0.0F, 1.0F);

        addVertex(buffer, pose, sprite, maxX, topY, z, topColor, maxU, topV, lightCoords, 0.0F, 0.0F, -1.0F);
        addVertex(buffer, pose, sprite, maxX, bottomY, z, bottomColor, maxU, bottomV, lightCoords, 0.0F, 0.0F, -1.0F);
        addVertex(buffer, pose, sprite, minX, bottomY, z, bottomColor, minU, bottomV, lightCoords, 0.0F, 0.0F, -1.0F);
        addVertex(buffer, pose, sprite, minX, topY, z, topColor, minU, topV, lightCoords, 0.0F, 0.0F, -1.0F);
    }

    private static void addVertex(
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
            float normalX,
            float normalY,
            float normalZ
    ) {
        buffer.addVertex(pose, x, y, z)
                .setColor(color)
                .setUv(sprite.getU(u), sprite.getV(v))
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(lightCoords)
                .setNormal(pose, normalX, normalY, normalZ);
    }

    private static float getAnimatedOpeningY(float tipAngle, int verticalDistance) {
        float radians = tipAngle * Mth.DEG_TO_RAD;
        float relativeY = MAIN_OPENING_Y - MAIN_PIVOT_Y;
        float relativeZ = MAIN_OPENING_Z - MAIN_PIVOT_Z;
        float rotatedY = MAIN_PIVOT_Y
                + Mth.cos(radians) * relativeY
                - Mth.sin(radians) * relativeZ;
        return rotatedY / 16.0F - verticalDistance;
    }

    private record PipeChannel(float centerX, float outletZ) {
        private static PipeChannel forLength(ShishiOdoshiPipeBlock.PipeLength length) {
            return new PipeChannel(length.outletX(), length.outletZ());
        }
    }
}
