package com.sshakusora.shadowsandpetals.compat.jade;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.item.hammer.HammerItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public final class RockeryHammerProgressServerDataProvider implements IServerDataProvider<BlockAccessor> {
    static final String PROGRESS_KEY = "RockeryHammerProgress";
    static final String DURATION_KEY = "RockeryHammerDuration";
    public static final RockeryHammerProgressServerDataProvider INSTANCE = new RockeryHammerProgressServerDataProvider();

    private RockeryHammerProgressServerDataProvider() {
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getPlayer().getMainHandItem().getItem() instanceof HammerItem)
                || !accessor.getPlayer().isUsingItem()) {
            return;
        }

        float progress = HammerItem.getHammeringProgress(
                accessor.getPlayer(),
                accessor.getLevel(),
                accessor.getPosition()
        );
        if (progress < 0.0F) {
            return;
        }
        data.putInt(PROGRESS_KEY, Math.round(progress * 100.0F));
        data.putFloat(DURATION_KEY, HammerItem.getEffectiveUseDuration(accessor.getPlayer().getMainHandItem()));
    }

    @Override
    public boolean shouldRequestData(BlockAccessor accessor) {
        return accessor.getPlayer().getMainHandItem().getItem() instanceof HammerItem
                && accessor.getPlayer().isUsingItem();
    }

    @Override
    public ResourceLocation getUid() {
        return ShadowsAndPetals.asResource("jade.rockery_hammer_progress");
    }
}
