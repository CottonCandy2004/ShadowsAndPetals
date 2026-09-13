package com.sshakusora.shadowsandpetals.client.tooltip;

import com.sshakusora.shadowsandpetals.block.RockeryDimensions;
import com.sshakusora.shadowsandpetals.block.nature.RockeryBlock;

/**
 * Immutable state used by the 1.21.1 preview renderer.
 *
 * <p>The newer source tree renders this state through NeoForge's picture-in-picture
 * pipeline. That pipeline does not exist in 1.21.1, so the state is deliberately
 * independent of GUI renderer internals and can be drawn directly by
 * {@link RockeryPreviewRenderer}.</p>
 *
 * @param content      which block model fills the W×H×D footprint
 * @param yawDegrees   extra yaw applied around the vertical axis
 * @param selectedPart part index highlighted by the renderer, or {@code -1}
 */
public record RockeryPreviewState(
        RockeryBlock block,
        RockeryDimensions dimensions,
        Content content,
        float yawDegrees,
        int selectedPart
) {
    /** Block model drawn for every footprint position. */
    public enum Content {
        /** Plain {@code minecraft:stone}, used for the block being carved. */
        STONE_STRUCTURE,
        /** The carved rockery model of {@link #block()}. */
        ROCKERY
    }

    public RockeryPreviewState(RockeryBlock block, RockeryDimensions dimensions) {
        this(block, dimensions, Content.ROCKERY, 0.0F, -1);
    }

    /**
     * GUI pixels per model block that fit the rotated footprint inside a square
     * preview, matching the rotation applied by {@link RockeryPreviewRenderer}.
     */
    public static float scaleFor(RockeryDimensions dimensions, int width, int height) {
        double horizontalExtent = Math.hypot(dimensions.width(), dimensions.depth());
        double verticalExtent = dimensions.height() * Math.cos(Math.toRadians(30.0))
                + horizontalExtent * Math.sin(Math.toRadians(30.0));
        double projectedExtent = Math.max(horizontalExtent, verticalExtent);
        return (float) (Math.min(width, height) * 0.78 / Math.max(1.0, projectedExtent));
    }
}
