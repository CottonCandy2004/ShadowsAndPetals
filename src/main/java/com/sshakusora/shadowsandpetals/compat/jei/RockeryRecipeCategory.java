package com.sshakusora.shadowsandpetals.compat.jei;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.block.RockeryDimensions;
import com.sshakusora.shadowsandpetals.client.tooltip.ClientRockeryTooltip;
import com.sshakusora.shadowsandpetals.client.tooltip.RockeryPreviewRenderer;
import com.sshakusora.shadowsandpetals.client.tooltip.RockeryPreviewState;
import com.sshakusora.shadowsandpetals.data.BuiltinLanguageKeys;
import com.sshakusora.shadowsandpetals.item.hammer.HammerItem;
import com.sshakusora.shadowsandpetals.registries.ItemRegistry;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.inputs.IJeiGuiEventListener;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

/**
 * JEI page for hammer-carved rockeries.
 *
 * <p>Both sides of the recipe are block previews instead of item slots: the stone
 * footprint being carved on the left (hovering a block highlights it) and the
 * finished rockery on the right. Dragging either preview rotates both.</p>
 */
public final class RockeryRecipeCategory implements IRecipeCategory<RockeryInfoRecipe> {
    public static final RecipeType<RockeryInfoRecipe> TYPE = RecipeType.create(
            ShadowsAndPetals.MOD_ID,
            "rockery_carving",
            RockeryInfoRecipe.class
    );

    private static final int WIDTH = 184;
    private static final int HEIGHT = 60;
    private static final int PIP_SIZE = 52;
    private static final int INPUT_PIP_X = 2;
    private static final int OUTPUT_PIP_X = WIDTH - PIP_SIZE - 2;
    private static final int PIP_Y = (HEIGHT - PIP_SIZE) / 2;
    private static final int CENTER_X = WIDTH / 2;
    private static final int TOOL_Y = 3;
    private static final int ARROW_Y = 27;
    private static final int ARROW_BACKGROUND_COLOR = 0xFFAAAAAA;
    private static final int ARROW_PROGRESS_COLOR = 0xFF666666;
    private static final long NANOS_PER_TICK = 50_000_000L;

    private final IDrawable icon;

