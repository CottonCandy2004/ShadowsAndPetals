package com.sshakusora.shadowsandpetals.client.model;

import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;

/**
 * Compatibility type retained for code that used the 26.x client-extension
 * registration.  NeoForge 1.21.1 has no dynamic-tint collection hook on
 * {@link IClientBlockExtensions}; the lamp's stored slab is composed by
 * {@link RecessedLampCompositeBlockStateModel} through ModelData instead.
 */
public final class RecessedLampCompositeClientExtensions implements IClientBlockExtensions {
    private RecessedLampCompositeClientExtensions() {
    }
}
