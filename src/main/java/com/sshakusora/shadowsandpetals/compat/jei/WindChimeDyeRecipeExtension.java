package com.sshakusora.shadowsandpetals.compat.jei;

import com.sshakusora.shadowsandpetals.item.chime.WindChimeColors;
import com.sshakusora.shadowsandpetals.recipe.WindChimeDyeRecipe;
import com.sshakusora.shadowsandpetals.registries.BlockRegistry;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

/** JEI's 1.21.1 crafting extension for the custom wind-chime recipe. */
final class WindChimeDyeRecipeExtension implements ICraftingCategoryExtension<WindChimeDyeRecipe> {
    static final WindChimeDyeRecipeExtension INSTANCE = new WindChimeDyeRecipeExtension();

    private WindChimeDyeRecipeExtension() {
    }

    @Override
    public void setRecipe(
            RecipeHolder<WindChimeDyeRecipe> holder,
            IRecipeLayoutBuilder builder,
            ICraftingGridHelper craftingGridHelper,
            IFocusGroup focuses
    ) {
        WindChimeDyeRecipe.Target target = holder.value().target();
        List<Ingredient> ingredients = new ArrayList<>(List.of(
                Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY,
                Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY,
                Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY
        ));
        int center = 4;
        ingredients.set(center, Ingredient.of(BlockRegistry.WIND_CHIME.get().asItem()));
        if (target != WindChimeDyeRecipe.Target.VANE) {
            ingredients.set(1, Ingredient.of(Items.RED_DYE));
        }
        if (target != WindChimeDyeRecipe.Target.RIBBON) {
            ingredients.set(7, Ingredient.of(Items.BLUE_DYE));
        }
        craftingGridHelper.createAndSetIngredients(builder, ingredients, 3, 3);

        WindChimeColors colors = WindChimeColors.DEFAULT;
        if (target != WindChimeDyeRecipe.Target.VANE) {
            colors = colors.withRibbon(DyeColor.RED);
        }
        if (target != WindChimeDyeRecipe.Target.RIBBON) {
            colors = colors.withVane(DyeColor.BLUE);
        }
        ItemStack output = new ItemStack(BlockRegistry.WIND_CHIME.get().asItem());
        colors.applyToStack(output);
        craftingGridHelper.createAndSetOutputs(builder, List.of(output));
    }

    @Override
    public void onDisplayedIngredientsUpdate(
            RecipeHolder<WindChimeDyeRecipe> holder,
            List<IRecipeSlotDrawable> recipeSlots,
            IFocusGroup focuses
    ) {
        ItemStack windChime = recipeSlots.stream()
                .filter(slot -> slot.getRole() == RecipeIngredientRole.INPUT)
                .map(slot -> slot.getDisplayedItemStack().orElse(ItemStack.EMPTY))
                .filter(stack -> stack.is(BlockRegistry.WIND_CHIME.get().asItem()))
                .findFirst()
                .orElse(ItemStack.EMPTY);
        if (windChime.isEmpty()) {
            return;
        }

        List<DyeColor> dyes = recipeSlots.stream()
                .filter(slot -> slot.getRole() == RecipeIngredientRole.INPUT)
                .map(slot -> slot.getDisplayedItemStack().orElse(ItemStack.EMPTY))
                .map(DyeColor::getColor)
                .filter(java.util.Objects::nonNull)
                .toList();
        WindChimeDyeRecipe.Target target = holder.value().target();
        if (dyes.size() < (target == WindChimeDyeRecipe.Target.BOTH ? 2 : 1)) {
            return;
        }

        WindChimeColors colors = WindChimeColors.fromStack(windChime);
        if (target != WindChimeDyeRecipe.Target.VANE) {
            colors = colors.withRibbon(dyes.getFirst());
        }
        if (target != WindChimeDyeRecipe.Target.RIBBON) {
            colors = colors.withVane(target == WindChimeDyeRecipe.Target.BOTH ? dyes.get(1) : dyes.getFirst());
        }

        ItemStack output = windChime.copyWithCount(1);
        colors.applyToStack(output);
        recipeSlots.stream()
                .filter(slot -> slot.getRole() == RecipeIngredientRole.OUTPUT)
                .findFirst()
                .ifPresent(slot -> slot.createDisplayOverrides().addItemStack(output));
    }
}
