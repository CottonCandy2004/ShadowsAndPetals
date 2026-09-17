package com.sshakusora.shadowsandpetals.item.barrel;

import com.sshakusora.shadowsandpetals.blockentity.WoodenBarrelBlockEntity;
import com.sshakusora.shadowsandpetals.registries.BlockRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.Nullable;

/**
 * A placeable wooden barrel item that can also pick up a full source fluid,
 * like the vanilla empty bucket.
 */
public class WoodenBarrelBlockItem extends BlockItem {
    public WoodenBarrelBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.isSecondaryUseActive()
                && WoodenBarrelItemFluid.read(context.getItemInHand()).isPresent()) {
            InteractionResult placement = tryPlaceStoredFluid(
                    context.getLevel(),
                    context.getPlayer(),
                    context.getHand()
            );
            return placement == InteractionResult.PASS ? InteractionResult.FAIL : placement;
        }

        InteractionResult pickup = tryPickupFluid(
                context.getLevel(),
                context.getPlayer(),
                context.getHand()
        );
        if (pickup.consumesAction()) {
            return pickup;
        }

        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()
                && WoodenBarrelItemFluid.read(player.getItemInHand(hand)).isPresent()) {
            InteractionResult result = tryPlaceStoredFluid(level, player, hand);
            return new InteractionResultHolder<>(result, player.getItemInHand(hand));
        }

        InteractionResult pickup = tryPickupFluid(level, player, hand);
        return pickup.consumesAction()
                ? new InteractionResultHolder<>(pickup, player.getItemInHand(hand))
                : super.use(level, player, hand);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(
            BlockPos pos,
            Level level,
            @Nullable Player player,
            ItemStack itemStack,
            BlockState placedState
    ) {
        if (!level.isClientSide()) {
            return super.updateCustomBlockEntityTag(pos, level, player, itemStack, placedState);
        }

        CustomData blockEntityData = itemStack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (blockEntityData == null
                || !(level.getBlockEntity(pos) instanceof WoodenBarrelBlockEntity barrel)) {
            return false;
        }

        return blockEntityData.loadInto(barrel, level.registryAccess());
    }

    private InteractionResult tryPlaceStoredFluid(Level level, Player player, InteractionHand hand) {
        if (player == null) {
            return InteractionResult.PASS;
        }

        ItemStack stack = player.getItemInHand(hand);
        FluidStack storedFluid = WoodenBarrelItemFluid.read(stack).orElse(FluidStack.EMPTY);
        if (storedFluid.isEmpty() || storedFluid.getAmount() < FluidType.BUCKET_VOLUME) {
            return InteractionResult.FAIL;
        }

        BlockHitResult hit = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        }

        BlockPos clickedPos = hit.getBlockPos();
        Direction side = hit.getDirection();
        BlockPos adjacentPos = clickedPos.relative(side);
        if (!level.mayInteract(player, clickedPos)
                || !player.mayUseItemAt(adjacentPos, side, stack)) {
            return InteractionResult.FAIL;
        }

        BlockState clickedState = level.getBlockState(clickedPos);
        BlockPos destination = clickedState.getBlock() instanceof LiquidBlockContainer container
                && container.canPlaceLiquid(player, level, clickedPos, clickedState, storedFluid.getFluid())
                ? clickedPos
                : adjacentPos;

        ItemStack usedStack = stack.copy();
        FluidActionResult result = FluidUtil.tryPlaceFluid(
                player,
                level,
                hand,
                destination,
                stack,
                storedFluid
        );
        if (!result.isSuccess()) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            applyContainerResult(player, hand, stack, result.getResult());
            player.awardStat(Stats.ITEM_USED.get(this));
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, destination, usedStack);
            }
        }

        return InteractionResult.SUCCESS;
    }

    private InteractionResult tryPickupFluid(Level level, Player player, InteractionHand hand) {
        if (player == null) {
            return InteractionResult.PASS;
        }

        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty() || WoodenBarrelItemFluid.read(stack).isPresent()) {
            return InteractionResult.PASS;
        }

        BlockHitResult hit = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        }

        BlockPos sourcePos = hit.getBlockPos();
        Direction side = hit.getDirection();
        if (!level.mayInteract(player, sourcePos)
                || !player.mayUseItemAt(sourcePos.relative(side), side, stack)) {
            return InteractionResult.FAIL;
        }

        FluidActionResult result = FluidUtil.tryPickUpFluid(
                stack,
                player,
                level,
                sourcePos,
                side
        );
        if (!result.isSuccess()) {
            // The SOURCE_ONLY ray hit a fluid, but this particular source could
            // not be transferred into a barrel. Match the bucket's no-op result
            // instead of placing a barrel into the source by accident.
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            applyContainerResult(player, hand, stack, result.getResult());
            player.awardStat(Stats.ITEM_USED.get(this));
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.FILLED_BUCKET.trigger(serverPlayer, result.getResult());
            }
        }

        return InteractionResult.SUCCESS;
    }

    private static void applyContainerResult(
            Player player,
            InteractionHand hand,
            ItemStack original,
            ItemStack result
    ) {
        if (player.hasInfiniteMaterials()) {
            return;
        }

        if (original.getCount() == 1) {
            player.setItemInHand(hand, result);
            return;
        }

        original.shrink(1);
        if (!player.getInventory().add(result)) {
            player.drop(result, false);
        }
    }

    public static ItemStack filledWoodenBarrel(Fluid fluid) {
        return WoodenBarrelItemFluid.write(
                new ItemStack(BlockRegistry.WOODEN_BARREL.get()),
                new FluidStack(fluid, WoodenBarrelBlockEntity.FLUID_CAPACITY)
        );
    }
}
