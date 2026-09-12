package com.sshakusora.shadowsandpetals.registries;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.data.ModBlockTagProvider;
import com.sshakusora.shadowsandpetals.registries.builder.RegBlockBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.*;

/**
 * Global registry that collects block → tag mappings declared via {@link RegBlockBuilder}
 * during block registration. These mappings are later consumed by
 * {@link ModBlockTagProvider} for automatic datagen.
 */
public final class BlockTagRegistry {
    public static final TagKey<Block> OCCUPIES_IRORI_GRILL_SURFACE = create("occupies_irori_grill_surface");
    public static final TagKey<Block> WOOD_POST_HANGING_CONNECTIONS = create("wood_post_hanging_connections");

    private static final Map<TagKey<Block>, List<DeferredBlock<? extends Block>>> TAG_MAP = new HashMap<>();
    private static final Map<TagKey<Block>, List<Block>> DIRECT_BLOCK_MAP = new HashMap<>();
    private static final Map<TagKey<Block>, List<TagKey<Block>>> INCLUDED_TAG_MAP = new HashMap<>();

    static {
        addDefaultIncludedTags();
    }

    private BlockTagRegistry() {
    }

    private static TagKey<Block> create(String path) {
        return TagKey.create(Registries.BLOCK, ShadowsAndPetals.asResource(path));
    }

    private static void addDefaultIncludedTags() {
        // 1.21.1 has no vanilla minecraft:lanterns block tag. Add the two
        // vanilla lantern blocks directly so hanging connections retain the
        // 26.1.2 behavior without referencing a missing tag.
        add(WOOD_POST_HANGING_CONNECTIONS, Blocks.LANTERN);
        add(WOOD_POST_HANGING_CONNECTIONS, Blocks.SOUL_LANTERN);
        include(WOOD_POST_HANGING_CONNECTIONS, BlockTags.CEILING_HANGING_SIGNS);
    }

    public static void add(TagKey<Block> tag, DeferredBlock<? extends Block> block) {
        TAG_MAP.computeIfAbsent(tag, k -> new ArrayList<>()).add(block);
    }

    public static void add(TagKey<Block> tag, Block block) {
        DIRECT_BLOCK_MAP.computeIfAbsent(tag, k -> new ArrayList<>()).add(block);
    }

    public static Map<TagKey<Block>, List<DeferredBlock<? extends Block>>> getAll() {
        return Collections.unmodifiableMap(TAG_MAP);
    }

    public static Map<TagKey<Block>, List<Block>> getAllDirectBlocks() {
        return Collections.unmodifiableMap(DIRECT_BLOCK_MAP);
    }

    public static void include(TagKey<Block> tag, TagKey<Block> includedTag) {
        INCLUDED_TAG_MAP.computeIfAbsent(tag, k -> new ArrayList<>()).add(includedTag);
    }

    public static Map<TagKey<Block>, List<TagKey<Block>>> getAllIncludedTags() {
        return Collections.unmodifiableMap(INCLUDED_TAG_MAP);
    }

    public static void clear() {
        TAG_MAP.clear();
        DIRECT_BLOCK_MAP.clear();
        INCLUDED_TAG_MAP.clear();
        addDefaultIncludedTags();
    }
}