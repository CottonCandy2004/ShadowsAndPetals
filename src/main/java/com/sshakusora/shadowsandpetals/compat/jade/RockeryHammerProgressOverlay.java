package com.sshakusora.shadowsandpetals.compat.jade;

import com.sshakusora.shadowsandpetals.item.hammer.HammerItem;
import net.minecraft.util.Mth;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.api.ui.IBoxElement;
import snownee.jade.api.ui.MessageType;

/** Feeds hammer progress into Jade's native tooltip progress strip. */
public final class RockeryHammerProgressOverlay {
    private RockeryHammerProgressOverlay() {
    }

    public static void onTooltipCollected(IBoxElement root, Accessor<?> accessor) {
        if (!(accessor instanceof BlockAccessor blockAccessor)
                || !IWailaConfig.get().getPlugin().get(JadeIds.MC_BREAKING_PROGRESS)
                || !(accessor.getPlayer().getMainHandItem().getItem() instanceof HammerItem)
                || !accessor.getPlayer().isUsingItem()) {
            root.clearBoxProgress();
            return;
        }

        int progressPercent = blockAccessor.getServerData()
                .getInt(RockeryHammerProgressServerDataProvider.PROGRESS_KEY);
        float progress = Mth.clamp(progressPercent / 100.0F, 0.0F, 1.0F);
        root.setBoxProgress(MessageType.NORMAL, progress);
    }
}
