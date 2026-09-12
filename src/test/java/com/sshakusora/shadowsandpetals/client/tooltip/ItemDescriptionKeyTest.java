package com.sshakusora.shadowsandpetals.client.tooltip;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemDescriptionKeyTest {
    @Test
    void usesItemNamespaceForOrdinaryItems() {
        assertEquals(
                "item.minecraft.diamond.tooltip",
                ItemDescription.tooltipTranslationPrefix(
                        ResourceLocation.fromNamespaceAndPath("minecraft", "diamond")
                )
        );
    }

    @Test
    void usesItemNamespaceForBlockItems() {
        // BlockItem#getDescriptionId() is block.minecraft.stone in 1.21.1;
        // the registry-id based prefix must still use the item namespace.
        assertEquals(
                "item.minecraft.stone.tooltip",
                ItemDescription.tooltipTranslationPrefix(
                        ResourceLocation.fromNamespaceAndPath("minecraft", "stone")
                )
        );
    }

    @Test
    void buildsPrefixFromCanonicalRegistryId() {
        assertEquals(
                "item.shadowsandpetals.wind_chime.tooltip",
                ItemDescription.tooltipTranslationPrefix(
                        ResourceLocation.fromNamespaceAndPath("shadowsandpetals", "wind_chime")
                )
        );
    }
}
