package com.sshakusora.shadowsandpetals.compat.jade;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.blockentity.irori.IroriBlockEntity;
import com.sshakusora.shadowsandpetals.data.BuiltinLanguageKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;

public final class IroriBurnTimeComponentProvider implements IBlockComponentProvider {
    static final String BURN_TIME_KEY = "IroriBurnTime";
    static final String BURN_TIME_TOTAL_KEY = "IroriBurnTimeTotal";
    static final String BURN_CYCLE_KEY = "IroriBurnCycle";
    private static final ResourceLocation UID = ShadowsAndPetals.asResource("jade.irori_burn_time");

    public static final IroriBurnTimeComponentProvider INSTANCE = new IroriBurnTimeComponentProvider();

    private IroriBurnTimeComponentProvider() {
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockEntity blockEntity = accessor.getBlockEntity();
        if (!(blockEntity instanceof IroriBlockEntity irori)) {
            return;
        }

        CompoundTag data = accessor.getServerData();
        int burnTime = data.contains(BURN_TIME_KEY) ? data.getInt(BURN_TIME_KEY) : irori.getBurnTime();
        int burnTimeTotal = data.contains(BURN_TIME_TOTAL_KEY)
                ? data.getInt(BURN_TIME_TOTAL_KEY)
                : irori.getBurnTimeTotal();
        if (burnTime <= 0 || burnTimeTotal <= 0) {
            if (irori.hasAsh()) {
                tooltip.add(Component.translatable(BuiltinLanguageKeys.JADE_IRORI_BURNED_OUT.key())
                        .withStyle(ChatFormatting.GRAY));
            }
            return;
        }

        float progress = Math.clamp(burnTime / (float) burnTimeTotal, 0.0F, 1.0F);
        tooltip.add(Component.translatable(BuiltinLanguageKeys.JADE_IRORI_BURNING.key())
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(IElementHelper.get().progress(
                progress,
                Component.empty(),
                IElementHelper.get().progressStyle().color(0xFFE0E0E0),
                BoxStyle.getNestedBox(),
                true
        ));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
