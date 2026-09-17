package com.sshakusora.shadowsandpetals.registries;

import com.sshakusora.shadowsandpetals.ShadowsAndPetals;
import com.sshakusora.shadowsandpetals.blockentity.ShishiOdoshiBlockEntity;
import com.sshakusora.shadowsandpetals.item.barrel.WoodenBarrelItemFluidHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

@EventBusSubscriber(modid = ShadowsAndPetals.MOD_ID)
public class CapabilityRegistry {

    @SubscribeEvent
    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                BlockEntityRegistry.SHISHI_ODOSHI.get(),
                (blockEntity, side) -> new ShishiOdoshiBlockEntity.FluidHandler(blockEntity)
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                BlockEntityRegistry.IRORI.get(),
                (blockEntity, side) -> new InvWrapper(blockEntity)
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                BlockEntityRegistry.BONSAI.get(),
                (blockEntity, side) -> side == null
                        ? blockEntity.getPlantStorage()
                        : null
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                BlockEntityRegistry.COPPER_TEAPOT.get(),
                (blockEntity, side) -> blockEntity.getFluidTank()
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                BlockEntityRegistry.WOODEN_BARREL.get(),
                (blockEntity, side) -> blockEntity.getFluidTank()
        );
        event.registerItem(
                Capabilities.FluidHandler.ITEM,
                (stack, ignored) -> new WoodenBarrelItemFluidHandler(stack),
                BlockRegistry.WOODEN_BARREL.get()
        );
    }
}
