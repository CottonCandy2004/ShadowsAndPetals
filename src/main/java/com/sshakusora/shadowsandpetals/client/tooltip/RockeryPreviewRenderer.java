package com.sshakusora.shadowsandpetals.client.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sshakusora.shadowsandpetals.block.RockeryDimensions;
import com.sshakusora.shadowsandpetals.block.nature.RockeryBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Direct 1.21.1 renderer for the rockery tooltip preview.
 *
 * <p>1.21.1 has no picture-in-picture GUI render state API, so the preview uses
 * the normal baked block renderer against the tooltip's buffer source. This
 * preserves the old angled, multi-part preview without depending on the later
 * render-state pipeline.</p>
 */
public final class RockeryPreviewRenderer {
    private static final double OUTLINE_OFFSET = 0.0025D;

    private RockeryPreviewRenderer() {
    }

    public static void render(GuiGraphics graphics, RockeryPreviewState state, int x, int y, int size) {
        RockeryDimensions dimensions = state.dimensions();
        int largestDimension = Math.max(
                1,
                Math.max(dimensions.width(), Math.max(dimensions.height(), dimensions.depth()))
        );
        float modelScale = Math.max(1.0F, (size - 6.0F) / largestDimension);
        float centerX = x + size * 0.5F;
        float centerY = y + size * 0.56F;

        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, 100.0F);
        poseStack.scale(modelScale, -modelScale, modelScale);
        poseStack.mulPose(Axis.XP.rotationDegrees(28.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(35.0F + state.yawDegrees()));
        poseStack.translate(-dimensions.width() * 0.5F, -dimensions.height() * 0.5F, -dimensions.depth() * 0.5F);

        for (int part = 0; part < dimensions.partCount(); part++) {
            var local = dimensions.localPos(part);
            poseStack.pushPose();
            poseStack.translate(local.getX(), local.getY(), local.getZ());
            blockRenderer.renderSingleBlock(
                    partState(state.block(), part),
                    poseStack,
                    graphics.bufferSource(),
                    0xF000F0,
                    OverlayTexture.NO_OVERLAY
            );
            poseStack.popPose();
        }
        poseStack.popPose();
        graphics.flush();
    }

    private static BlockState partState(RockeryBlock block, int part) {
        return block.defaultBlockState()
                .setValue(RockeryBlock.FACING, Direction.SOUTH)
                .setValue(RockeryBlock.PART, part);
    }

    public static String dimensionLabel(RockeryDimensions dimensions) {
        return dimensions.displayName();
    }

    static int pipTextureDimension(int guiDimension, int guiScale) {
        return Math.max(1, guiDimension * guiScale);
    }

    static VoxelShape selectionShape(RockeryDimensions dimensions, net.minecraft.core.Vec3i selected) {
        double minX = selected.getX() == 0 ? -OUTLINE_OFFSET : 0.0D;
        double minY = selected.getY() == 0 ? -OUTLINE_OFFSET : 0.0D;
        double minZ = selected.getZ() == 0 ? -OUTLINE_OFFSET : 0.0D;
        double maxX = selected.getX() == dimensions.width() - 1 ? 1.0D + OUTLINE_OFFSET : 1.0D;
        double maxY = selected.getY() == dimensions.height() - 1 ? 1.0D + OUTLINE_OFFSET : 1.0D;
        double maxZ = selected.getZ() == dimensions.depth() - 1 ? 1.0D + OUTLINE_OFFSET : 1.0D;
        return Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
