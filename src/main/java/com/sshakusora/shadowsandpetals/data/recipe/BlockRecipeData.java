package com.sshakusora.shadowsandpetals.data.recipe;

import com.sshakusora.shadowsandpetals.data.DatagenRecipeFactory;
import com.sshakusora.shadowsandpetals.data.ModRecipeProvider;
import com.sshakusora.shadowsandpetals.recipe.WindChimeDyeRecipe;
import com.sshakusora.shadowsandpetals.registries.BlockRegistry;
import com.sshakusora.shadowsandpetals.registries.ItemRegistry;
import com.sshakusora.shadowsandpetals.registries.ItemTagRegistry;
import com.sshakusora.shadowsandpetals.util.WoolUtils;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredBlock;

public final class BlockRecipeData {
    private BlockRecipeData() {}

    public static void rawBauxiteBlock(ModRecipeProvider provider, DeferredBlock<?> block) {
        DatagenRecipeFactory.storageBlock(provider, block, ItemRegistry.RAW_BAUXITE.get(), "raw_bauxite_from_block");
    }

    public static void aluminumBlock(ModRecipeProvider provider, DeferredBlock<?> block) {
        DatagenRecipeFactory.storageBlock(provider, block, ItemRegistry.ALUMINUM_INGOT.get(), "aluminum_ingot_from_block");
    }

    public static void windChime(ModRecipeProvider provider, DeferredBlock<?> block) {
        provider.shaped(RecipeCategory.DECORATIONS, block.get())
                .define('S', Items.STRING)
                .define('G', Items.GLASS_PANE)
                .define('A', Items.AMETHYST_SHARD)
                .define('P', Items.PAPER)
                .pattern(" S ")
                .pattern("GAG")
                .pattern(" P ")
                .unlockedBy(provider.hasName(Items.AMETHYST_SHARD), provider.hasItem(Items.AMETHYST_SHARD))
                .save(provider.output());
        provider.output().accept(provider.id("wind_chime_ribbon_dyeing"),
                new WindChimeDyeRecipe(WindChimeDyeRecipe.Target.RIBBON), null);
        provider.output().accept(provider.id("wind_chime_vane_dyeing"),
                new WindChimeDyeRecipe(WindChimeDyeRecipe.Target.VANE), null);
        provider.output().accept(provider.id("wind_chime_dual_dyeing"),
                new WindChimeDyeRecipe(WindChimeDyeRecipe.Target.BOTH), null);
    }

    public static void copperTeapot(ModRecipeProvider provider, DeferredBlock<?> block) {
        provider.shaped(RecipeCategory.DECORATIONS, block.get())
                .define('C', Items.COPPER_INGOT)
                .pattern("C C")
                .pattern("C C")
                .pattern(" C ")
                .unlockedBy("has_copper_ingot", provider.hasItem(Items.COPPER_INGOT))
                .save(provider.output());
    }

    public static void rawConcrete(ModRecipeProvider provider, DeferredBlock<?> block) {
        provider.shaped(RecipeCategory.BUILDING_BLOCKS, block.get(), 8)
                .define('P', ItemTags.PLANKS)
                .define('C', Tags.Items.CONCRETE_POWDERS)
                .pattern("PPP")
                .pattern("PCP")
                .pattern("PPP")
                .unlockedBy("has_concrete_powder", provider.hasTag(Tags.Items.CONCRETE_POWDERS))
                .save(provider.output());
        provider.stonecutter(RecipeCategory.BUILDING_BLOCKS, block.get(), 1, Blocks.WHITE_CONCRETE);
        provider.shapeless(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.ISOLATED_RAW_CONCRETE.get())
                .requires(block.get())
                .unlockedBy(provider.hasName(block.get()), provider.hasItem(block.get()))
                .save(provider.output(), provider.id("isolated_raw_concrete_from_raw_concrete").toString());
    }

    public static void isolatedRawConcrete(ModRecipeProvider provider, DeferredBlock<?> block) {
        provider.shapeless(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.RAW_CONCRETE.get())
                .requires(block.get())
                .unlockedBy(provider.hasName(block.get()), provider.hasItem(block.get()))
                .save(provider.output(), provider.id("raw_concrete_from_isolated_raw_concrete").toString());
    }

