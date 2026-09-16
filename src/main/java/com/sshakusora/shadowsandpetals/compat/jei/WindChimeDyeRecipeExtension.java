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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
        Ingredient dye = Ingredient.of(Tags.Items.DYES);
        Ingredient windChime = Ingredient.of(BlockRegistry.WIND_CHIME.get().asItem());
        List<Ingredient> ingredients = switch (target) {
            case RIBBON -> List.of(dye, windChime);
            case VANE -> List.of(windChime, dye);
            case BOTH -> List.of(dye, windChime, dye);
        };
        craftingGridHelper.createAndSetIngredients(
                builder,
                ingredients,
                1,
                target == WindChimeDyeRecipe.Target.BOTH ? 3 : 2
        );
        craftingGridHelper.createAndSetOutputs(builder, displayResults(target));
    }

    @Override
    public void onDisplayedIngredientsUpdate(
            RecipeHolder<WindChimeDyeRecipe> holder,
            List<IRecipeSlotDrawable> recipeSlots,
            IFocusGroup focuses
    ) {
        var outputSlot = recipeSlots.stream()
                .filter(slot -> slot.getRole() == RecipeIngredientRole.OUTPUT)
                .findFirst();
        outputSlot.ifPresent(IRecipeSlotDrawable::clearDisplayOverrides);

        ItemStack windChime = recipeSlots.stream()
                .filter(slot -> slot.getRole() == RecipeIngredientRole.INPUT)
                .map(slot -> slot.getDisplayedItemStack().orElse(ItemStack.EMPTY))
                .filter(stack -> stack.is(BlockRegistry.WIND_CHIME.get().asItem()))
                .findFirst()
                .orElse(ItemStack.EMPTY);
        if (windChime.isEmpty()) {
            return;
        }

        List<IRecipeSlotDrawable> dyeSlots = recipeSlots.stream()
                .filter(slot -> slot.getRole() == RecipeIngredientRole.INPUT)
                .filter(slot -> displayedDye(slot) != null)
                .sorted(Comparator.comparingInt(slot -> slot.getAreaIncludingBackground().getY()))
                .toList();
        WindChimeDyeRecipe.Target target = holder.value().target();
        int expectedDyeCount = target == WindChimeDyeRecipe.Target.BOTH ? 2 : 1;
        if (dyeSlots.size() != expectedDyeCount) {
            return;
        }

        DyeColor firstDye = displayedDye(dyeSlots.getFirst());
        if (firstDye == null) {
            return;
        }
        WindChimeColors colors = WindChimeColors.fromStack(windChime);
        if (target != WindChimeDyeRecipe.Target.VANE) {
            colors = colors.withRibbon(firstDye);
        }
        if (target != WindChimeDyeRecipe.Target.RIBBON) {
            DyeColor vaneDye = target == WindChimeDyeRecipe.Target.BOTH
                    ? displayedDye(dyeSlots.get(1))
                    : firstDye;
            if (vaneDye == null) {
                return;
            }
            colors = colors.withVane(vaneDye);
        }

        ItemStack output = windChime.copyWithCount(1);
        colors.applyToStack(output);
        outputSlot.ifPresent(slot -> slot.createDisplayOverrides().addItemStack(output));
    }

    private static @Nullable DyeColor displayedDye(IRecipeSlotDrawable slot) {
        return slot.getDisplayedItemStack().map(DyeColor::getColor).orElse(null);
    }

    private static List<ItemStack> displayResults(WindChimeDyeRecipe.Target target) {
        if (target == WindChimeDyeRecipe.Target.BOTH) {
            List<ItemStack> results = new ArrayList<>(DyeColor.values().length * DyeColor.values().length);
            for (DyeColor ribbon : DyeColor.values()) {
                for (DyeColor vane : DyeColor.values()) {
                    results.add(displayResult(ribbon, vane));
                }
            }
            return results;
        }

        List<ItemStack> results = new ArrayList<>(DyeColor.values().length);
        for (DyeColor color : DyeColor.values()) {
            results.add(target == WindChimeDyeRecipe.Target.RIBBON
                    ? displayResult(color, WindChimeColors.DEFAULT_COLOR)
                    : displayResult(WindChimeColors.DEFAULT_COLOR, color));
        }
        return results;
    }

    private static ItemStack displayResult(DyeColor ribbon, DyeColor vane) {
        ItemStack result = new ItemStack(BlockRegistry.WIND_CHIME.get().asItem());
        new WindChimeColors(ribbon, vane).applyToStack(result);
        return result;
    }
}
