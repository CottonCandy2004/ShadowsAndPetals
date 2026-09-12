package com.sshakusora.shadowsandpetals.compat.jei;

import com.sshakusora.shadowsandpetals.block.RockeryDimensions;
import com.sshakusora.shadowsandpetals.block.nature.RockeryBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

/** Informational JEI recipe data for a hammer-carved rockery shape. */
public record RockeryInfoRecipe(RockeryBlock block, RockeryDimensions dimensions) {
    public ItemStack inputStack() {
        return new ItemStack(Blocks.STONE, dimensions.partCount());
    }

    public ItemStack outputStack() {
        return new ItemStack(block);
    }
}