    public static void aluminumIngotPile(ModRecipeProvider provider, DeferredBlock<?> pile) {
        DatagenRecipeFactory.ingotPile(provider, pile, ItemRegistry.ALUMINUM_INGOT.get(), "aluminum_ingot_from_pile");
    }

    public static void ironIngotPile(ModRecipeProvider provider, DeferredBlock<?> pile) {
        DatagenRecipeFactory.ingotPile(provider, pile, Items.IRON_INGOT, "iron_ingot_from_pile");
    }

    public static void copperIngotPile(ModRecipeProvider provider, DeferredBlock<?> pile) {
        DatagenRecipeFactory.ingotPile(provider, pile, Items.COPPER_INGOT, "copper_ingot_from_pile");
    }

    public static void goldIngotPile(ModRecipeProvider provider, DeferredBlock<?> pile) {
        DatagenRecipeFactory.ingotPile(provider, pile, Items.GOLD_INGOT, "gold_ingot_from_pile");
    }

    public static void netheriteIngotPile(ModRecipeProvider provider, DeferredBlock<?> pile) {
        DatagenRecipeFactory.ingotPile(provider, pile, Items.NETHERITE_INGOT, "netherite_ingot_from_pile");
    }

    public static void irori(ModRecipeProvider provider, DeferredBlock<?> block) {
        provider.shaped(RecipeCategory.DECORATIONS, block.get())
                .define('L', ItemTags.LOGS)
                .define('B', Items.STONE_BRICKS)
                .define('G', Items.GRAVEL)
                .pattern("LLL")
                .pattern("BGB")
                .pattern("BBB")
                .unlockedBy(provider.hasName(Items.STONE_BRICKS), provider.hasItem(Items.STONE_BRICKS))
                .save(provider.output());
    }

    public static void bedroomLamp(ModRecipeProvider provider, DeferredBlock<?> lamp) {
        provider.shapeless(RecipeCategory.DECORATIONS, lamp.get(), 2)
                .requires(provider.ingredient(Tags.Items.DUSTS_REDSTONE), 2)
                .requires(Items.GLOWSTONE)
                .requires(Tags.Items.INGOTS_IRON)
                .unlockedBy(provider.hasName(Items.GLOWSTONE), provider.hasItem(Items.GLOWSTONE))
                .save(provider.output());
    }

    public static void wallLamp(ModRecipeProvider provider, DeferredBlock<?> lamp) {
        provider.shapeless(RecipeCategory.DECORATIONS, lamp.get(), 2)
                .requires(provider.ingredient(Tags.Items.DUSTS_REDSTONE), 2)
                .requires(Items.GLOWSTONE)
                .requires(ItemRegistry.ALUMINUM_INGOT.get())
                .unlockedBy(provider.hasName(Items.GLOWSTONE), provider.hasItem(Items.GLOWSTONE))
                .save(provider.output());
    }

    public static void emergencyLamp(ModRecipeProvider provider, DeferredBlock<?> lamp) {
        provider.shapeless(RecipeCategory.DECORATIONS, lamp.get(), 2)
                .requires(provider.ingredient(Tags.Items.DUSTS_REDSTONE), 2)
                .requires(Items.WHITE_STAINED_GLASS)
                .requires(Items.GLOWSTONE)
                .requires(Tags.Items.INGOTS_IRON)
                .unlockedBy(provider.hasName(Items.GLOWSTONE), provider.hasItem(Items.GLOWSTONE))
                .save(provider.output());
        provider.shapeless(RecipeCategory.DECORATIONS, lamp.get(), 2)
                .requires(provider.ingredient(Tags.Items.DUSTS_REDSTONE), 2)
                .requires(Items.WHITE_STAINED_GLASS)
                .requires(Items.GLOWSTONE)
                .requires(ItemRegistry.ALUMINUM_INGOT.get())
                .unlockedBy(provider.hasName(Items.GLOWSTONE), provider.hasItem(Items.GLOWSTONE))
                .save(provider.output(), provider.id("emergency_lamp_from_aluminum").toString());
    }

