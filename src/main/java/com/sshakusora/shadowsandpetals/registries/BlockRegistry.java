package com.sshakusora.shadowsandpetals.registries;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.block.*;
import com.sshakusora.shadowsandpetals.block.agriculture.OrangeTreeBlock;
import com.sshakusora.shadowsandpetals.block.decoration.*;
import com.sshakusora.shadowsandpetals.block.decoration.bonsai.BonsaiBlock;
import com.sshakusora.shadowsandpetals.block.decoration.curtain.CurtainBlock;
import com.sshakusora.shadowsandpetals.block.decoration.curtain.LargeCurtainBlock;
import com.sshakusora.shadowsandpetals.block.decoration.curtain.LongCurtainBlock;
import com.sshakusora.shadowsandpetals.block.decoration.irori.IroriBlock;
import com.sshakusora.shadowsandpetals.block.decoration.irori.IroriGrillBlock;
import com.sshakusora.shadowsandpetals.block.decoration.irori.IroriGrillCopperTeapotBlock;
import com.sshakusora.shadowsandpetals.block.decoration.sofa.SofaBlock;
import com.sshakusora.shadowsandpetals.block.decoration.window.CasementWindowBlock;
import com.sshakusora.shadowsandpetals.block.nature.LeavesVerticalSlabBlock;
import com.sshakusora.shadowsandpetals.block.nature.RockeryBlock;
import com.sshakusora.shadowsandpetals.block.nature.SandExcavationBlock;
import com.sshakusora.shadowsandpetals.client.ct.CTTextureType;
import com.sshakusora.shadowsandpetals.client.tooltip.RockeryTooltipComponent;
import com.sshakusora.shadowsandpetals.compat.chinjufu.ChinjufuBlockCompat;
import com.sshakusora.shadowsandpetals.compat.chinjufu.ChinjufuIds;
import com.sshakusora.shadowsandpetals.data.DatagenLangRegistry;
import com.sshakusora.shadowsandpetals.data.lang.BlockTooltipData;
import com.sshakusora.shadowsandpetals.data.model.generator.*;
import com.sshakusora.shadowsandpetals.data.recipe.BlockRecipeData;
import com.sshakusora.shadowsandpetals.item.CopperTeapotBlockItem;
import com.sshakusora.shadowsandpetals.item.RecessedLampBlockItem;
import com.sshakusora.shadowsandpetals.item.barrel.WoodenBarrelBlockItem;
import com.sshakusora.shadowsandpetals.item.barrel.WoodenBarrelTooltipModifier;
import com.sshakusora.shadowsandpetals.item.chime.WindChimeTooltipModifier;
import com.sshakusora.shadowsandpetals.item.hammer.HammerItem;
import com.sshakusora.shadowsandpetals.worldgen.SAPTreeGrowers;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.List;

public class BlockRegistry {
    public static final WoodSetList WOOD_SETS = new WoodSetList();

    public static final WoodSetList.WoodSet SAKURA_SET = WOOD_SETS.get(WoodSetList.Type.SAKURA);
    public static final WoodSetList.WoodSet MAPLE_SET = WOOD_SETS.get(WoodSetList.Type.MAPLE);
    public static final WoodSetList.WoodSet GINKGO_SET = WOOD_SETS.get(WoodSetList.Type.GINKGO);

    public static final DeferredBlock<SaplingBlock> AUTUMN_OAK_SAPLING = WoodSetList.treeSapling(
            "autumn_oak_sapling",
            SAPTreeGrowers.AUTUMN_OAK,
            "秋橡树树苗"
    );
    public static final DeferredBlock<LeavesBlock> AUTUMN_OAK_LEAVES = WoodSetList.treeLeaves(
            "autumn_oak_leaves",
            AUTUMN_OAK_SAPLING,
            "秋橡树树叶",
            MapColor.COLOR_ORANGE,
            () -> ItemRegistry.CHESTNUT.get()
    );
    public static final DeferredBlock<CarpetBlock> AUTUMN_OAK_LEAVES_CARPET = WoodSetList.treeLeavesCarpet(
            "autumn_oak_leaves_carpet",
            AUTUMN_OAK_LEAVES,
            "秋橡树树叶地毯",
            MapColor.COLOR_ORANGE
    );
    public static final DeferredBlock<SlabBlock> AUTUMN_OAK_LEAVES_SLAB = WoodSetList.treeLeavesSlab(
            "autumn_oak_leaves_slab",
            AUTUMN_OAK_LEAVES,
            "秋橡树树叶台阶",
            MapColor.COLOR_ORANGE
    );
    public static final DeferredBlock<LeavesVerticalSlabBlock> AUTUMN_OAK_LEAVES_VERTICAL_SLAB = WoodSetList.treeLeavesVerticalSlab(
            "autumn_oak_leaves_vertical_slab",
            AUTUMN_OAK_LEAVES_SLAB,
            AUTUMN_OAK_LEAVES,
            "竖直秋橡树树叶台阶",
            MapColor.COLOR_ORANGE
    );
    public static final DeferredBlock<StairBlock> AUTUMN_OAK_LEAVES_STAIRS = WoodSetList.treeLeavesStairs(
            "autumn_oak_leaves_stairs",
            AUTUMN_OAK_LEAVES,
            "秋橡树树叶楼梯",
            MapColor.COLOR_ORANGE
    );
    public static final DeferredBlock<HedgeBlock> AUTUMN_OAK_HEDGE = WoodSetList.treeHedge(
            "autumn_oak_hedge",
            AUTUMN_OAK_LEAVES,
            "秋橡树树篱",
            MapColor.COLOR_ORANGE
    );

    public static final DeferredBlock<DropExperienceBlock> BAUXITE_ORE = SAPRegistries
            .block("bauxite_ore", props -> new DropExperienceBlock(UniformInt.of(1, 3), props))
            .alias(ChinjufuIds.MOD_ID, "block_bauxite_ore")
            .itemAlias(ChinjufuIds.MOD_ID, "block_bauxite_ore")
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .strength(3.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops())
            .tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
            .withItem()
            .creativeTab(CreativeTabRegistry.CreativeTabKey.NATURE, CreativeTabOrder.NATURE_METALS)
            .blockstate(() -> (context, generator) -> StandardBlockModels.cubeAll(
                    context, generator, generator.modLoc("block/bauxite_ore/bauxite_ore")))
            .loot((provider, ore) -> provider.dropOre(ore.get(), ItemRegistry.RAW_BAUXITE.get()))
            .lang(DatagenLangRegistry.ZH_CN, "矾土矿石")
            .register();

