package com.sshakusora.shadowsandpetals.compat;

import net.neoforged.fml.ModList;

/**
 * Optional-mod gate retained for 1.21.1.  Integrations are disabled when the
 * corresponding mod is absent, which is the normal migration configuration.
 */
public final class CompatManager {
    private CompatManager() {}

    public static boolean isLoaded(String modId) {
        ModList modList = ModList.get();
        return modList != null && modList.isLoaded(modId);
    }

    public static boolean isSereneSeasonsLoaded() {
        return isLoaded(CompatInfo.SERENE_SEASONS);
    }
}
