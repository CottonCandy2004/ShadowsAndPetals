package com.sshakusora.shadowsandpetals.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.block.decoration.irori.IroriBlock;
import com.sshakusora.shadowsandpetals.blockentity.irori.IroriBlockEntity;
import com.sshakusora.shadowsandpetals.blockentity.irori.IroriComponentTopology;
import com.sshakusora.shadowsandpetals.client.effect.IroriClientEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

import java.util.Random;

public class IroriBlockEntityRenderer implements BlockEntityRenderer<IroriBlockEntity> {
    private static final long FIREWOOD_RENDER_SEED = 42L;
    private static final double FIREWOOD_Y = 10.0D / 16.0D;
    private static final double FIREWOOD_APPEAR_FALL_DISTANCE = 5.0D / 16.0D;
    private static final double BURNING_OVERLAY_Y = 10.01D / 16.0D;
    private static final double ITEM_Y = 21.2D / 16.0D;
    private final BlockRenderDispatcher blockRenderer;
    private final ItemRenderer itemRenderer;

    public IroriBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public AABB getRenderBoundingBox(IroriBlockEntity blockEntity) {
        if (blockEntity.getLevel() == null || blockEntity.getMaster() != blockEntity) {
            return new AABB(blockEntity.getBlockPos());
        }

        IroriComponentTopology.Bounds component = IroriComponentTopology.bounds(
                blockEntity.getLevel(),
                blockEntity.getBlockPos()
        );
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(
                component.minX(),
                pos.getY(),
                component.minZ(),
                component.maxX() + 1.0D,
                pos.getY() + 2.0D,
                component.maxZ() + 1.0D
        );
    }

    @Override
    public void render(IroriBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (blockEntity.shouldRenderFirewood() && blockEntity.getFirewoodModel() != null) {
            renderFirewood(blockEntity, partialTick, poseStack, buffer, packedLight, packedOverlay);
            renderBurningOverlay(blockEntity, poseStack, buffer, packedLight);
        }

        for (IroriBlockEntity.CookingRenderItem item : blockEntity.getCookingRenderItems()) {
            if (item.stack().isEmpty()) {
                continue;
            }
            Random random = new Random(item.seed() ^ 0x49524F52494C4F4EL);
            double jitterX = (random.nextDouble() - random.nextDouble()) / 16.0D;
            double jitterZ = (random.nextDouble() - random.nextDouble()) / 16.0D;
            poseStack.pushPose();
            poseStack.translate(item.offsetX() + 0.5D + jitterX, ITEM_Y, item.offsetZ() + 0.5D + jitterZ);
            poseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.scale(0.5F, 0.5F, 0.5F);
            LegacyBlockEntityRenderSupport.renderItem(
                    itemRenderer, item.stack(), poseStack, buffer, blockEntity.getLevel(), packedLight, packedOverlay);
            poseStack.popPose();
        }
    }

    private void renderFirewood(
            IroriBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int packedOverlay
    ) {
        var offset = blockEntity.getFirewoodRenderOffset();
        float appearProgress = IroriClientEffects.getFirewoodAppearProgress(blockEntity, partialTick);
        float rotation = getFirewoodRotation(blockEntity);
        poseStack.pushPose();
        poseStack.translate(
                offset.x(),
                FIREWOOD_Y + (1.0F - appearProgress) * FIREWOOD_APPEAR_FALL_DISTANCE,
                offset.z()
        );
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.translate(-0.5D, 0.0D, -0.5D);
        LegacyBlockEntityRenderSupport.renderStandalone(
                blockRenderer,
                ShadowsAndPetals.asResource("block/irori/firewood/" + blockEntity.getFirewoodModel().modelName()),
                blockEntity.getBlockState(),
                poseStack,
                buffer,
                packedLight,
                packedOverlay
        );
        poseStack.popPose();
    }

    private static float getFirewoodRotation(IroriBlockEntity blockEntity) {
        var layout = blockEntity.getGrillLayoutInfo();
        Random random = new Random(blockEntity.getBlockPos().asLong() ^ FIREWOOD_RENDER_SEED);
        if (layout == null) {
            return 0.0F;
        }
        if (layout.model() == IroriBlockEntity.GrillModel.ONE_BY_ONE) {
            return 45.0F + random.nextInt(4) * 90.0F;
        }
        float rotation = random.nextInt(16) * 22.5F;
        return layout.rotated() ? rotation + 90.0F : rotation;
    }

    private static void renderBurningOverlay(
            IroriBlockEntity blockEntity,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        if (blockEntity.getBurnTime() <= 0
                || blockEntity.getLevel() == null
                || blockEntity.getBlockState().getValue(IroriBlock.WATERLOGGED)) {
            return;
        }

        TextureAtlas atlas = (TextureAtlas) Minecraft.getInstance().getTextureManager()
                .getTexture(TextureAtlas.LOCATION_BLOCKS);
        TextureAtlasSprite sprite = atlas.getSprite(
                ShadowsAndPetals.asResource("block/irori/firewood/burning")
        );
        long gameTime = blockEntity.getLevel().getGameTime();
        float phase = (gameTime % 60L) / 60.0F;
        float breath = (float) ((Math.sin(phase * Math.PI * 2.0D) + 1.0D) * 0.5D);
        int light = ClientFluidRenderInfo.applyLightEmission(
                packedLight, 5 + Math.round(8.0F * breath)
        );
        var offset = blockEntity.getFirewoodRenderOffset();
        poseStack.pushPose();
        poseStack.translate(offset.x(), BURNING_OVERLAY_Y, offset.z());
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(getFirewoodRotation(blockEntity)));
        poseStack.translate(-0.5D, 0.0D, -0.5D);
        VertexConsumer consumer = buffer.getBuffer(RenderType.translucent());
        addBurningVertex(consumer, poseStack.last(), sprite, 0.0F, 0.0F, 0.0F, 0.0F, light);
        addBurningVertex(consumer, poseStack.last(), sprite, 0.0F, 0.0F, 1.0F, 0.0F, light);
        addBurningVertex(consumer, poseStack.last(), sprite, 1.0F, 0.0F, 1.0F, 1.0F, light);
        addBurningVertex(consumer, poseStack.last(), sprite, 1.0F, 0.0F, 0.0F, 1.0F, light);
        poseStack.popPose();
    }

    private static void addBurningVertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            TextureAtlasSprite sprite,
            float x,
            float y,
            float z,
            float u,
            int light
    ) {
        consumer.addVertex(pose, x, y, z)
                .setColor(0xFFFFFFFF)
                .setUv(sprite.getU(u), sprite.getV(z))
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