    public static final DeferredBlock<DropExperienceBlock> DEEPSLATE_BAUXITE_ORE = SAPRegistries
            .block("deepslate_bauxite_ore", props -> new DropExperienceBlock(UniformInt.of(1, 3), props))
            .alias(ChinjufuIds.MOD_ID, "block_bauxite_ore_deep")
            .itemAlias(ChinjufuIds.MOD_ID, "block_bauxite_ore_deep")
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE)
                    .strength(4.5F, 3.0F)
                    .sound(SoundType.DEEPSLATE)
                    .requiresCorrectToolForDrops())
            .tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
            .withItem()
            .creativeTab(CreativeTabRegistry.CreativeTabKey.NATURE, CreativeTabOrder.NATURE_METALS)
            .blockstate(() -> (context, generator) -> StandardBlockModels.cubeAll(
                    context, generator, generator.modLoc("block/bauxite_ore/deepslate_bauxite_ore")))
            .loot((provider, ore) -> provider.dropOre(ore.get(), ItemRegistry.RAW_BAUXITE.get()))
            .lang(DatagenLangRegistry.ZH_CN, "深层矾土矿石")
            .register();

    public static final DeferredBlock<Block> RAW_BAUXITE_BLOCK = SAPRegistries
            .block("raw_bauxite_block")
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK)
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.STONE)
                    .mapColor(MapColor.DIRT)
                    .requiresCorrectToolForDrops())
            .tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL)
            .withItem()
            .creativeTab(CreativeTabRegistry.CreativeTabKey.NATURE, CreativeTabOrder.NATURE_METALS)
            .blockstate(() -> StandardBlockModels::cubeAll)
            .loot((provider, block) -> provider.dropSelf(block.get()))
            .recipe(BlockRecipeData::rawBauxiteBlock)
            .lang(DatagenLangRegistry.ZH_CN, "粗矾土块")
            .register();

    public static final DeferredBlock<Block> ALUMINUM_BLOCK = SAPRegistries
            .block("aluminum_block")
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops())
            .tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL)
            .withItem()
            .creativeTab(CreativeTabRegistry.CreativeTabKey.NATURE, CreativeTabOrder.NATURE_METALS)
            .blockstate(() -> StandardBlockModels::cubeAll)
            .loot((provider, block) -> provider.dropSelf(block.get()))
            .recipe(BlockRecipeData::aluminumBlock)
            .lang(DatagenLangRegistry.ZH_CN, "铝块")
            .register();

    public static final DeferredBlock<WindChimeBlock> WIND_CHIME = SAPRegistries
            .block("wind_chime", WindChimeBlock::new)
            .properties(properties -> BlockBehaviour.Properties.of()
                    .strength(1.0F)
                    .sound(SoundType.GLASS)
                    .mapColor(MapColor.NONE)
                    .noOcclusion())
            .tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTagRegistry.WOOD_POST_HANGING_CONNECTIONS)
            .withItem()
            .tooltipDescription(BlockTooltipData::windChime)
            .tooltipModifier(new WindChimeTooltipModifier())
            .creativeTab(CreativeTabRegistry.CreativeTabKey.TRADITIONS, CreativeTabOrder.TRADITIONS_CHIMES)
            .blockstate(() -> WindChimeModels::block)
            .loot((provider, block) -> provider.dropSelf(block.get()))
            .recipe(BlockRecipeData::windChime)
            .itemModel(() -> WindChimeModels::item)
            .customClientItem(ShadowsAndPetals.asResource("wind_chime"))
            .lang(DatagenLangRegistry.ZH_CN, "风铃")
            .register();

    public static final DeferredBlock<CopperTeapotBlock> COPPER_TEAPOT = SAPRegistries
            .block("copper_teapot", CopperTeapotBlock::new)
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .requiresCorrectToolForDrops())
            .tags(BlockTags.MINEABLE_WITH_PICKAXE)
            .withCustomItem(CopperTeapotBlockItem::new)
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_TEA)
            .tooltipDescription(BlockTooltipData::copperTeapot)
            .blockstate(() -> DecorationBlockModels::copperTeapot)
            .recipe(BlockRecipeData::copperTeapot)
            .clientItem(ShadowsAndPetals.asResource("item/teapot/copper"))
            .lang(DatagenLangRegistry.ZH_CN, "铜茶壶")
            .register();

    public static final DeferredBlock<RawConcreteBlock> RAW_CONCRETE = SAPRegistries
            .block("raw_concrete", RawConcreteBlock::new)
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .strength(2.5F, 6.0F)
                    .sound(SoundType.STONE)
                    .mapColor(MapColor.CLAY)
                    .requiresCorrectToolForDrops())
            .tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL)
            .withItem()
            .tooltipDescription(BlockTooltipData::rawConcrete)
            .creativeTab(CreativeTabRegistry.CreativeTabKey.ARCHITECTURE, CreativeTabOrder.ARCHITECTURE_CONCRETE)
            .blockstate(() -> (context, generator) -> StandardBlockModels.cubeAll(
                    context,
                    generator,
                    ShadowsAndPetals.asResource("block/raw_concrete/base")))
            .loot((provider, block) -> provider.dropSelf(block.get()))
            .connectedTextures(
                    ShadowsAndPetals.asResource("block/raw_concrete/base"),
                    List.of(
                            ShadowsAndPetals.asResource("block/raw_concrete/connected_bleed"),
                            ShadowsAndPetals.asResource("block/raw_concrete/connected_hole_bleed"),
                            ShadowsAndPetals.asResource("block/raw_concrete/connected_dense_hole_bleed")),
                    RawConcreteBlock::selectTextureIndex,
                    CTTextureType.OMNIDIRECTIONAL, 1)
            .recipe(BlockRecipeData::rawConcrete)
            .lang(DatagenLangRegistry.ZH_CN, "清水混凝土")
            .register();

    public static final DeferredBlock<RawConcreteBlock> ISOLATED_RAW_CONCRETE = SAPRegistries
            .block("isolated_raw_concrete", RawConcreteBlock::new)
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .strength(2.5F, 6.0F)
                    .sound(SoundType.STONE)
                    .mapColor(MapColor.CLAY)
                    .requiresCorrectToolForDrops())
            .tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL)
            .withItem()
            .tooltipDescription(BlockTooltipData::isolatedRawConcrete)
            .creativeTab(CreativeTabRegistry.CreativeTabKey.ARCHITECTURE, CreativeTabOrder.ARCHITECTURE_ISOLATED_CONCRETE)
            .blockstate(() -> (context, generator) -> StandardBlockModels.cubeAllWithItemTexture(
                    context,
                    generator,
                    ShadowsAndPetals.asResource("block/raw_concrete/base"),
                    ShadowsAndPetals.asResource("item/isolated_raw_concrete")))
            .loot((provider, block) -> provider.dropSelf(block.get()))
            .connectedTextures(
                    ShadowsAndPetals.asResource("block/raw_concrete/base"),
                    List.of(
                            ShadowsAndPetals.asResource("block/raw_concrete/connected_bleed"),
                            ShadowsAndPetals.asResource("block/raw_concrete/connected_hole_bleed"),
                            ShadowsAndPetals.asResource("block/raw_concrete/connected_dense_hole_bleed")),
                    RawConcreteBlock::selectTextureIndex,
                    CTTextureType.OMNIDIRECTIONAL, 1)
            .recipe(BlockRecipeData::isolatedRawConcrete)
            .lang(DatagenLangRegistry.ZH_CN, "隔离清水混凝土")
            .register();

    public static final DeferredBlock<IngotPileBlock> ALUMINUM_INGOT_PILE = ChinjufuBlockCompat
            .ingotPileStateAlias(SAPRegistries.block("aluminum_ingot_pile", IngotPileBlock::new), "block_alumi_block")
            .itemAlias(ChinjufuIds.MOD_ID, "block_alumi_block")
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .strength(2.5F, 6.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .requiresCorrectToolForDrops())
            .tags(BlockTags.MINEABLE_WITH_PICKAXE)
            .withItem()
            .creativeTab(CreativeTabRegistry.CreativeTabKey.FURNISHINGS, CreativeTabOrder.FURNISHINGS_INGOT_PILES)
            .blockstate(() -> DecorationBlockModels::ingotPile)
            .loot((provider, pile) -> provider.dropSlab(pile.get()))
            .recipe(BlockRecipeData::aluminumIngotPile)
            .lang(DatagenLangRegistry.ZH_CN, "铝锭堆")
            .register();

    public static final DeferredBlock<IngotPileBlock> IRON_INGOT_PILE = ChinjufuBlockCompat
            .ingotPileStateAlias(SAPRegistries.block("iron_ingot_pile", IngotPileBlock::new), "block_steel_block")
            .itemAlias(ChinjufuIds.MOD_ID, "block_steel_block")
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .strength(2.5F, 6.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .requiresCorrectToolForDrops())
            .tags(BlockTags.MINEABLE_WITH_PICKAXE)
            .withItem()
            .creativeTab(CreativeTabRegistry.CreativeTabKey.FURNISHINGS, CreativeTabOrder.FURNISHINGS_INGOT_PILES)
            .blockstate(() -> DecorationBlockModels::ingotPile)
            .loot((provider, pile) -> provider.dropSlab(pile.get()))
            .recipe(BlockRecipeData::ironIngotPile)
            .lang(DatagenLangRegistry.ZH_CN, "铁锭堆")
            .register();

    public static final DeferredBlock<IngotPileBlock> COPPER_INGOT_PILE = ChinjufuBlockCompat
            .ingotPileStateAlias(SAPRegistries.block("copper_ingot_pile", IngotPileBlock::new), "block_copper_block")
            .itemAlias(ChinjufuIds.MOD_ID, "block_copper_block")
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .requiresCorrectToolForDrops())
            .tags(BlockTags.MINEABLE_WITH_PICKAXE)
            .withItem()
            .creativeTab(CreativeTabRegistry.CreativeTabKey.FURNISHINGS, CreativeTabOrder.FURNISHINGS_INGOT_PILES)
            .blockstate(() -> DecorationBlockModels::ingotPile)
            .loot((provider, pile) -> provider.dropSlab(pile.get()))
            .recipe(BlockRecipeData::copperIngotPile)
            .lang(DatagenLangRegistry.ZH_CN, "铜锭堆")
            .register();

    public static final DeferredBlock<IngotPileBlock> GOLD_INGOT_PILE = ChinjufuBlockCompat
            .ingotPileStateAlias(SAPRegistries.block("gold_ingot_pile", IngotPileBlock::new), "block_gold_block")
            .itemAlias(ChinjufuIds.MOD_ID, "block_gold_block")
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_BLOCK)
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .requiresCorrectToolForDrops())
            .tags(BlockTags.MINEABLE_WITH_PICKAXE)
            .withItem()
            .creativeTab(CreativeTabRegistry.CreativeTabKey.FURNISHINGS, CreativeTabOrder.FURNISHINGS_INGOT_PILES)
            .blockstate(() -> DecorationBlockModels::ingotPile)
            .loot((provider, pile) -> provider.dropSlab(pile.get()))
            .recipe(BlockRecipeData::goldIngotPile)
            .lang(DatagenLangRegistry.ZH_CN, "金锭堆")
            .register();

    public static final DeferredBlock<IngotPileBlock> NETHERITE_INGOT_PILE = ChinjufuBlockCompat
            .ingotPileStateAlias(SAPRegistries.block("netherite_ingot_pile", IngotPileBlock::new), "block_netherite_block")
            .itemAlias(ChinjufuIds.MOD_ID, "block_netherite_block")
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.NETHERITE_BLOCK)
                    .strength(50.0F, 1200.0F)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .noOcclusion()
                    .requiresCorrectToolForDrops())
            .tags(BlockTags.MINEABLE_WITH_PICKAXE)
            .withItem()
            .creativeTab(CreativeTabRegistry.CreativeTabKey.FURNISHINGS, CreativeTabOrder.FURNISHINGS_INGOT_PILES)
            .blockstate(() -> DecorationBlockModels::ingotPile)
            .loot((provider, pile) -> provider.dropSlab(pile.get()))
            .recipe(BlockRecipeData::netheriteIngotPile)
            .lang(DatagenLangRegistry.ZH_CN, "下界合金锭堆")
            .register();

    public static final DeferredBlock<IroriBlock> IRORI = SAPRegistries
            .block("irori", IroriBlock::new)
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .strength(2.0F, 6.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion())
            .tags(BlockTags.MINEABLE_WITH_PICKAXE)
            .withItem()
            .tooltipDescription(BlockTooltipData::irori)
            .creativeTab(CreativeTabRegistry.CreativeTabKey.FURNISHINGS, CreativeTabOrder.FURNISHINGS_IRORI)
            .blockstate(() -> DecorationBlockModels::irori)
            .loot((provider, irori) -> provider.dropSelf(irori.get()))
            .recipe(BlockRecipeData::irori)
            .lang(DatagenLangRegistry.ZH_CN, "日式围炉")
            .register();

    public static final DeferredBlock<IroriGrillBlock> IRORI_GRILL = SAPRegistries
            .block("irori_grill", IroriGrillBlock::new)
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .strength(2.0F, 6.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .pushReaction(PushReaction.BLOCK))
            .tags(BlockTags.MINEABLE_WITH_PICKAXE)
            .blockstate(() -> DecorationBlockModels::iroriGrill)
            .loot((provider, block) -> provider.addTable(block.get(), provider.noDropTable()))
            .lang(DatagenLangRegistry.ZH_CN, "围炉烤架")
            .register();

    public static final DeferredBlock<IroriGrillCopperTeapotBlock> IRORI_GRILL_COPPER_TEAPOT = SAPRegistries
            .block("irori_grill_copper_teapot", IroriGrillCopperTeapotBlock::new)
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()
                    .pushReaction(PushReaction.BLOCK))
            .tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTagRegistry.OCCUPIES_IRORI_GRILL_SURFACE)
            .blockstate(() -> DecorationBlockModels::iroriGrillCopperTeapot)
            .loot((provider, block) -> provider.dropOther(block.get(), COPPER_TEAPOT.get()))
            .lang(DatagenLangRegistry.DEFAULT_LOCALE, "Copper Teapot")
            .lang(DatagenLangRegistry.ZH_CN, "铜茶壶")
            .register();

    public static final DeferredBlock<BedroomLampBlock> BEDROOM_LAMP = SAPRegistries
            .block("bedroom_lamp", BedroomLampBlock::new)
            .properties(properties -> BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.METAL)
                    .mapColor(MapColor.METAL)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(BedroomLampBlock.LIT) ? 10 : 0))
            .tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_AXE)
            .withItem()
            .tooltipDescription(BlockTooltipData::bedroomLamp)
            .creativeTab(CreativeTabRegistry.CreativeTabKey.FURNISHINGS, CreativeTabOrder.FURNISHINGS_LAMPS)
            .blockstate(() -> DecorationBlockModels::bedroomLamp)
            .loot((provider, lamp) -> provider.dropSelf(lamp.get()))
            .recipe(BlockRecipeData::bedroomLamp)
            .clientItem(ShadowsAndPetals.asResource("block/bedroom_lamp/off"))
            .lang(DatagenLangRegistry.ZH_CN, "卧室台灯")
            .register();

    public static final DeferredBlock<WallLampBlock> WALL_LAMP = SAPRegistries
            .block("wall_lamp", WallLampBlock::new)
            .properties(properties -> BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.METAL)
                    .mapColor(MapColor.METAL)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(WallLampBlock.LIT) ? 10 : 0))
            .tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_AXE)
            .withItem()
            .tooltipDescription(BlockTooltipData::wallLamp)
            .creativeTab(CreativeTabRegistry.CreativeTabKey.FURNISHINGS, CreativeTabOrder.FURNISHINGS_LAMPS)
            .blockstate(() -> DecorationBlockModels::wallLamp)
            .loot((provider, lamp) -> provider.dropSelf(lamp.get()))
            .recipe(BlockRecipeData::wallLamp)
            .clientItem(ShadowsAndPetals.asResource("block/wall_lamp/off"))
            .lang(DatagenLangRegistry.ZH_CN, "壁灯")
            .register();

    public static final DeferredBlock<EmergencyLampBlock> EMERGENCY_LAMP = SAPRegistries
            .block("emergency_lamp", EmergencyLampBlock::new)
            .properties(properties -> BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.METAL)
                    .mapColor(MapColor.METAL)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(EmergencyLampBlock.LIT) ? 10 : 0))
            .withItem()
            .tooltipDescription(BlockTooltipData::emergencyLamp)
            .creativeTab(CreativeTabRegistry.CreativeTabKey.FURNISHINGS, CreativeTabOrder.FURNISHINGS_LAMPS)
            .blockstate(() -> DecorationBlockModels::emergencyLamp)
            .loot((provider, lamp) -> provider.dropSelf(lamp.get()))
            .recipe(BlockRecipeData::emergencyLamp)
            .clientItem(ShadowsAndPetals.asResource("block/emergency_lamp/off"))
            .lang(DatagenLangRegistry.ZH_CN, "防爆灯")
            .register();

    public static final DeferredBlock<RecessedLampBlock> RECESSED_LAMP = SAPRegistries
            .block("recessed_lamp", RecessedLampBlock::new)
            .properties(properties -> BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.METAL)
                    .mapColor(MapColor.METAL)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(RecessedLampBlock.LIT) ? 10 : 0))
            .tags(BlockTags.MINEABLE_WITH_PICKAXE)
            .withCustomItem(RecessedLampBlockItem::new)
            .tooltipDescription(BlockTooltipData::recessedLamp)
            .creativeTab(CreativeTabRegistry.CreativeTabKey.FURNISHINGS, CreativeTabOrder.FURNISHINGS_LAMPS)
            .blockstate(() -> DecorationBlockModels::recessedLamp)
            .loot((provider, lamp) -> provider.dropSelf(lamp.get()))
            .recipe(BlockRecipeData::recessedLamp)
            .clientItem(ShadowsAndPetals.asResource("block/recessed_lamp/up_off"))
            .lang(DatagenLangRegistry.ZH_CN, "嵌灯")
            .register();

    public static final DeferredBlock<RecessedLampCompositeBlock> RECESSED_LAMP_COMPOSITE = SAPRegistries
            .block("recessed_lamp_composite", RecessedLampCompositeBlock::new)
            .properties(properties -> BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.METAL)
                    .mapColor(MapColor.METAL)
                    .dynamicShape()
                    .lightLevel(state -> state.getValue(RecessedLampBlock.LIT) ? 10 : 0))
            .blockstate(() -> DecorationBlockModels::recessedLampComposite)
            .loot((provider, block) -> provider.addTable(block.get(), provider.noDropTable()))
            .lang(DatagenLangRegistry.DEFAULT_LOCALE, "Recessed Lamp")
            .lang(DatagenLangRegistry.ZH_CN, "嵌灯")
            .register();

    public static final DeferredBlock<DeskLampBlock> DESK_LAMP = SAPRegistries
            .block("desk_lamp", DeskLampBlock::new)
            .properties(properties -> BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.METAL)
                    .mapColor(MapColor.METAL)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(DeskLampBlock.LIT) ? 10 : 0))
            .withItem()
            .tooltipDescription(BlockTooltipData::deskLamp)
            .creativeTab(CreativeTabRegistry.CreativeTabKey.FURNISHINGS, CreativeTabOrder.FURNISHINGS_LAMPS)
            .blockstate(() -> DecorationBlockModels::deskLamp)
            .loot((provider, lamp) -> provider.dropSelf(lamp.get()))
            .recipe(BlockRecipeData::deskLamp)
            .clientItem(ShadowsAndPetals.asResource("block/desk_lamp/off"))
            .lang(DatagenLangRegistry.ZH_CN, "台灯")
            .register();

    public static final DeferredBlock<BonsaiBlock> BONSAI = SAPRegistries
            .block("bonsai", BonsaiBlock::new)
            .properties(properties -> BlockBehaviour.Properties.of()
                    .strength(0.5F)
                    .sound(SoundType.STONE)
                    .mapColor(MapColor.STONE)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY))
            .tags(BlockTags.MINEABLE_WITH_PICKAXE)
            .withItem()
            .creativeTab(CreativeTabRegistry.CreativeTabKey.TRADITIONS, CreativeTabOrder.TRADITIONS_BONSAI)
            .tooltipDescription(BlockTooltipData::bonsai)
            .loot((provider, block) -> provider.dropSelf(block.get()))
            .recipe(BlockRecipeData::bonsai)
            .blockstate(() -> BonsaiBlockModels::block)
            .lang(DatagenLangRegistry.ZH_CN, "盆栽")
            .register();

    public static final DeferredBlock<WoodenBarrelBlock> WOODEN_BARREL = SAPRegistries
            .block("wooden_barrel", WoodenBarrelBlock::new)
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .noOcclusion())
            .tags(BlockTags.MINEABLE_WITH_AXE)
            .withCustomItem(WoodenBarrelBlockItem::new)
            .tooltipDescription(BlockTooltipData::woodenBarrel)
            .tooltipModifier(new WoodenBarrelTooltipModifier())
            .creativeTab(CreativeTabRegistry.CreativeTabKey.CUISINE, CreativeTabOrder.CUISINE_CONTAINERS)
            .blockstate(() -> DecorationBlockModels::woodenBarrel)
            .customClientItem(ShadowsAndPetals.asResource("wooden_barrel"))
            .loot((provider, block) -> provider.dropSelf(block.get()))
            .recipe(BlockRecipeData::woodenBarrel)
            .lang(DatagenLangRegistry.ZH_CN, "木桶")
            .register();

    public static final DyedBlockList<RoofTileBlock> ROOF_TILES = new DyedBlockList<>(color -> ChinjufuBlockCompat
            .roofTileStateAlias(
                    SAPRegistries.block(color.getName() + "_roof_tile", RoofTileBlock::new),
                    ChinjufuIds.dyedBlockAlias(color, "block_kawara")
            )
            .itemAlias(ChinjufuIds.MOD_ID, ChinjufuIds.dyedBlockAlias(color, "block_kawara"))
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)
                    .strength(1.5F, 6.0F)
                    .sound(SoundType.DEEPSLATE_TILES)
                    .mapColor(color)
                    .requiresCorrectToolForDrops())
            .tags(BlockTags.MINEABLE_WITH_PICKAXE)
            .withItem()
            .creativeTab(CreativeTabRegistry.CreativeTabKey.ARCHITECTURE, CreativeTabOrder.ARCHITECTURE_ROOF_TILES)
            .blockstate(() -> (context, generator) -> RoofTileModels.base(
                    context,
                    generator,
                    generator.modLoc("block/roof_tile/" + color.getName())
            ))
            .loot((provider, block) -> provider.dropSelf(block.get()))
            .recipe((provider, block) -> BlockRecipeData.roofTile(provider, block, color))
            .lang(DatagenLangRegistry.ZH_CN, DyedBlockList.zhName(color) + "瓦")
            .register());

    public static final DyedBlockList<RoofTileSlabBlock> ROOF_TILE_SLABS = new DyedBlockList<>(color -> SAPRegistries
            .block(color.getName() + "_roof_tile_slab", RoofTileSlabBlock::new)
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_SLAB)
                    .strength(1.5F, 6.0F)
                    .sound(SoundType.DEEPSLATE_TILES)
                    .mapColor(color)
                    .requiresCorrectToolForDrops())
            .tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.SLABS)
            .withItem()
            .creativeTab(CreativeTabRegistry.CreativeTabKey.ARCHITECTURE, CreativeTabOrder.ARCHITECTURE_ROOF_TILES)
            .blockstate(() -> (context, generator) -> RoofTileModels.shapes(context, generator, color))
            .loot((provider, block) -> provider.dropSlab(block.get()))
            .recipe((provider, block) -> BlockRecipeData.roofTileSlab(provider, block, color))
            .lang(DatagenLangRegistry.ZH_CN, DyedBlockList.zhName(color) + "瓦台阶")
            .register());

    public static final DyedBlockList<RoofTileVerticalSlabBlock> ROOF_TILE_VERTICAL_SLABS = new DyedBlockList<>(color -> SAPRegistries
            .block(color.getName() + "_roof_tile_vertical_slab", RoofTileVerticalSlabBlock::new)
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_SLAB)
                    .strength(1.5F, 6.0F)
                    .sound(SoundType.DEEPSLATE_TILES)
                    .mapColor(color)
                    .requiresCorrectToolForDrops())
            .tags(BlockTags.MINEABLE_WITH_PICKAXE)
            .withItem()
            .creativeTab(CreativeTabRegistry.CreativeTabKey.ARCHITECTURE, CreativeTabOrder.ARCHITECTURE_ROOF_TILES)
            .clientItem(block -> ShadowsAndPetals.asResource("block/" + block.getId().getPath()))
            .loot((provider, block) -> provider.dropSlab(block.get()))
            .recipe((provider, block) -> BlockRecipeData.roofTileVerticalSlab(provider, block, color))
            .lang(DatagenLangRegistry.ZH_CN, "竖直" + DyedBlockList.zhName(color) + "瓦台阶")
            .register());

    public static final DyedBlockList<StairBlock> ROOF_TILE_STAIRS = new DyedBlockList<>(color -> SAPRegistries
            .block(color.getName() + "_roof_tile_stairs", properties -> new StairBlock(ROOF_TILES.get(color).get().defaultBlockState(), properties))
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICK_STAIRS)
                    .strength(1.5F, 6.0F)
                    .sound(SoundType.DEEPSLATE_TILES)
                    .mapColor(color)
                    .requiresCorrectToolForDrops())
            .tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.STAIRS)
            .withItem()
            .creativeTab(CreativeTabRegistry.CreativeTabKey.ARCHITECTURE, CreativeTabOrder.ARCHITECTURE_ROOF_TILES)
            .loot((provider, block) -> provider.dropSelf(block.get()))
            .recipe((provider, block) -> BlockRecipeData.roofTileStairs(provider, block, color))
            .lang(DatagenLangRegistry.ZH_CN, DyedBlockList.zhName(color) + "瓦楼梯")
            .register());

    public static final WoodBlockList<VanityBlock> VANITIES = new WoodBlockList<>(woodType -> ChinjufuBlockCompat
            .vanityStateAlias(
                    SAPRegistries.block(woodType.getName() + "_vanity", VanityBlock::new),
                    woodType
            )
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(woodType.getPlanks())
                    .strength(2.5F, 3.0F)
                    .sound(SoundType.WOOD)
                    .noOcclusion())
            .tags(BlockTags.MINEABLE_WITH_AXE)
            .withItem()
            .tooltipDescription(BlockTooltipData::vanity)
            .creativeTab(CreativeTabRegistry.CreativeTabKey.FURNISHINGS, CreativeTabOrder.FURNISHINGS_VANITIES)
            .blockstate(() -> DecorationBlockModels::vanity)
            .clientItem(ShadowsAndPetals.asResource("item/vanity/" + woodType.getName()))
            .recipe((provider, vanity) -> BlockRecipeData.vanity(provider, vanity, woodType))
            .lang(DatagenLangRegistry.ZH_CN, woodType.getZhName() + "梳妆台")
            .register());

    public static final DeferredBlock<WindowPaneBlock> RED_LACQUERED_WINDOW_PANE = SAPRegistries
            .block("red_lacquered_window_pane", WindowPaneBlock::new)
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                    .sound(SoundType.WOOD)
                    .mapColor(DyeColor.RED)
                    .noOcclusion())
            .tags(BlockTags.MINEABLE_WITH_AXE)
            .withItem()
            .creativeTab(CreativeTabRegistry.CreativeTabKey.ARCHITECTURE, CreativeTabOrder.ARCHITECTURE_WINDOW_PANES)
            .blockstate(() -> WindowPaneModels::redLacquered)
            .loot((provider, block) -> provider.dropSelf(block.get()))
            .lang(DatagenLangRegistry.ZH_CN, "红漆窗格")
            .register();

    public static final WoodBlockList<WindowPaneBlock> WINDOW_PANES = new WoodBlockList<>(woodType ->
            registerWindowPane(
                    woodType.getName() + "_window_pane",
                    woodType,
                    woodType.getName(),
                    woodType.getZhName() + "窗格"
            ));

    public static final WoodBlockList<CasementWindowBlock> CASEMENT_WINDOWS =
            new WoodBlockList<>(woodType -> SAPRegistries
                            .block(woodType.getName() + "_casement_window", CasementWindowBlock::new)
                            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(woodType.getPlanks())
                                    .sound(SoundType.WOOD)
                                    .noOcclusion())
                            .tags(BlockTags.MINEABLE_WITH_AXE)
                            .withItem()
                            .creativeTab(CreativeTabRegistry.CreativeTabKey.ARCHITECTURE, CreativeTabOrder.ARCHITECTURE_WINDOWS)
                            .blockstate(() -> CasementWindowModels::block)
                            .recipe((provider, block) -> BlockRecipeData.casementWindow(provider, block, woodType))
                            .loot((provider, block) -> provider.dropSelf(block.get()))
                            .lang(DatagenLangRegistry.ZH_CN, woodType.getZhName() + "平开窗")
                            .register());

    public static final DeferredBlock<WoodPillarBlock> RED_LACQUERED_WOOD_PILLAR = SAPRegistries
            .block("red_lacquered_wood_pillar", WoodPillarBlock::new)
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .mapColor(DyeColor.RED))
            .tags(BlockTags.MINEABLE_WITH_AXE)
            .withItem()
            .creativeTab(CreativeTabRegistry.CreativeTabKey.ARCHITECTURE, CreativeTabOrder.ARCHITECTURE_PILLARS)
            .blockstate(() -> (context, generator) -> AxisAlignedPillarBlockModels.withItem(
                    context,
                    generator,
                    ShadowsAndPetals.asResource("block/wood_pillar/red_lacquered_wood_pillar")
            ))
            .loot((provider, block) -> provider.dropSelf(block.get()))
            .recipe(BlockRecipeData::redLacqueredWoodPillar)
            .lang(DatagenLangRegistry.ZH_CN, "红漆木圆柱")
            .register();

    public static final WoodBlockList<WoodPillarBlock> STRIPPED_WOOD_PILLARS = new WoodBlockList<>(woodType -> SAPRegistries
            .block("stripped_" + woodType.getName() + "_wood_pillar", WoodPillarBlock::new)
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(woodType.getStrippedLog())
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD))
            .tags(BlockTags.MINEABLE_WITH_AXE)
            .withItem()
            .creativeTab(CreativeTabRegistry.CreativeTabKey.ARCHITECTURE, CreativeTabOrder.ARCHITECTURE_PILLARS)
            .blockstate(() -> (context, generator) -> WoodPillarBlockModels.strippedWoodPillar(
                    context,
                    generator,
                    woodType
            ))
            .loot((provider, block) -> provider.dropSelf(block.get()))
            .lang(DatagenLangRegistry.ZH_CN, "去皮" + woodType.getZhName() + "木圆柱")
            .register());