    public static void recessedLamp(ModRecipeProvider provider, DeferredBlock<?> lamp) {
        provider.shaped(RecipeCategory.DECORATIONS, lamp.get(), 4)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('L', Items.GLOWSTONE)
                .define('G', Tags.Items.GLASS_PANES_COLORLESS)
                .pattern("GIG")
                .pattern("ILI")
                .pattern("GIG")
                .unlockedBy("has_iron_ingot", provider.hasTag(Tags.Items.INGOTS_IRON))
                .save(provider.output());
        var aluminum = ItemRegistry.ALUMINUM_INGOT.get();
        provider.shaped(RecipeCategory.DECORATIONS, lamp.get(), 4)
                .define('I', aluminum)
                .define('L', Items.GLOWSTONE)
                .define('G', Tags.Items.GLASS_PANES_COLORLESS)
                .pattern("GIG")
                .pattern("ILI")
                .pattern("GIG")
                .unlockedBy("has_aluminum_ingot", provider.hasItem(aluminum))
                .save(provider.output(), provider.id("recessed_lamp_from_aluminum").toString());
    }

    public static void deskLamp(ModRecipeProvider provider, DeferredBlock<?> lamp) {
        provider.shapeless(RecipeCategory.DECORATIONS, lamp.get(), 2)
                .requires(provider.ingredient(Tags.Items.DUSTS_REDSTONE), 2)
                .requires(Items.GLOWSTONE)
                .requires(ItemRegistry.ALUMINUM_INGOT.get(), 2)
                .unlockedBy(provider.hasName(Items.GLOWSTONE), provider.hasItem(Items.GLOWSTONE))
                .save(provider.output());
    }

    public static void bonsai(ModRecipeProvider provider, DeferredBlock<?> block) {
        var aluminum = ItemRegistry.ALUMINUM_INGOT.get();
        provider.shaped(RecipeCategory.DECORATIONS, block.get(), 3)
                .define('A', aluminum)
                .define('D', ItemTags.DIRT)
                .pattern("ADA")
                .pattern(" A ")
                .unlockedBy("has_aluminum_ingot", provider.hasItem(aluminum))
                .save(provider.output());
    }

    public static void woodenBarrel(ModRecipeProvider provider, DeferredBlock<?> block) {
        provider.shaped(RecipeCategory.DECORATIONS, block.get())
                .define('S', ItemTags.WOODEN_SLABS)
                .pattern(" S ")
                .pattern("SSS")
                .pattern(" S ")
                .unlockedBy(provider.hasName(Items.OAK_PLANKS), provider.hasItem(Items.OAK_PLANKS))
                .save(provider.output());
        DatagenRecipeFactory.woodenBarrelFluid(provider, block, Fluids.WATER,
                Tags.Items.BUCKETS_WATER, "wooden_barrel_from_water_bucket");
        DatagenRecipeFactory.woodenBarrelFluid(provider, block, NeoForgeMod.MILK.value(),
                Tags.Items.BUCKETS_MILK, "wooden_barrel_from_milk_bucket");
    }

    public static void roofTile(ModRecipeProvider provider, DeferredBlock<?> block, DyeColor color) {
        provider.shaped(RecipeCategory.BUILDING_BLOCKS, block.get(), 8)
                .define('S', Items.STONE_BRICKS)
                .define('D', color.getTag())
                .pattern("SSS")
                .pattern("SDS")
                .pattern("SSS")
                .unlockedBy(provider.hasName(Items.STONE_BRICKS), provider.hasItem(Items.STONE_BRICKS))
                .save(provider.output());
    }

    public static void roofTileSlab(ModRecipeProvider provider, DeferredBlock<?> block, DyeColor color) {
        var base = BlockRegistry.ROOF_TILES.get(color).get();
        provider.slabFromBase(RecipeCategory.BUILDING_BLOCKS, block.get(), base);
        provider.stonecutter(RecipeCategory.BUILDING_BLOCKS, block.get(), 2, base);
    }

    public static void roofTileVerticalSlab(ModRecipeProvider provider, DeferredBlock<?> block, DyeColor color) {
        var slab = BlockRegistry.ROOF_TILE_SLABS.get(color).get();
        provider.shaped(RecipeCategory.BUILDING_BLOCKS, block.get(), 3)
                .define('S', slab)
                .pattern("S")
                .pattern("S")
                .pattern("S")
                .unlockedBy(provider.hasName(slab), provider.hasItem(slab))
                .save(provider.output());
        provider.shapeless(RecipeCategory.BUILDING_BLOCKS, slab)
                .requires(block.get())
                .unlockedBy(provider.hasName(block.get()), provider.hasItem(block.get()))
                .save(provider.output(), provider.id(color.getName() + "_roof_tile_vertical_slab_revert").toString());
        provider.stonecutter(RecipeCategory.BUILDING_BLOCKS, block.get(), 2,
                BlockRegistry.ROOF_TILES.get(color).get());
    }

