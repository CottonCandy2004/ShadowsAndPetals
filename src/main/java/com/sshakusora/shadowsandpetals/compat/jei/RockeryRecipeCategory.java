package com.sshakusora.shadowsandpetals.compat.jei;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.block.RockeryDimensions;
import com.sshakusora.shadowsandpetals.client.tooltip.RockeryPreviewRenderer;
import com.sshakusora.shadowsandpetals.client.tooltip.RockeryPreviewState;
import com.sshakusora.shadowsandpetals.data.BuiltinLanguageKeys;
import com.sshakusora.shadowsandpetals.item.hammer.HammerItem;
import com.sshakusora.shadowsandpetals.registries.ItemRegistry;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class RockeryRecipeCategory implements IRecipeCategory<RockeryInfoRecipe> {
    public static final RecipeType<RockeryInfoRecipe> TYPE = RecipeType.create(
            ShadowsAndPetals.MOD_ID,
            "rockery_carving",
            RockeryInfoRecipe.class
    );

    private static final int WIDTH = 184;
    private static final int HEIGHT = 60;
    private static final int PREVIEW_SIZE = 48;

    private final IDrawable icon;
    private final IDrawable arrow;

    public RockeryRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ItemRegistry.HAMMER.get()));
        this.arrow = guiHelper.getRecipeArrow();
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
        builder.addOutputSlot(160, 22)
                .setOutputSlotBackground()
                .addItemStack(recipe.outputStack());
    }

    @Override
    public void draw(
            RockeryInfoRecipe recipe,
            IRecipeSlotsView recipeSlots,
            GuiGraphics graphics,
            double mouseX,
            double mouseY
    ) {
        RockeryPreviewRenderer.render(
                graphics,
                new RockeryPreviewState(recipe.block(), recipe.dimensions()),
                8,
                4,
                PREVIEW_SIZE
        );
        arrow.draw(graphics, 82, 22);
        graphics.renderItem(new ItemStack(ItemRegistry.HAMMER.get()), 88, 2);
        graphics.renderItem(new ItemStack(ItemRegistry.CHISEL.get()), 106, 2);

        int durationTicks = HammerItem.getEffectiveUseDuration(recipe.dimensions());
        String seconds = String.format(java.util.Locale.ROOT, "%.2f", durationTicks / 20.0F);
        Component time = Component.translatable(
                BuiltinLanguageKeys.JEI_ROCKERY_HAMMERING_TIME.key(),
                seconds
        ).withStyle(ChatFormatting.DARK_GRAY);
        graphics.drawString(Minecraft.getInstance().font, time, 70, 50, 0xFF000000, false);

        Component dimensions = dimensionLabel(recipe.dimensions());
        graphics.drawString(Minecraft.getInstance().font, dimensions, 8, 50, 0xFF000000, false);
    }

    private static Component dimensionLabel(RockeryDimensions dimensions) {
        return Component.translatable(
                        BuiltinLanguageKeys.ROCKERY_DIMENSIONS_LABEL.key()
                )
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(dimensions.width() + "×" + dimensions.height() + "×" + dimensions.depth())
                        .withStyle(ChatFormatting.DARK_GRAY));
    }
}
