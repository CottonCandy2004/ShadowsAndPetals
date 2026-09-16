package com.sshakusora.shadowsandpetals.client.tooltip;

import com.sshakusora.shadowsandpetals.block.RockeryDimensions;
import com.sshakusora.shadowsandpetals.block.nature.RockeryBlock;

/**
 * Immutable state used by the preview renderer.
 *
 * @param content      which block model fills the W×H×D footprint
 * @param yawDegrees   extra yaw applied around the vertical axis
 * @param animate      whether the renderer adds its automatic preview rotation
 * @param selectedPart part index highlighted by the renderer, or {@code -1}
 */
public record RockeryPreviewState(
        RockeryBlock block,
        RockeryDimensions dimensions,
        Content content,
        float yawDegrees,
        boolean animate,
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
        this(block, dimensions, Content.ROCKERY, 0.0F, true, -1);
    }

    /**
     * Creates a non-animated preview with an explicit yaw. This keeps the compact
     * constructor used by interactive previews such as JEI from changing behavior.
     */
    public RockeryPreviewState(
            RockeryBlock block,
            RockeryDimensions dimensions,
            Content content,
            float yawDegrees,
            int selectedPart
    ) {
        this(block, dimensions, content, yawDegrees, false, selectedPart);
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
