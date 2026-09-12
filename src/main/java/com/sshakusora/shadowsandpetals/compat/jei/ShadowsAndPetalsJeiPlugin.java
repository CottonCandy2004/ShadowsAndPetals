package com.sshakusora.shadowsandpetals.compat.jei;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.item.barrel.WoodenBarrelItemFluid;
import com.sshakusora.shadowsandpetals.item.chime.WindChimeColors;
import com.sshakusora.shadowsandpetals.item.hammer.HammerItem;
import com.sshakusora.shadowsandpetals.recipe.WindChimeDyeRecipe;
import com.sshakusora.shadowsandpetals.registries.BlockRegistry;
import com.sshakusora.shadowsandpetals.registries.ItemRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

@JeiPlugin
@SuppressWarnings("removal")
public final class ShadowsAndPetalsJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ShadowsAndPetals.asResource("jei");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(
                BlockRegistry.WIND_CHIME.get().asItem(),
                (IIngredientSubtypeInterpreter<net.minecraft.world.item.ItemStack>) (stack, context) ->
                        context == UidContext.Recipe
                                ? IIngredientSubtypeInterpreter.NONE
                                : WindChimeColors.fromStack(stack).ribbon().getName()
                                 + ":" + WindChimeColors.fromStack(stack).vane().getName()
        );
        registration.registerSubtypeInterpreter(
                BlockRegistry.WOODEN_BARREL.get().asItem(),
                (IIngredientSubtypeInterpreter<net.minecraft.world.item.ItemStack>) (stack, context) -> {
                    if (context == UidContext.Recipe) {
                        return IIngredientSubtypeInterpreter.NONE;
                    }
                    return WoodenBarrelItemFluid.read(stack)
                            .map(fluid -> BuiltInRegistries.FLUID.getKey(fluid.getFluid()).toString())
                            .orElse("empty");
                }
        );
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new RockeryRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()
        ));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(
                RockeryRecipeCategory.TYPE,
                ItemRegistry.HAMMER.get(),
                ItemRegistry.CHISEL.get(),
                Blocks.STONE
        );
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addExtension(
                WindChimeDyeRecipe.class,
                WindChimeDyeRecipeExtension.INSTANCE
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(RockeryRecipeCategory.TYPE, rockeryRecipes());
    }

    private static List<RockeryInfoRecipe> rockeryRecipes() {
        return HammerItem.rockeryTemplates().stream()
                .map(template -> new RockeryInfoRecipe(template.block().get(), template.dimensions()))
                .toList();
    }
}
