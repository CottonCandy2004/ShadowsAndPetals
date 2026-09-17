package com.sshakusora.shadowsandpetals.event;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.block.WoodSetList;
import com.sshakusora.shadowsandpetals.registries.BlockRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = ShadowsAndPetals.MOD_ID)
public final class WoodCompatibilityEvents {
    private WoodCompatibilityEvents() {
    }

    @SubscribeEvent
    public static void onToolModification(BlockEvent.BlockToolModificationEvent event) {
        if (event.getItemAbility() != ItemAbilities.AXE_STRIP) {
            return;
        }

        var state = event.getState();
        for (WoodSetList.WoodSet woodSet : BlockRegistry.WOOD_SETS) {
            var stripped = strippedState(state, woodSet.log().get(), woodSet.strippedLog().get());
            if (stripped != null) {
                event.setFinalState(stripped);
                return;
            }
            stripped = strippedState(state, woodSet.wood().get(), woodSet.strippedWood().get());
            if (stripped != null) {
                event.setFinalState(stripped);
                return;
            }
            stripped = strippedState(state, woodSet.post().get(), woodSet.strippedPost().get());
            if (stripped != null) {
                event.setFinalState(stripped);
                return;
            }
            stripped = strippedState(state, woodSet.woodPost().get(), woodSet.strippedWoodPost().get());
            if (stripped != null) {
                event.setFinalState(stripped);
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onFurnaceFuel(FurnaceFuelBurnTimeEvent event) {
        for (WoodSetList.WoodSet woodSet : BlockRegistry.WOOD_SETS) {
            if (isItem(event, woodSet.post())
                    || isItem(event, woodSet.strippedPost())
                    || isItem(event, woodSet.woodPost())
                    || isItem(event, woodSet.strippedWoodPost())) {
                event.setBurnTime(100);
                return;
            }
            if (isItem(event, woodSet.verticalSlab())) {
                event.setBurnTime(150);
                return;
            }
        }
    }

    private static @Nullable BlockState strippedState(
            BlockState state,
            Block source,
            Block stripped
    ) {
        return state.is(source) ? stripped.withPropertiesOf(state) : null;
    }

    private static boolean isItem(
            FurnaceFuelBurnTimeEvent event,
            DeferredBlock<?> block
    ) {
        return event.getItemStack().is(block.asItem());
    }
}