//    public static final WoodBlockList<ModularDeskBlock> MODULAR_DESKS = new WoodBlockList<>(woodType -> SAPRegistries.
//            block(woodType.getName() + "_modular_desk", ModularDeskBlock::new)
//            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
//                    .strength(2.0F, 3.0F)
//                    .sound(SoundType.WOOD)
//                    .noOcclusion())
//            .withItem()
//            .creativeTab(CreativeTabType.MAIN)
//            .lang(DatagenLangRegistry.ZH_CN, woodType.getZhName() + "书桌")
//            .recipe((provider, desk) -> provider.shaped(RecipeCategory.DECORATIONS, desk.get())
//                    .define('W', woodType.getSlab())
//                    .pattern("WWW")
//                    .pattern("W W")
//                    .pattern("W W")
//                    .unlockedBy(provider.hasName(woodType.getPlanks()), provider.hasItem(woodType.getPlanks()))
//                    .save(provider.output()))
//            .register());
//
//    public static final WoodBlockList<CafeTableBlock> CAFE_TABLES = new WoodBlockList<>(woodType -> SAPRegistries.
//            block(woodType.getName() + "_cafe_table", CafeTableBlock::new)
//            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
//                    .strength(2.0F, 3.0F)
//                    .sound(SoundType.WOOD)
//                    .noOcclusion())
//            .withItem()
//            .creativeTab(CreativeTabType.MAIN)
//            .lang(DatagenLangRegistry.ZH_CN, woodType.getZhName() + "咖啡桌")
//            .recipe((provider, desk) -> provider.shaped(RecipeCategory.DECORATIONS, desk.get())
//                    .define('W', woodType.getSlab())
//                    .define('S', Items.STICK)
//                    .pattern("WWW")
//                    .pattern(" S ")
//                    .pattern("SSS")
//                    .unlockedBy(provider.hasName(woodType.getPlanks()), provider.hasItem(woodType.getPlanks()))
//                    .save(provider.output()))
//            .register());
//
//    public static final WoodBlockList<DiningChairBlock> DINING_CHAIRS = new WoodBlockList<>(woodType -> SAPRegistries
//            .block(woodType.getName() + "_dining_chair", DiningChairBlock::new)
//            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
//                    .strength(2.0F, 3.0F)
//                    .sound(SoundType.WOOD)
//                    .noOcclusion())
//            .withItem()
//            .creativeTab(CreativeTabType.MAIN)
//            .lang(DatagenLangRegistry.ZH_CN, woodType.getZhName() + "餐椅")
//            .recipe((provider, chair) -> provider.shaped(RecipeCategory.DECORATIONS, chair.get())
//                    .define('W', woodType.getSlab())
//                    .define('S', Items.STICK)
//                    .pattern("W  ")
//                    .pattern("WWW")
//                    .pattern("S S")
//                    .unlockedBy(provider.hasName(woodType.getPlanks()), provider.hasItem(woodType.getPlanks()))
//                    .save(provider.output()))
//            .register());

    public static final DyedBlockList<CafeChairBlock> CAFE_CHAIRS = new DyedBlockList<>(color -> SAPRegistries
            .block(color.getName() + "_cafe_chair", CafeChairBlock::new)
            .alias(ChinjufuIds.MOD_ID, ChinjufuIds.dyedBlockAlias(color, "block_cafechair"))
            .itemAlias(ChinjufuIds.MOD_ID, ChinjufuIds.dyedBlockAlias(color, "block_cafechair"))
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .mapColor(color)
                    .noOcclusion())
            .withItem()
            .tooltipDescription(BlockTooltipData::cafeChair)
            .creativeTab(CreativeTabRegistry.CreativeTabKey.FURNISHINGS, CreativeTabOrder.FURNISHINGS_CHAIRS)
            .lang(DatagenLangRegistry.ZH_CN, DyedBlockList.zhName(color) + "咖啡椅")
            .blockstate(() -> (context, generator) -> StandardBlockModels.simpleBlockWithItem(
                    context,
                    generator,
                    generator.modLoc("block/cafe_chair/" + color.getName())
            ))
            .recipe((provider, chair) -> BlockRecipeData.cafeChair(provider, chair, color))
            .register());

    // Add another colour when its block/sofa/<colour>.json model family exists.
    public static final DeferredBlock<SofaBlock> BROWN_SOFA = registerSofa(DyeColor.BROWN);

    public static final DeferredBlock<SamonBlock> SAMON = SAPRegistries
            .block("samon", SamonBlock::new)
            .properties(properties -> BlockBehaviour.Properties.of()
                    .strength(0.7F)
                    .sound(SoundType.STONE)
                    .mapColor(MapColor.SAND))
            .tags(BlockTags.MINEABLE_WITH_PICKAXE)
            .withItem()
            .blockstate(() -> DecorationBlockModels::samon)
            .loot((provider, block) -> provider.dropSelf(block.get()))
            .lang(DatagenLangRegistry.ZH_CN, "砂纹")
            .register();

    public static final DeferredBlock<SandExcavationBlock> SAND_EXCAVATION = SAPRegistries
            .block("sand_excavation", SandExcavationBlock::new)
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.SAND)
                    .strength(0.5F)
                    .sound(SoundType.SAND))
            .tags(BlockTags.MINEABLE_WITH_SHOVEL)
            .blockstate(() -> NatureBlockModels::sandExcavation)
            .loot((provider, block) -> provider.addTable(
                    block.get(),
                    LootTable.lootTable().withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .add(LootItem.lootTableItem(Items.SAND)))
            ))
            .lang(DatagenLangRegistry.ZH_CN, "挖掘中的沙子")
            .register();

    public static final DeferredBlock<ShishiOdoshiBlock> SHISHI_ODOSHI = SAPRegistries
            .block("shishi_odoshi", ShishiOdoshiBlock::new)
            .properties(properties -> BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.BAMBOO)
                    .mapColor(MapColor.STONE)
                    .noOcclusion())
            .tags(BlockTags.MINEABLE_WITH_AXE)
            .withItem()
            .tooltipDescription(BlockTooltipData::shishiOdoshi)
            .creativeTab(CreativeTabRegistry.CreativeTabKey.TRADITIONS, CreativeTabOrder.TRADITIONS_WATER_FEATURES)
            .blockstate(() -> DecorationBlockModels::shishiOdoshi)
            .loot((provider, block) -> provider.dropSelf(block.get()))
            .recipe(BlockRecipeData::shishiOdoshi)
            .lang(DatagenLangRegistry.ZH_CN, "添水")
            .register();

    public static final DeferredBlock<ShishiOdoshiPipeBlock> SHISHI_ODOSHI_PIPE = SAPRegistries
            .block("shishi_odoshi_pipe", ShishiOdoshiPipeBlock::new)
            .properties(properties -> BlockBehaviour.Properties.of()
                    .strength(1.0F, 1.5F)
                    .sound(SoundType.BAMBOO)
                    .mapColor(MapColor.PLANT)
                    .noOcclusion())
            .tags(BlockTags.MINEABLE_WITH_AXE)
            .withItem()
            .tooltipDescription(BlockTooltipData::shishiOdoshiPipe)
            .creativeTab(CreativeTabRegistry.CreativeTabKey.TRADITIONS, CreativeTabOrder.TRADITIONS_WATER_FEATURES)
            .blockstate(() -> DecorationBlockModels::shishiOdoshiPipe)
            .loot((provider, block) -> provider.dropSelf(block.get()))
            .recipe(BlockRecipeData::shishiOdoshiPipe)
            .lang(DatagenLangRegistry.ZH_CN, "添水竹管")
            .register();

    public static final DyedBlockList<CurtainBlock> CURTAINS = new DyedBlockList<>(color -> SAPRegistries
                .block(color.getName() + "_curtain", CurtainBlock::new)
                .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL)
                        .strength(1.0F)
                        .sound(SoundType.WOOL)
                        .mapColor(color)
                        .noOcclusion())
                .tags(BlockTags.WOOL, BlockTags.MINEABLE_WITH_AXE)
                .withItem()
                .creativeTab(CreativeTabRegistry.CreativeTabKey.FURNISHINGS, CreativeTabOrder.FURNISHINGS_CURTAINS)
                .blockstate(() -> CurtainModels::block)
                .clientItem(block -> ShadowsAndPetals.asResource(
                        "block/curtain/item/" + color.getName()))
                .loot((provider, block) -> provider.dropSelf(block.get()))
                .lang(DatagenLangRegistry.ZH_CN, DyedBlockList.zhName(color) + "窗帘")
                .register()
    );

    public static final DyedBlockList<LongCurtainBlock> LONG_CURTAINS = new DyedBlockList<>(color -> SAPRegistries
                .block(color.getName() + "_long_curtain", LongCurtainBlock::new)
                .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL)
                        .strength(1.0F)
                        .sound(SoundType.WOOL)
                        .mapColor(color)
                        .noOcclusion())
                .tags(BlockTags.WOOL, BlockTags.MINEABLE_WITH_AXE)
                .withItem()
                .creativeTab(CreativeTabRegistry.CreativeTabKey.FURNISHINGS, CreativeTabOrder.FURNISHINGS_CURTAINS)
                .blockstate(() -> LongCurtainModels::block)
                .clientItem(block -> ShadowsAndPetals.asResource("block/long_curtain/item/"
                        + color.getName()))
                .loot((provider, block) -> provider.dropSelfLowerHalfOnly(block.get()))
                .lang(DatagenLangRegistry.ZH_CN, DyedBlockList.zhName(color) + "长窗帘")
                .register()
    );

    public static final DyedBlockList<LargeCurtainBlock> LARGE_CURTAINS = new DyedBlockList<>(color -> SAPRegistries
                .block(color.getName() + "_large_curtain", LargeCurtainBlock::new)
                .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL)
                        .strength(1.0F)
                        .sound(SoundType.WOOL)
                        .mapColor(color)
                        .noOcclusion())
                .tags(BlockTags.WOOL, BlockTags.MINEABLE_WITH_AXE)
                .withItem()
                .creativeTab(CreativeTabRegistry.CreativeTabKey.FURNISHINGS, CreativeTabOrder.FURNISHINGS_CURTAINS)
                .blockstate(() -> LargeCurtainModels::block)
                .clientItem(block -> ShadowsAndPetals.asResource("block/large_curtain/item/"
                        + color.getName()))
                .loot((provider, block) -> provider.dropSelfAnchorOnly(block.get(), LargeCurtainBlock.COLUMN))
                .lang(DatagenLangRegistry.ZH_CN, DyedBlockList.zhName(color) + "大窗帘")
                .register()
    );

    public static final DeferredBlock<OrangeTreeBlock> ORANGE_TREE = ChinjufuBlockCompat
            .orangeTreeStateAlias(SAPRegistries.block("orange_tree", OrangeTreeBlock::new))
            .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH)
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .noOcclusion()
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY))
            .blockstate(() -> NatureBlockModels::orangeTree)
            .loot((provider, tree) -> provider.addTable(
                    tree.get(),
                    LootTable.lootTable().withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .add(LootItem.lootTableItem(ItemRegistry.ORANGE_SEED.get())))
            ))
            .lang(DatagenLangRegistry.ZH_CN, "蜜柑树")
            .register();

    public static final DeferredBlock<RockeryBlock> ROCKERY_1x1x1 = registerRockery(1, 1, 1);
    public static final DeferredBlock<RockeryBlock> ROCKERY_1x1x2 = registerRockery(1, 1, 2);
    public static final DeferredBlock<RockeryBlock> ROCKERY_1x2x1 = registerRockery(1, 2, 1);
    public static final DeferredBlock<RockeryBlock> ROCKERY_1x2x2 = registerRockery(1, 2, 2);
    public static final DeferredBlock<RockeryBlock> ROCKERY_1x3x1 = registerRockery(1, 3, 1);

    private static DeferredBlock<RockeryBlock> registerRockery(int w, int h, int d) {
        RockeryDimensions dims = new RockeryDimensions(w, h, d);
        DeferredBlock<RockeryBlock> result = SAPRegistries
                .block("rockery_" + w + "_" + h + "_" + d,
                        props -> new RockeryBlock(dims, props))
                .properties(p -> BlockBehaviour.Properties.of()
                        .strength(1.5F, 6.0F)
                        .sound(SoundType.STONE)
                        .mapColor(MapColor.STONE)
                        .noOcclusion()
                        .requiresCorrectToolForDrops())
                .withItem()
                .tooltipComponent(
                        (rockery, stack) -> new RockeryTooltipComponent(rockery, dims),
                        100)
                .creativeTab(CreativeTabRegistry.CreativeTabKey.TRADITIONS, CreativeTabOrder.TRADITIONS_ROCKERIES)
                .tags(BlockTags.MINEABLE_WITH_PICKAXE)
                .blockstate(() -> (context, generator) -> NatureBlockModels.rockery(context, generator, dims))
                .clientItem(ShadowsAndPetals.asResource("block/rock/1x1x1/0_0_0"))
                .loot((provider, block) -> provider.addTable(block.get(), provider.noDropTable()))
                .lang(DatagenLangRegistry.DEFAULT_LOCALE, "rockery")
                .lang(DatagenLangRegistry.ZH_CN, "石山")
                .register();

        HammerItem.registerRockery(result, dims);
        return result;
    }

    private static DeferredBlock<WindowPaneBlock> registerWindowPane(
            String id,
            WoodBlockList.WoodType woodType,
            String modelName,
            String zhName
    ) {
        return SAPRegistries
                .block(id, WindowPaneBlock::new)
                .properties(properties -> BlockBehaviour.Properties.ofFullCopy(woodType.getPlanks())
                        .sound(SoundType.WOOD)
                        .noOcclusion())
                .tags(BlockTags.MINEABLE_WITH_AXE)
                .withItem()
                .creativeTab(CreativeTabRegistry.CreativeTabKey.ARCHITECTURE, CreativeTabOrder.ARCHITECTURE_WINDOW_PANES)
                .blockstate(() -> (context, generator) -> WindowPaneModels.block(
                        context,
                        generator,
                        modelName,
                        woodType.getPlanks()
                ))
                .loot((provider, block) -> provider.dropSelf(block.get()))
                .lang(DatagenLangRegistry.ZH_CN, zhName)
                .register();
    }

    private static DeferredBlock<SofaBlock> registerSofa(DyeColor color) {
        return SAPRegistries
                .block(color.getName() + "_sofa", SofaBlock::new)
                .properties(properties -> BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL)
                        .strength(1.0F)
                        .sound(SoundType.WOOL)
                        .mapColor(color)
                        .noOcclusion())
                .tags(BlockTags.MINEABLE_WITH_AXE)
                .withItem()
                .creativeTab(CreativeTabRegistry.CreativeTabKey.FURNISHINGS, CreativeTabOrder.FURNISHINGS_SOFAS)
                .blockstate(() -> SofaModels::block)
                .recipe((provider, sofa) -> BlockRecipeData.sofa(provider, sofa, color))
                .loot((provider, block) -> provider.dropSelf(block.get()))
                .lang(DatagenLangRegistry.ZH_CN, DyedBlockList.zhName(color) + "沙发")
                .register();
    }

    static {
        ChinjufuBlockCompat.registerDirectAliases(
                AUTUMN_OAK_SAPLING,
                AUTUMN_OAK_LEAVES,
                AUTUMN_OAK_LEAVES_CARPET,
                SAKURA_SET,
                MAPLE_SET,
                GINKGO_SET
        );
    }

    public static void init() {}
}
