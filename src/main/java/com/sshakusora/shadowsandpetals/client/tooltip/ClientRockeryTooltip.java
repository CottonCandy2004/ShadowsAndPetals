package com.sshakusora.shadowsandpetals.client.tooltip;

import com.sshakusora.shadowsandpetals.block.RockeryDimensions;
import com.sshakusora.shadowsandpetals.block.nature.RockeryBlock;
import com.sshakusora.shadowsandpetals.data.BuiltinLanguageKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.joml.Matrix4f;

/**
 * 1.21.1 client tooltip component for rockery blocks.
 *
 * <p>Holding Shift expands the compact hint into a block-item footprint preview
 * and a colour-coded dimension label. The later picture-in-picture renderer is
 * intentionally not referenced because it is absent from the 1.21.1 client API.</p>
 */
public final class ClientRockeryTooltip implements ClientTooltipComponent {
    private static final int LINE_HEIGHT = 9;
    private static final int PREVIEW_SIZE = 64;
    private static final int PADDING = 4;

    private final RockeryBlock block;
    private final RockeryDimensions dimensions;

    public ClientRockeryTooltip(RockeryTooltipComponent component) {
        this.block = component.block();
        this.dimensions = component.dimensions();
    }

    @Override
    public int getHeight() {
        if (!Screen.hasShiftDown()) {
            return LINE_HEIGHT + PADDING;
        }
        return LINE_HEIGHT + PADDING * 2 + PREVIEW_SIZE + LINE_HEIGHT;
    }

    @Override
    public int getWidth(Font font) {
        int hintWidth = font.width(hintText(Screen.hasShiftDown()));
        if (!Screen.hasShiftDown()) {
            return hintWidth + PADDING * 2;
        }
        int labelWidth = font.width(dimensionLabel(dimensions));
        return Math.max(hintWidth, Math.max(PREVIEW_SIZE, labelWidth)) + PADDING * 2;
    }

    @Override
    public void renderText(Font font, int x, int y, Matrix4f pose, MultiBufferSource.BufferSource bufferSource) {
        // The legacy tooltip renderer calls renderText before renderImage. Draw
        // everything in renderImage so the hint and preview share one layout.
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        boolean expanded = Screen.hasShiftDown();
        Component hint = hintText(expanded);
        graphics.drawString(font, hint, x + PADDING, y + 1, 0xFFAAAAAA);
        if (!expanded) {
            return;
        }

        int previewY = y + LINE_HEIGHT + PADDING;
        int previewX = x + (getWidth(font) - PREVIEW_SIZE) / 2;
        RockeryPreviewRenderer.render(
                graphics,
                new RockeryPreviewState(block, dimensions),
                previewX,
                previewY,
                PREVIEW_SIZE);

        Component label = dimensionLabel(dimensions);
        int labelX = x + (getWidth(font) - font.width(label)) / 2;
        graphics.drawString(font, label, labelX, previewY + PREVIEW_SIZE + PADDING, 0xFFFFFFFF);
    }

    private static MutableComponent hintText(boolean active) {
        return TooltipHelper.buildHint(
                BuiltinLanguageKeys.ROCKERY_HOLD_FOR_PREVIEW.key(),
                Component.translatable(BuiltinLanguageKeys.TOOLTIP_HOLD_KEY_SHIFT.key()),
                active);
    }

    public static MutableComponent dimensionLabel(RockeryDimensions dimensions) {
        return Component.empty()
                .append(Component.translatable(BuiltinLanguageKeys.ROCKERY_DIMENSIONS_LABEL.key())
                        .withStyle(ChatFormatting.GRAY))
                .append(Component.literal(Integer.toString(dimensions.width())).withStyle(ChatFormatting.RED))
                .append(Component.literal("×").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal(Integer.toString(dimensions.height())).withStyle(ChatFormatting.GREEN))
                .append(Component.literal("×").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.literal(Integer.toString(dimensions.depth())).withStyle(ChatFormatting.BLUE));
    }
}
