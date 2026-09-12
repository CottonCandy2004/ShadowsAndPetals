package com.sshakusora.shadowsandpetals.client;

import com.mojang.datafixers.util.Either;
import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.api.outline.BlockOutlineContext;
import com.sshakusora.shadowsandpetals.api.outline.OutlineGeometry;
import com.sshakusora.shadowsandpetals.block.decoration.curtain.CurtainBlock;
import com.sshakusora.shadowsandpetals.client.outline.BlockOutlineRegistry;
import com.sshakusora.shadowsandpetals.client.outline.BlockOutlineRenderer;
import com.sshakusora.shadowsandpetals.client.renderer.BonsaiBreakingOverlay;
import com.sshakusora.shadowsandpetals.item.hammer.HammerClientExtensions;
import com.sshakusora.shadowsandpetals.item.harrow.HarrowClientExtensions;
import com.sshakusora.shadowsandpetals.tooltip.TooltipComponentRegistry;
import com.sshakusora.shadowsandpetals.tooltip.TooltipModifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.minecraft.world.phys.shapes.CollisionContext;

/** Client game-bus hooks that must run after the client has been initialized. */
@EventBusSubscriber(modid = ShadowsAndPetals.MOD_ID, value = Dist.CLIENT)
public final class ClientGameEvents {
    private ClientGameEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        HammerClientExtensions.clientTick();
        HarrowClientExtensions.clientTick();
    }

    @SubscribeEvent
    public static void onRenderBlockHighlight(RenderHighlightEvent.Block event) {
        var minecraft = net.minecraft.client.Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        var target = event.getTarget();
        var state = minecraft.level.getBlockState(target.getBlockPos());
        if (CurtainBlock.isAnimating(state)) {
            event.setCanceled(true);
            return;
        }

        CollisionContext collisionContext = minecraft.player == null
                ? CollisionContext.empty()
                : CollisionContext.of(minecraft.player);
        OutlineGeometry geometry = BlockOutlineRegistry.createGeometry(
                state,
                new BlockOutlineContext(target.getBlockPos(), target, collisionContext));
        if (geometry == null) {
            return;
        }

        event.setCanceled(true);
        BlockOutlineRenderer.render(
                geometry,
                event.getPoseStack(),
                event.getMultiBufferSource(),
                target.getBlockPos(),
                event.getCamera().getPosition(),
                0x66000000);
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            BonsaiBreakingOverlay.render(event);
        }
    }

    @SubscribeEvent
    public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
        for (TooltipComponentRegistry.Entry entry : TooltipComponentRegistry.gather(event.getItemStack())) {
            event.getTooltipElements().add(Either.right(entry.component()));
            event.setMaxWidth(Math.max(event.getMaxWidth(), entry.minimumWidth()));
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        TooltipModifier.applyIfPresent(event);
    }
}
