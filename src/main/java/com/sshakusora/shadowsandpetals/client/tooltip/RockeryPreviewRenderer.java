package com.sshakusora.shadowsandpetals.client.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sshakusora.shadowsandpetals.block.RockeryDimensions;
import com.sshakusora.shadowsandpetals.block.nature.RockeryBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/** Renders rockery previews used by the block tooltip and the JEI carving page. */
public final class RockeryPreviewRenderer {
    /**
     * Pitch of the preview camera. Positive because the pose mirrors the vertical
     * axis for the GUI's downward Y; a negative pitch would show the models'
     * bottom faces instead of their tops.
     */
    private static final float PITCH_DEGREES = 30.0F;
    private static final float BASE_YAW_DEGREES = -45.0F;
    private static final long ROTATE_DURATION_NANOS = 1_550_000_000L;
    private static final long RESET_GAP_NANOS = 1_000_000_000L;
    private static final double OUTLINE_OFFSET = 0.0025D;

    /*
     * Tooltip components may be recreated as the tooltip is gathered. Keep the animation
     * clock here so recreation of a client component cannot restart the animation
     * every frame.
     */
    private static long animationStartNanos = -1L;
    private static long lastRenderNanos = -1L;

    private RockeryPreviewRenderer() {
    }

    /**
     * Draws the footprint as {@code size}×{@code size} GUI pixels with its top-left
     * corner at ({@code x}, {@code y}).
     */
    public static void render(GuiGraphics graphics, RockeryPreviewState state, int x, int y, int size) {
        RockeryDimensions dimensions = state.dimensions();
        float scale = RockeryPreviewState.scaleFor(dimensions, size, size);
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        PoseStack poseStack = graphics.pose();
        float yawDegrees = state.yawDegrees() + (state.animate() ? animatedYaw() : 0.0F);
        poseStack.pushPose();
        poseStack.translate(x + size * 0.5F, y + size * 0.5F, 100.0F);
        poseStack.scale(scale, -scale, scale);
        poseStack.mulPose(previewRotation(yawDegrees));
        poseStack.translate(-dimensions.width() * 0.5F, -dimensions.height() * 0.5F, -dimensions.depth() * 0.5F);

        for (int part = 0; part < dimensions.partCount(); part++) {
            Vec3i local = dimensions.localPos(part);
            poseStack.pushPose();
            poseStack.translate(local.getX(), local.getY(), local.getZ());
            blockRenderer.renderSingleBlock(
                    partState(state, part),
                    poseStack,
                    graphics.bufferSource(),
                    0xF000F0,
                    OverlayTexture.NO_OVERLAY,
                    ModelData.EMPTY,
                    null
            );
            poseStack.popPose();
        }

        if (state.content() == RockeryPreviewState.Content.STONE_STRUCTURE
                && state.selectedPart() >= 0
                && state.selectedPart() < dimensions.partCount()) {
            renderSelectionOutline(graphics, poseStack, dimensions, dimensions.localPos(state.selectedPart()));
        }

        poseStack.popPose();
        graphics.flush();
    }

    /**
     * Rotation applied to the centred footprint by {@link #render}. {@link #hitTest}
     * uses the same matrix so the drawn model and the part under the cursor agree.
     */
    public static Matrix4f previewRotation(float yawDegrees) {
        return new Matrix4f()
                .rotateX((float) Math.toRadians(PITCH_DEGREES))
                .rotateY((float) Math.toRadians(BASE_YAW_DEGREES + yawDegrees));
    }

    private static float animatedYaw() {
        long now = System.nanoTime();
        if (animationStartNanos < 0L
                || lastRenderNanos < 0L
                || now - lastRenderNanos > RESET_GAP_NANOS) {
            animationStartNanos = now;
        }

        lastRenderNanos = now;
        long elapsed = Math.max(0L, now - animationStartNanos);
        return elapsed * 90.0F / ROTATE_DURATION_NANOS;
    }

    /**
     * Index of the footprint part whose projected centre is closest to the pointer,
     * or {@code -1} when the pointer is farther than one block from every centre.
     *
     * @param scale   GUI pixels per model block, as returned by
     *                {@link RockeryPreviewState#scaleFor}
     * @param centerX horizontal centre of the preview in GUI coordinates
     * @param centerY vertical centre of the preview in GUI coordinates
     */
    public static int hitTest(
            RockeryDimensions dimensions,
            float yawDegrees,
            float scale,
            float centerX,
            float centerY,
            double mouseX,
            double mouseY
    ) {
        Matrix4f rotation = previewRotation(yawDegrees);
        Vector3f projected = new Vector3f();
        float radius = Math.max(7.0F, scale * 0.72F);
        float bestDistance = radius * radius;
        int bestPart = -1;

        for (int part = 0; part < dimensions.partCount(); part++) {
            Vec3i local = dimensions.localPos(part);
            rotation.transformPosition(
                    local.getX() + 0.5F - dimensions.width() / 2.0F,
                    local.getY() + 0.5F - dimensions.height() / 2.0F,
                    local.getZ() + 0.5F - dimensions.depth() / 2.0F,
                    projected
            );
            // render() scales the rotated model by (scale, -scale, scale): the GUI
            // projection is y-down, so the vertical axis is the mirrored one.
            float dx = (float) mouseX - (centerX + projected.x * scale);
            float dy = (float) mouseY - (centerY - projected.y * scale);
            float distance = dx * dx + dy * dy;
            if (distance < bestDistance) {
                bestDistance = distance;
                bestPart = part;
            }
        }
        return bestPart;
    }

    private static BlockState partState(RockeryPreviewState state, int part) {
        if (state.content() == RockeryPreviewState.Content.STONE_STRUCTURE) {
            return Blocks.STONE.defaultBlockState();
        }
        return state.block().defaultBlockState()
                .setValue(RockeryBlock.FACING, Direction.SOUTH)
                .setValue(RockeryBlock.PART, part);
    }

    private static void renderSelectionOutline(
            GuiGraphics graphics,
            PoseStack poseStack,
            RockeryDimensions dimensions,
            Vec3i selected
    ) {
        VertexConsumer lines = graphics.bufferSource().getBuffer(RenderType.lines());
        poseStack.pushPose();
        try {
            poseStack.translate(selected.getX(), selected.getY(), selected.getZ());
            LevelRenderer.renderVoxelShape(
                    poseStack,
                    lines,
                    selectionShape(dimensions, selected),
                    0.0D,
                    0.0D,
                    0.0D,
                    1.0F,
                    1.0F,
                    1.0F,
                    1.0F,
                    true
            );
        } finally {
            poseStack.popPose();
        }
    }

    static VoxelShape selectionShape(RockeryDimensions dimensions, Vec3i selected) {
        double minX = selected.getX() == 0 ? -OUTLINE_OFFSET : 0.0D;
        double minY = selected.getY() == 0 ? -OUTLINE_OFFSET : 0.0D;
        double minZ = selected.getZ() == 0 ? -OUTLINE_OFFSET : 0.0D;
        double maxX = selected.getX() == dimensions.width() - 1 ? 1.0D + OUTLINE_OFFSET : 1.0D;
        double maxY = selected.getY() == dimensions.height() - 1 ? 1.0D + OUTLINE_OFFSET : 1.0D;
        double maxZ = selected.getZ() == dimensions.depth() - 1 ? 1.0D + OUTLINE_OFFSET : 1.0D;
        return Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
