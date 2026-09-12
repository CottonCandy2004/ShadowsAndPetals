package com.sshakusora.shadowsandpetals.compat.jade;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.data.BuiltinLanguageKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;

public final class SandExcavationCooldownComponentProvider implements IBlockComponentProvider {
    private static final ResourceLocation UID = ShadowsAndPetals.asResource("jade.sand_excavation_cooldown");
    public static final SandExcavationCooldownComponentProvider INSTANCE = new SandExcavationCooldownComponentProvider();

    private SandExcavationCooldownComponentProvider() {
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!accessor.getBlockState().is(Blocks.SAND)) {
            return;
        }

        long endTick = accessor.getServerData()
                .getLong(SandExcavationCooldownServerDataProvider.COOLDOWN_END_TICK_KEY);
        long duration = accessor.getServerData()
                .getLong(SandExcavationCooldownServerDataProvider.COOLDOWN_DURATION_KEY);
        long remaining = endTick - accessor.getLevel().getGameTime();
        if (remaining <= 0L || duration <= 0L) {
            return;
        }

        tooltip.add(Component.translatable(BuiltinLanguageKeys.JADE_SAND_EXCAVATION_COOLDOWN.key())
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(IElementHelper.get().progress(
                Math.clamp(remaining / (float) duration, 0.0F, 1.0F),
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
