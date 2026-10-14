package com.sshakusora.shadowsandpetals.data.recipe;

import com.sshakusora.shadowsandpetals.data.ModRecipeProvider;
import com.sshakusora.shadowsandpetals.registries.ItemRegistry;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredItem;

public final class ItemRecipeData {
    private ItemRecipeData() {}

    public static void orange(ModRecipeProvider provider, DeferredItem<?> orange) {
        provider.shapeless(RecipeCategory.FOOD, ItemRegistry.ORANGE_SEED.get())
                .requires(orange.get())
                .unlockedBy(provider.hasName(orange.get()), provider.hasItem(orange.get()))
                .save(provider.output());
    }

    public static void aluminumIngot(ModRecipeProvider provider, DeferredItem<?> ingot) {
        var rawBauxite = ItemRegistry.RAW_BAUXITE.get();
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(rawBauxite), RecipeCategory.MISC, ingot.get(), 0.7F, 200)
                .unlockedBy(provider.hasName(rawBauxite), provider.hasItem(rawBauxite))
                .save(provider.output(), provider.id("aluminum_ingot_from_smelting").toString());
        SimpleCookingRecipeBuilder.blasting(
                        Ingredient.of(rawBauxite), RecipeCategory.MISC, ingot.get(), 0.7F, 100)
                .unlockedBy(provider.hasName(rawBauxite), provider.hasItem(rawBauxite))
                .save(provider.output(), provider.id("aluminum_ingot_from_blasting").toString());
    }

    public static void chisel(ModRecipeProvider provider, DeferredItem<?> item) {
        var aluminum = ItemRegistry.ALUMINUM_INGOT.get();
        provider.shaped(RecipeCategory.TOOLS, item.get())
                .define('A', aluminum)
                .pattern("A")
                .pattern("A")
                .unlockedBy(provider.hasName(aluminum), provider.hasItem(aluminum))
                .save(provider.output());
    }

    public static void hammer(ModRecipeProvider provider, DeferredItem<?> item) {
        var aluminum = ItemRegistry.ALUMINUM_INGOT.get();
        provider.shaped(RecipeCategory.TOOLS, item.get())
                .define('A', aluminum)
                .define('S', Items.STICK)
                .pattern("AAA")
                .pattern("ASA")
                .pattern(" S ")
                .unlockedBy(provider.hasName(aluminum), provider.hasItem(aluminum))
                .save(provider.output());
    }

    public static void harrow(ModRecipeProvider provider, DeferredItem<?> item) {
        provider.shaped(RecipeCategory.TOOLS, item.get())
                .define('B', Items.BAMBOO)
                .pattern("BBB")
                .pattern(" B ")
                .pattern(" B ")
                .unlockedBy(provider.hasName(Items.BAMBOO), provider.hasItem(Items.BAMBOO))
                .save(provider.output());
    }
}
