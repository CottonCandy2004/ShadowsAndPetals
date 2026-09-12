package com.sshakusora.shadowsandpetals.tooltip;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/** Shared translation-key conventions for item Tooltip data. */
public final class TooltipTranslationKeys {
    private TooltipTranslationKeys() {
    }

    /**
     * Returns the prefix used by generated Tooltip entries for an item registry
     * id, including the {@code .tooltip} suffix.
     */
    public static String itemTooltip(ResourceLocation itemId) {
        Objects.requireNonNull(itemId, "itemId");
        return "item." + itemId.getNamespace() + "." + itemId.getPath() + ".tooltip";
    }
}
