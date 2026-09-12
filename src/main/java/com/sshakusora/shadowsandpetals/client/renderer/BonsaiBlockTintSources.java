package com.sshakusora.shadowsandpetals.client.renderer;

import com.sshakusora.shadowsandpetals.blockentity.BonsaiBlockEntity;
import com.sshakusora.shadowsandpetals.client.model.bonsai.BonsaiTreeGeometryCache;
import com.sshakusora.shadowsandpetals.registries.BlockRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import org.jetbrains.annotations.Nullable;

/** Legacy block-color bridge for the tint indices assigned to bonsai quads. */
public final class BonsaiBlockTintSources {
    private static final BlockColor TRUNK = new LayerColor(true);
    private static final BlockColor LEAVES = new LayerColor(false);

    private BonsaiBlockTintSources() {
    }

    public static void register(RegisterColorHandlersEvent.Block event) {
        event.register(TRUNK, BlockRegistry.BONSAI.get());
        event.register(LEAVES, BlockRegistry.BONSAI.get());
    }

    private record LayerColor(boolean trunk) implements BlockColor {
        @Override
        public int getColor(
                BlockState state,
                @Nullable BlockAndTintGetter level,
                @Nullable BlockPos pos,
                int tintIndex
        ) {
            if (level == null || pos == null) {
                return 0xFFFFFFFF;
            }
            BlockEntity entity = level.getBlockEntity(pos);
            if (!(entity instanceof BonsaiBlockEntity bonsai)) {
                return 0xFFFFFFFF;
            }
            BonsaiBlockEntity.RenderData data = bonsai.getModelData()
                    .get(BonsaiBlockEntity.RENDER_DATA);
            if (data == null || !data.planted() || (!trunk && data.dead())) {
                return 0xFFFFFFFF;
            }
            ResourceLocation blockId = trunk ? data.trunkBlockId() : data.leavesBlockId();
            if (blockId == null) {
                return 0xFFFFFFFF;
            }
            Block target = BuiltInRegistries.BLOCK.get(blockId);
            if (target == Blocks.AIR) {
                return 0xFFFFFFFF;
            }
            int targetTintIndex = BonsaiTreeGeometryCache.getTargetTintIndex(blockId);
            if (targetTintIndex < 0) {
                return 0xFFFFFFFF;
            }
            int color = Minecraft.getInstance().getBlockColors().getColor(
                    target.defaultBlockState(), level, pos, targetTintIndex);
            return color == -1 ? 0xFFFFFFFF : color;
        }
    }
}
