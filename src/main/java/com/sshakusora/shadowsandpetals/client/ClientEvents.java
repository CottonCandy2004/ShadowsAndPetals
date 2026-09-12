package com.sshakusora.shadowsandpetals.client;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.client.animation.SAPAnimationResources;
import com.sshakusora.shadowsandpetals.client.animation.SAPAnimations;
import com.sshakusora.shadowsandpetals.client.ct.CTModelRegistry;
import com.sshakusora.shadowsandpetals.client.model.BlockModelRegistry;
import com.sshakusora.shadowsandpetals.client.model.WindChimeItemModel;
import com.sshakusora.shadowsandpetals.client.model.WoodenBarrelItemModel;
import com.sshakusora.shadowsandpetals.client.model.bonsai.BonsaiTreeGeometryCache;
import com.sshakusora.shadowsandpetals.client.outline.*;
import com.sshakusora.shadowsandpetals.client.particle.FallingLeafParticle;
import com.sshakusora.shadowsandpetals.client.renderer.*;
import com.sshakusora.shadowsandpetals.client.screen.IroriScreen;
import com.sshakusora.shadowsandpetals.client.screen.TeapotScreen;
import com.sshakusora.shadowsandpetals.client.tooltip.ClientRockeryTooltip;
import com.sshakusora.shadowsandpetals.client.tooltip.RockeryTooltipComponent;
import com.sshakusora.shadowsandpetals.item.hammer.HammerClientExtensions;
import com.sshakusora.shadowsandpetals.item.harrow.HarrowClientExtensions;
import com.sshakusora.shadowsandpetals.registries.*;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = ShadowsAndPetals.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
@SuppressWarnings({"removal"})
public final class ClientEvents {
    private ClientEvents() {}

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleRegistry.GINKGO.get(), FallingLeafParticle.GinkgoProvider::new);
        event.registerSpriteSet(ParticleRegistry.MAPLE.get(), FallingLeafParticle.MapleProvider::new);
        event.registerSpriteSet(ParticleRegistry.SAKURA.get(), FallingLeafParticle.SakuraProvider::new);
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MenuRegistry.IRORI.get(), IroriScreen::new);
        event.register(MenuRegistry.TEAPOT.get(), TeapotScreen::new);
    }

    @SubscribeEvent
    public static void registerClientTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(RockeryTooltipComponent.class, ClientRockeryTooltip::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.SEAT.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.SAND_EXCAVATION.get(), SandExcavationBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.IRORI.get(), IroriBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.VANITY.get(),
                com.sshakusora.shadowsandpetals.client.VanityBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.SHISHI_ODOSHI.get(), ShishiOdoshiBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.SHISHI_ODOSHI_PIPE.get(), ShishiOdoshiPipeBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.WOODEN_BARREL.get(), WoodenBarrelBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.CURTAIN.get(), CurtainBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.WIND_CHIME.get(), WindChimeBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.LARGE_CURTAIN.get(), LargeCurtainBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.COPPER_TEAPOT.get(), CopperTeapotBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new HammerClientExtensions(), ItemRegistry.HAMMER.get(), ItemRegistry.CHISEL.get());
        event.registerItem(new HarrowClientExtensions(), ItemRegistry.HARROW.get());
        event.registerItem(new WindChimeItemModel(), BlockRegistry.WIND_CHIME.get().asItem());
        event.registerItem(new WoodenBarrelItemModel(), BlockRegistry.WOODEN_BARREL.get().asItem());
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        BonsaiBlockTintSources.register(event);
    }

    @SubscribeEvent
    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        SAPAnimations.init();
        event.registerReloadListener(SAPAnimationResources.INSTANCE);
        CurtainOutlineCache.register(event);
        LargeCurtainOutlineCache.register(event);
        LampOutlineCache.register(event);
        TeapotOutlineCache.register(event);
        VanityOutlineCache.register(event);
        event.registerReloadListener(new ResourceManagerReloadListener() {
            @Override
            public void onResourceManagerReload(net.minecraft.server.packs.resources.ResourceManager resourceManager) {
                BonsaiTreeGeometryCache.invalidate();
            }
        });
    }

    @SubscribeEvent
    public static void modifyBakedModels(ModelEvent.ModifyBakingResult event) {
        BlockModelRegistry.wrapBlockStateModels(event);
        BlockModelRegistry.wrapRecessedLampCompositeModels(event);
        BlockModelRegistry.wrapIroriGrillCopperTeapotModels(event);
        CTModelRegistry.wrapModels(event);
    }

    @SubscribeEvent
    public static void cacheBakedModels(ModelEvent.BakingCompleted event) {
        BlockModelRegistry.cacheBakedModels(event);
    }
}
