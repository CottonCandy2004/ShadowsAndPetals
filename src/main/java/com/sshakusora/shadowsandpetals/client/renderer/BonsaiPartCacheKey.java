package com.sshakusora.shadowsandpetals.client.renderer;

import com.sshakusora.shadowsandpetals.blockentity.BonsaiBlockEntity;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/** Immutable key for one fully resolved bonsai geometry/material combination. */
public record BonsaiPartCacheKey(
        BonsaiBlockEntity.Shape shape,
        boolean dead,
        @Nullable ResourceLocation trunkBlockId,
        @Nullable ResourceLocation leavesBlockId
) {
    public BonsaiPartCacheKey {
        if (trunkBlockId == null || dead) {
            leavesBlockId = null;
        }
    }

    public static BonsaiPartCacheKey forState(
            BonsaiBlockEntity.Shape shape,
            boolean planted,
            boolean dead,
            @Nullable ResourceLocation trunkBlockId,
            @Nullable ResourceLocation leavesBlockId
    ) {
        return new BonsaiPartCacheKey(
                shape,
                dead,
                planted ? trunkBlockId : null,
                planted && !dead ? leavesBlockId : null
        );
    }
}