    public static void roofTileStairs(ModRecipeProvider provider, DeferredBlock<?> block, DyeColor color) {
        var base = BlockRegistry.ROOF_TILES.get(color).get();
        provider.stairsFromBase(block.get(), base);
        provider.stonecutter(RecipeCategory.BUILDING_BLOCKS, block.get(), 1, base);
    }

    public static void vanity(ModRecipeProvider provider, DeferredBlock<?> vanity,
                              com.sshakusora.shadowsandpetals.block.WoodBlockList.WoodType woodType) {
        provider.shaped(RecipeCategory.DECORATIONS, vanity.get())
                .define('S', woodType.getSlab())
                .define('G', Items.GLASS_PANE)
                .pattern("S  ")
                .pattern("G  ")
                .pattern("SSS")
                .unlockedBy(provider.hasName(woodType.getPlanks()), provider.hasItem(woodType.getPlanks()))
                .save(provider.output());
    }

    public static void casementWindow(ModRecipeProvider provider, DeferredBlock<?> block,
                                      com.sshakusora.shadowsandpetals.block.WoodBlockList.WoodType woodType) {
        provider.shaped(RecipeCategory.DECORATIONS, block.get(), 8)
                .define('P', woodType.getPlanks())
                .define('G', Tags.Items.GLASS_BLOCKS_COLORLESS)
                .pattern("   ")
                .pattern("GP ")
                .pattern("   ")
                .unlockedBy(provider.hasName(woodType.getPlanks()), provider.hasItem(woodType.getPlanks()))
                .save(provider.output());
    }

    public static void redLacqueredWoodPillar(ModRecipeProvider provider, DeferredBlock<?> block) {
        provider.shapeless(RecipeCategory.DECORATIONS, block.get())
                .requires(provider.ingredient(ItemTagRegistry.STRIPPED_WOOD_PILLARS))
                .requires(Items.RED_DYE)
                .unlockedBy(provider.hasName(ItemTagRegistry.STRIPPED_WOOD_PILLARS),
                        provider.hasTag(ItemTagRegistry.STRIPPED_WOOD_PILLARS))
                .save(provider.output());
    }

    public static void cafeChair(ModRecipeProvider provider, DeferredBlock<?> chair, DyeColor color) {
        var wool = WoolUtils.getWool(color);
        provider.shaped(RecipeCategory.DECORATIONS, chair.get())
                .define('W', wool)
                .define('S', Items.STICK)
                .pattern(" W ")
                .pattern(" S ")
                .pattern("SSS")
                .unlockedBy(provider.hasName(wool), provider.hasItem(wool))
                .save(provider.output());
    }

    public static void shishiOdoshi(ModRecipeProvider provider, DeferredBlock<?> block) {
        provider.shaped(RecipeCategory.DECORATIONS, block.get())
                .define('B', Items.BAMBOO)
                .define('S', Items.COBBLESTONE)
                .pattern("BBB")
                .pattern("SSS")
                .unlockedBy(provider.hasName(Items.BAMBOO), provider.hasItem(Items.BAMBOO))
                .save(provider.output());
    }

    public static void shishiOdoshiPipe(ModRecipeProvider provider, DeferredBlock<?> block) {
        provider.shaped(RecipeCategory.DECORATIONS, block.get())
                .define('B', Items.BAMBOO)
                .pattern("B")
                .pattern("B")
                .unlockedBy(provider.hasName(Items.BAMBOO), provider.hasItem(Items.BAMBOO))
                .save(provider.output());
    }

    public static void sofa(ModRecipeProvider provider, DeferredBlock<?> sofa, DyeColor color) {
        var wool = WoolUtils.getWool(color);
        provider.shaped(RecipeCategory.DECORATIONS, sofa.get(), 4)
                .define('P', ItemTags.PLANKS)
                .define('W', wool)
                .pattern("PW ")
                .pattern("PWW")
                .pattern("PPW")
                .unlockedBy(provider.hasName(wool), provider.hasItem(wool))
                .save(provider.output());
    }
}
