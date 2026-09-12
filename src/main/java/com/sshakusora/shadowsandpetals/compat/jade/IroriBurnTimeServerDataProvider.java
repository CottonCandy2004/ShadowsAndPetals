package com.sshakusora.shadowsandpetals.compat.jade;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.blockentity.irori.IroriBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public final class IroriBurnTimeServerDataProvider implements IServerDataProvider<BlockAccessor> {
    public static final IroriBurnTimeServerDataProvider INSTANCE = new IroriBurnTimeServerDataProvider();

    private IroriBurnTimeServerDataProvider() {
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        BlockEntity blockEntity = accessor.getBlockEntity();
        if (!(blockEntity instanceof IroriBlockEntity irori)
                || irori.getBurnTime() <= 0
                || irori.getBurnTimeTotal() <= 0) {
            return;
        }

        data.putInt(IroriBurnTimeComponentProvider.BURN_TIME_KEY, irori.getBurnTime());
        data.putInt(IroriBurnTimeComponentProvider.BURN_TIME_TOTAL_KEY, irori.getBurnTimeTotal());
        data.putInt(IroriBurnTimeComponentProvider.BURN_CYCLE_KEY, irori.getBurnCycle());
    }

    @Override
    public ResourceLocation getUid() {
        return ShadowsAndPetals.asResource("jade.irori_burn_time");
    }
}
