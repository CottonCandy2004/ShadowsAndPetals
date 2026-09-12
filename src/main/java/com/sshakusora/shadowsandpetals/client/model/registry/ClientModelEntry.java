package com.sshakusora.shadowsandpetals.client.model.registry;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.Set;

interface ClientModelEntry {
    void registerModels(ModelEvent.RegisterAdditional event, Set<ModelResourceLocation> registeredIds);

    void cacheModels(ModelEvent.BakingCompleted event);
}
