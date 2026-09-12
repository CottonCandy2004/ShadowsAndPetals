package com.sshakusora.shadowsandpetals.client.tooltip;

import com.sshakusora.shadowsandpetals.block.RockeryDimensions;
import com.sshakusora.shadowsandpetals.block.nature.RockeryBlock;

/**
 * Immutable state used by the 1.21.1 tooltip preview.
 *
 * <p>The newer source tree renders this state through NeoForge's picture-in-picture
 * pipeline. That pipeline does not exist in 1.21.1, so the state is deliberately
 * independent of GUI renderer internals and can be drawn directly by
 * {@link RockeryPreviewRenderer}.</p>
 */
public record RockeryPreviewState(
        RockeryBlock block,
        RockeryDimensions dimensions,
        float yawDegrees,
        boolean animate,
        int selectedPart
) {
    public RockeryPreviewState(RockeryBlock block, RockeryDimensions dimensions) {
        this(block, dimensions, 0.0F, false, -1);
    }
}
