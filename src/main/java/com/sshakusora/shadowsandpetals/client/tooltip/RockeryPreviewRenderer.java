package com.sshakusora.shadowsandpetals.client.tooltip;

import com.sshakusora.shadowsandpetals.block.RockeryDimensions;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

/**
 * Direct 1.21.1 fallback renderer for the rockery tooltip preview.
 *
 * <p>1.21.1 has no picture-in-picture GUI render state API. Rendering the block
 * item into the tooltip keeps the preview useful and, importantly, avoids
 * reaching into the later renderer pipeline. The dimensions are rendered beside
 * the icon by {@link ClientRockeryTooltip}.</p>
 */
public final class RockeryPreviewRenderer {
    private RockeryPreviewRenderer() {
    }

    public static void render(GuiGraphics graphics, RockeryPreviewState state, int x, int y, int size) {
        int footprint = Math.max(1, Math.max(state.dimensions().width(), state.dimensions().depth()));
        int cellSize = Math.max(1, size / footprint);
        ItemStack stack = new ItemStack(state.block().asItem());

        // Show the footprint as a compact stack of item models. This conveys the
        // multi-block shape while staying within the legacy GUI render API.
        for (int localX = 0; localX < state.dimensions().width(); localX++) {
            for (int localZ = 0; localZ < state.dimensions().depth(); localZ++) {
                int itemX = x + localX * cellSize + Math.max(0, (cellSize - 16) / 2);
                int itemY = y + localZ * cellSize + Math.max(0, (cellSize - 16) / 2);
                graphics.renderItem(stack, itemX, itemY);
            }
        }
    }

    public static String dimensionLabel(RockeryDimensions dimensions) {
        return dimensions.displayName();
    }
}
