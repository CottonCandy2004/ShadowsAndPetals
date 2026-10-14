package com.sshakusora.shadowsandpetals.registries;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.compat.chinjufu.ChinjufuIds;
import com.sshakusora.shadowsandpetals.data.DatagenLangRegistry;
import com.sshakusora.shadowsandpetals.data.lang.ItemTooltipData;
import com.sshakusora.shadowsandpetals.data.recipe.ItemRecipeData;
import com.sshakusora.shadowsandpetals.item.hammer.HammerItem;
import com.sshakusora.shadowsandpetals.item.harrow.HarrowItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredItem;

public class ItemRegistry {
    public static final DeferredItem<BucketItem> TEA_BUCKET = SAPRegistries.item(
                    "tea_bucket",
                    properties -> new BucketItem(FluidRegistry.TEA.get(), properties)
            )
            .properties(properties -> properties.craftRemainder(Items.BUCKET).stacksTo(1))
            .customClientItem(ShadowsAndPetals.asResource("tea_bucket"))
            .lang(DatagenLangRegistry.ZH_CN, "茶桶")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_TEA)
            .register();

    public static final DeferredItem<BlockItem> ORANGE_SEED = SAPRegistries.item(
                    "orange_seed",
                    properties -> new BlockItem(BlockRegistry.ORANGE_TREE.get(), properties)
            )
            .alias(ChinjufuIds.MOD_ID, "block_wood_mikan")
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .lang(DatagenLangRegistry.ZH_CN, "蜜柑种子")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_CROPS)
            .register();

    public static final DeferredItem<Item> ORANGE = SAPRegistries.item("orange")
            .alias(ChinjufuIds.MOD_ID, "item_food_mikan")
            .properties(properties -> properties.food(new FoodProperties.Builder()
                    .nutrition(4)
                    .saturationModifier(0.3F)
                    .build()))
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .recipe(ItemRecipeData::orange)
            .lang(DatagenLangRegistry.ZH_CN, "蜜柑")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_INGREDIENTS)
            .tag(Tags.Items.FOODS_FRUIT)
            .register();

    public static final DeferredItem<Item> BEAN_POD = SAPRegistries.item("bean_pod")
            .properties(properties -> properties.food(new FoodProperties.Builder()
                    .nutrition(2)
                    .saturationModifier(0.3F)
                    .build()))
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .lang(DatagenLangRegistry.ZH_CN, "豆荚")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_INGREDIENTS)
            .tag(Tags.Items.FOODS_VEGETABLE)
            .register();

    public static final DeferredItem<Item> CABBAGE = SAPRegistries.item("cabbage")
            .alias(ChinjufuIds.MOD_ID, "item_food_cabbage")
            .properties(properties -> properties.food(new FoodProperties.Builder()
                    .nutrition(3)
                    .saturationModifier(0.3F)
                    .build()))
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .lang(DatagenLangRegistry.ZH_CN, "卷心菜")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_INGREDIENTS)
            .tag(Tags.Items.FOODS_VEGETABLE)
            .register();

    public static final DeferredItem<Item> CHERRY = SAPRegistries.item("cherry")
            .properties(properties -> properties.food(new FoodProperties.Builder()
                    .nutrition(2)
                    .saturationModifier(0.2F)
                    .build()))
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .lang(DatagenLangRegistry.ZH_CN, "樱桃")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_INGREDIENTS)
            .tag(Tags.Items.FOODS_FRUIT)
            .register();

    public static final DeferredItem<Item> CHESTNUT = SAPRegistries.item("chestnut")
            .properties(properties -> properties.food(new FoodProperties.Builder()
                    .nutrition(3)
                    .saturationModifier(0.3F)
                    .build()))
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .lang(DatagenLangRegistry.ZH_CN, "栗子")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_INGREDIENTS)
            .register();

    public static final DeferredItem<Item> CHILI_PEPPER = SAPRegistries.item("chili_pepper")
            .properties(properties -> properties.food(new FoodProperties.Builder()
                    .nutrition(1)
                    .saturationModifier(0.2F)
                    .build()))
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .lang(DatagenLangRegistry.ZH_CN, "辣椒")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_INGREDIENTS)
            .tag(Tags.Items.FOODS_VEGETABLE)
            .register();

    public static final DeferredItem<Item> CHINESE_CABBAGE = SAPRegistries.item("chinese_cabbage")
            .alias(ChinjufuIds.MOD_ID, "item_food_hakusai")
            .properties(properties -> properties.food(new FoodProperties.Builder()
                    .nutrition(3)
                    .saturationModifier(0.3F)
                    .build()))
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .lang(DatagenLangRegistry.ZH_CN, "白菜")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_INGREDIENTS)
            .tag(Tags.Items.FOODS_VEGETABLE)
            .register();

    public static final DeferredItem<Item> CORN = SAPRegistries.item("corn")
            .alias(ChinjufuIds.MOD_ID, "item_food_corn")
            .properties(properties -> properties.food(new FoodProperties.Builder()
                    .nutrition(3)
                    .saturationModifier(0.4F)
                    .build()))
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .lang(DatagenLangRegistry.ZH_CN, "玉米")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_INGREDIENTS)
            .tag(Tags.Items.FOODS_VEGETABLE)
            .register();

    public static final DeferredItem<Item> DRIED_RICE = SAPRegistries.item("dried_rice")
            .alias(ChinjufuIds.MOD_ID, "item_ine_dry")
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .lang(DatagenLangRegistry.ZH_CN, "干水稻")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_CROPS)
            .register();

    public static final DeferredItem<Item> GRAPE = SAPRegistries.item("grape")
            .alias(ChinjufuIds.MOD_ID, "item_food_grape")
            .properties(properties -> properties.food(new FoodProperties.Builder()
                    .nutrition(2)
                    .saturationModifier(0.2F)
                    .build()))
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .lang(DatagenLangRegistry.ZH_CN, "葡萄")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_INGREDIENTS)
            .tag(Tags.Items.FOODS_FRUIT)
            .register();

    public static final DeferredItem<Item> ONION = SAPRegistries.item("onion")
            .alias(ChinjufuIds.MOD_ID, "item_food_onion")
            .alias(ChinjufuIds.MOD_ID, "item_food_greenonion")
            .properties(properties -> properties.food(new FoodProperties.Builder()
                    .nutrition(2)
                    .saturationModifier(0.3F)
                    .build()))
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .lang(DatagenLangRegistry.ZH_CN, "洋葱")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_INGREDIENTS)
            .tag(Tags.Items.FOODS_VEGETABLE)
            .register();

    public static final DeferredItem<Item> RICE = SAPRegistries.item("rice")
            .alias(ChinjufuIds.MOD_ID, "item_ine")
            .alias(ChinjufuIds.MOD_ID, "item_kome")
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .lang(DatagenLangRegistry.ZH_CN, "水稻")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_CROPS)
            .register();

    public static final DeferredItem<Item> SPINACH = SAPRegistries.item("spinach")
            .alias(ChinjufuIds.MOD_ID, "item_food_spinach")
            .properties(properties -> properties.food(new FoodProperties.Builder()
                    .nutrition(2)
                    .saturationModifier(0.3F)
                    .build()))
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .lang(DatagenLangRegistry.ZH_CN, "菠菜")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_INGREDIENTS)
            .tag(Tags.Items.FOODS_VEGETABLE)
            .register();

    public static final DeferredItem<Item> STRAW = SAPRegistries.item("straw")
            .alias(ChinjufuIds.MOD_ID, "item_inewara")
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .lang(DatagenLangRegistry.ZH_CN, "稻草")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_CROPS)
            .register();

    public static final DeferredItem<Item> TOMATO = SAPRegistries.item("tomato")
            .alias(ChinjufuIds.MOD_ID, "item_food_tomato")
            .properties(properties -> properties.food(new FoodProperties.Builder()
                    .nutrition(2)
                    .saturationModifier(0.3F)
                    .build()))
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .lang(DatagenLangRegistry.ZH_CN, "番茄")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_INGREDIENTS)
            .tag(Tags.Items.FOODS_VEGETABLE)
            .register();

    public static final DeferredItem<Item> PICKLED_CHINESE_CABBAGE = SAPRegistries.item("pickled_chinese_cabbage")
            .properties(properties -> properties.food(new FoodProperties.Builder()
                    .nutrition(5)
                    .saturationModifier(0.6F)
                    .build()))
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .lang(DatagenLangRegistry.ZH_CN, "腌白菜")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_DISHES)
            .register();

    public static final DeferredItem<Item> RICE_BALL = SAPRegistries.item("rice_ball")
            .properties(properties -> properties.food(new FoodProperties.Builder()
                    .nutrition(5)
                    .saturationModifier(0.6F)
                    .build()))
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .lang(DatagenLangRegistry.ZH_CN, "饭团")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_DISHES)
            .register();

    public static final DeferredItem<Item> RAW_BAUXITE = SAPRegistries.item("raw_bauxite")
            .alias(ChinjufuIds.MOD_ID, "item_bauxite")
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .lang(DatagenLangRegistry.ZH_CN, "粗矾土")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.NATURE, CreativeTabOrder.NATURE_METALS)
            .register();

    public static final DeferredItem<Item> ALUMINUM_INGOT = SAPRegistries.item("aluminum_ingot")
            .alias(ChinjufuIds.MOD_ID, "item_ingot_alumi")
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .recipe(ItemRecipeData::aluminumIngot)
            .lang(DatagenLangRegistry.ZH_CN, "铝锭")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.NATURE, CreativeTabOrder.NATURE_METALS)
            .register();

    public static final DeferredItem<Item> CLAM = SAPRegistries.item("clam")
            .alias(ChinjufuIds.MOD_ID, "item_food_hamaguri")
            .alias(ChinjufuIds.MOD_ID, "item_hamaguri_shell")
            .model(() -> (context, generator) -> generator.generatedItem(context.get()))
            .lang(DatagenLangRegistry.ZH_CN, "蛤蜊")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_INGREDIENTS)
            .register();

    public static final DeferredItem<Item> CHISEL = SAPRegistries.item("chisel")
            .alias(ChinjufuIds.MOD_ID, "item_chisel")
            .tooltipDescription(ItemTooltipData::chisel)
            .recipe(ItemRecipeData::chisel)
            .lang(DatagenLangRegistry.ZH_CN, "凿子")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.TRADITIONS, CreativeTabOrder.TRADITIONS_TOOLS)
            .register();

    public static final DeferredItem<HammerItem> HAMMER = SAPRegistries.item("hammer", HammerItem::new)
            .tooltipDescription(ItemTooltipData::hammer)
            .recipe(ItemRecipeData::hammer)
            .lang(DatagenLangRegistry.ZH_CN, "锤子")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.TRADITIONS, CreativeTabOrder.TRADITIONS_TOOLS)
            .register();

    public static final DeferredItem<HarrowItem> HARROW = SAPRegistries.item("harrow", HarrowItem::new)
            .properties(properties -> properties.durability(256))
            .tooltipDescription(ItemTooltipData::harrow)
            .recipe(ItemRecipeData::harrow)
            .lang(DatagenLangRegistry.ZH_CN, "耙子")
            .creativeTab(CreativeTabRegistry.CreativeTabKey.TRADITIONS, CreativeTabOrder.TRADITIONS_TOOLS)
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_TOOLS)
            .register();

    public static void init() {}
}
