package com.sshakusora.shadowsandpetals.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sshakusora.shadowsandpetals.blockentity.WoodenBarrelBlockEntity;
import com.sshakusora.shadowsandpetals.client.renderer.ClientFluidRenderInfo;
import com.sshakusora.shadowsandpetals.client.renderer.WoodenBarrelFluidSpecialRenderer;
import com.sshakusora.shadowsandpetals.item.barrel.WoodenBarrelItemFluid;
import com.sshakusora.shadowsandpetals.registries.BlockRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

/** 1.21.1 custom item renderer for a wooden barrel and its stored fluid. */
public final class WoodenBarrelItemModel implements IClientItemExtensions {
    private final BlockEntityWithoutLevelRenderer renderer;

    public WoodenBarrelItemModel() {
        Minecraft minecraft = Minecraft.getInstance();
        this.renderer = new Renderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
    }

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return renderer;
    }

    private static final class Renderer extends BlockEntityWithoutLevelRenderer {
        private Renderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet entityModels) {
            super(dispatcher, entityModels);
        }

        @Override
        public void renderByItem(
                ItemStack stack,
                ItemDisplayContext displayContext,
                PoseStack poseStack,
                MultiBufferSource buffers,
                int packedLight,
                int packedOverlay
        ) {
            Minecraft minecraft = Minecraft.getInstance();
            ItemRenderer itemRenderer = minecraft.getItemRenderer();
            BakedModel baseModel = minecraft.getBlockRenderer().getBlockModel(
                    BlockRegistry.WOODEN_BARREL.get().defaultBlockState());
            if (baseModel != minecraft.getModelManager().getMissingModel()) {
                WoodenBarrelFluidSpecialRenderer.renderModel(
                        itemRenderer, baseModel, stack, poseStack, buffers, packedLight, packedOverlay);
            }

            WoodenBarrelItemFluid.read(stack).ifPresent(fluid -> renderFluid(
                    fluid, poseStack, buffers, packedLight));
        }

        private static void renderFluid(
                FluidStack fluid,
                PoseStack poseStack,
                MultiBufferSource buffers,
                int packedLight
        ) {
            int amount = Math.min(fluid.getAmount(), WoodenBarrelBlockEntity.FLUID_CAPACITY);
            if (amount <= 0) {
                return;
            }
            Minecraft minecraft = Minecraft.getInstance();
            var player = minecraft.player;
            ClientFluidRenderInfo.Info info = ClientFluidRenderInfo.createItemSurface(
                    fluid,
                    minecraft.level,
                    player == null ? null : player.blockPosition());
            if (info.sprite() == null) {
                return;
            }
            WoodenBarrelFluidSpecialRenderer.render(
                    new WoodenBarrelFluidSpecialRenderer.RenderData(
                            info.sprite(), info.color(), amount,
                            WoodenBarrelBlockEntity.FLUID_CAPACITY, info.lightEmission()),
                    poseStack, buffers, packedLight);
        }
    }
}