    public RockeryRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ItemRegistry.HAMMER.get()));
    }

    @Override
    public RecipeType<RockeryInfoRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(BuiltinLanguageKeys.JEI_ROCKERY_CARVING_TITLE.key());
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RockeryInfoRecipe recipe, IFocusGroup focuses) {
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStack(recipe.inputStack());
        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addItemStack(recipe.outputStack());
        builder.addSlot(RecipeIngredientRole.CATALYST, CENTER_X - 17, TOOL_Y)
                .setStandardSlotBackground()
                .addItemStack(new ItemStack(ItemRegistry.HAMMER.get()));
        builder.addSlot(RecipeIngredientRole.CATALYST, CENTER_X + 1, TOOL_Y)
                .setStandardSlotBackground()
                .addItemStack(new ItemStack(ItemRegistry.CHISEL.get()));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RockeryInfoRecipe recipe, IFocusGroup focuses) {
        RockeryPreviewWidget previewWidget = new RockeryPreviewWidget(recipe);
        builder.addWidget(previewWidget);
        builder.addGuiEventListener(previewWidget);
    }

    private static final class RockeryPreviewWidget implements IRecipeWidget, IJeiGuiEventListener {
        private static final float DRAG_SENSITIVITY = 1.5F;

        private final RockeryInfoRecipe recipe;
        private final long animationStartNanos = System.nanoTime();
        private float yawDegrees;

        private RockeryPreviewWidget(RockeryInfoRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public ScreenPosition getPosition() {
            return new ScreenPosition(0, 0);
        }

        @Override
        public ScreenRectangle getArea() {
            return new ScreenRectangle(0, 0, WIDTH, HEIGHT);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return button == 0 && isOverPreview(mouseX, mouseY);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
            if (button != 0) {
                return false;
            }
            yawDegrees = (yawDegrees + (float) dragX * DRAG_SENSITIVITY) % 360.0F;
            return true;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return button == 0;
        }

        @Override
        public void drawWidget(GuiGraphics graphics, double mouseX, double mouseY) {
            RockeryDimensions dimensions = recipe.dimensions();
            RockeryPreviewRenderer.render(
                    graphics,
                    new RockeryPreviewState(
                            recipe.block(),
                            dimensions,
                            RockeryPreviewState.Content.STONE_STRUCTURE,
                            yawDegrees,
                            hitTestStone(mouseX, mouseY)
                    ),
                    INPUT_PIP_X,
                    PIP_Y,
                    PIP_SIZE
            );
            RockeryPreviewRenderer.render(
                    graphics,
                    new RockeryPreviewState(
                            recipe.block(),
                            dimensions,
                            RockeryPreviewState.Content.ROCKERY,
                            yawDegrees,
                            -1
                    ),
                    OUTPUT_PIP_X,
                    PIP_Y,
                    PIP_SIZE
            );

            Font font = Minecraft.getInstance().font;
            int durationTicks = HammerItem.getEffectiveUseDuration(dimensions);
            drawProgressArrow(graphics, INPUT_PIP_X + PIP_SIZE + 4, OUTPUT_PIP_X - 4, durationTicks);

            String seconds = String.format(Locale.ROOT, "%.2f", durationTicks / 20.0F);
            Component timeLabel = Component.translatable(
                    BuiltinLanguageKeys.JEI_ROCKERY_HAMMERING_TIME.key(),
                    seconds
            ).withStyle(ChatFormatting.BLACK);
            graphics.drawString(font, timeLabel, CENTER_X - font.width(timeLabel) / 2, ARROW_Y + 10, 0xFFFFFFFF, false);

            Component dimensionsLabel = ClientRockeryTooltip.dimensionLabel(
                    dimensions,
                    ChatFormatting.BLACK,
                    ChatFormatting.BLACK
            );
            graphics.drawString(
                    font,
                    dimensionsLabel,
                    CENTER_X - font.width(dimensionsLabel) / 2,
                    HEIGHT - font.lineHeight - 2,
                    0xFFFFFFFF,
                    false
            );
        }

        private void drawProgressArrow(GuiGraphics graphics, int arrowStart, int arrowEnd, int durationTicks) {
            drawArrow(graphics, arrowStart, arrowEnd, ARROW_BACKGROUND_COLOR, arrowEnd);

            long durationNanos = Math.max(1, durationTicks) * NANOS_PER_TICK;
            long elapsedNanos = Math.max(0L, System.nanoTime() - animationStartNanos);
            float progress = (elapsedNanos % durationNanos) / (float) durationNanos;
            int progressEnd = arrowStart + (int) Math.ceil((arrowEnd - arrowStart) * progress);
            drawArrow(graphics, arrowStart, arrowEnd, ARROW_PROGRESS_COLOR, progressEnd);
        }

        private static void drawArrow(GuiGraphics graphics, int arrowStart, int arrowEnd, int color, int clipEnd) {
            fillClipped(graphics, arrowStart, ARROW_Y - 1, arrowEnd - 5, ARROW_Y + 1, color, clipEnd);
            fillClipped(graphics, arrowEnd - 6, ARROW_Y - 5, arrowEnd - 4, ARROW_Y + 6, color, clipEnd);
            fillClipped(graphics, arrowEnd - 4, ARROW_Y - 3, arrowEnd - 2, ARROW_Y + 4, color, clipEnd);
            fillClipped(graphics, arrowEnd - 2, ARROW_Y - 1, arrowEnd, ARROW_Y + 2, color, clipEnd);
        }

        private static void fillClipped(
                GuiGraphics graphics,
                int x0,
                int y0,
                int x1,
                int y1,
                int color,
                int clipEnd
        ) {
            int clippedX1 = Math.min(x1, clipEnd);
            if (clippedX1 > x0) {
                graphics.fill(x0, y0, clippedX1, y1, color);
            }
        }

        private int hitTestStone(double mouseX, double mouseY) {
            if (!isOverInputPreview(mouseX, mouseY)) {
                return -1;
            }
            return RockeryPreviewRenderer.hitTest(
                    recipe.dimensions(),
                    yawDegrees,
                    RockeryPreviewState.scaleFor(recipe.dimensions(), PIP_SIZE, PIP_SIZE),
                    INPUT_PIP_X + PIP_SIZE / 2.0F,
                    PIP_Y + PIP_SIZE / 2.0F,
                    mouseX,
                    mouseY
            );
        }

        private static boolean isOverPreview(double mouseX, double mouseY) {
            boolean withinY = mouseY >= PIP_Y && mouseY < PIP_Y + PIP_SIZE;
            boolean overInput = mouseX >= INPUT_PIP_X && mouseX < INPUT_PIP_X + PIP_SIZE;
            boolean overOutput = mouseX >= OUTPUT_PIP_X && mouseX < OUTPUT_PIP_X + PIP_SIZE;
            return withinY && (overInput || overOutput);
        }

        private static boolean isOverInputPreview(double mouseX, double mouseY) {
            return mouseY >= PIP_Y && mouseY < PIP_Y + PIP_SIZE
                    && mouseX >= INPUT_PIP_X && mouseX < INPUT_PIP_X + PIP_SIZE;
        }
    }
}
