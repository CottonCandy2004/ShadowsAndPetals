package com.sshakusora.shadowsandpetals.client.model.bonsai;

import com.sshakusora.shadowsandpetals.blockentity.BonsaiBlockEntity;
import com.sshakusora.shadowsandpetals.client.renderer.BonsaiPartCacheKey;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BonsaiTreeGeometryCacheTest {
    @Test
    void emptyQuadListsRemainStableAcrossRotationSegments() {
        List<BakedQuad> empty = List.of();

        assertSame(empty, BonsaiTreeGeometryCache.rotateQuads(empty, 0));
        assertSame(empty, BonsaiTreeGeometryCache.rotateQuads(empty, 3));
    }

    @Test
    void nullTargetBlockIsReportedAsUntinted() {
        assertEquals(-1, BonsaiTreeGeometryCache.getTargetTintIndex(null));
    }

    @Test
    void cacheKeyFactoryDropsUnplantedTreeIdentifiers() {
        BonsaiPartCacheKey key = BonsaiPartCacheKey.forState(
                BonsaiBlockEntity.Shape.SEMI_CASCADE,
                false,
                false,
                ResourceLocation.withDefaultNamespace("oak_log"),
                ResourceLocation.withDefaultNamespace("oak_leaves")
        );
        assertNull(key.trunkBlockId());
        assertNull(key.leavesBlockId());
    }
}
