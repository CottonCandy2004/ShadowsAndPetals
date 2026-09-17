package com.sshakusora.shadowsandpetals.blockentity;

import com.sshakusora.shadowsandpetals.registries.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.material.Fluids.WATER;

/**
 * Stores the barrel's fluid contents using NeoForge's fluid capability API.
 */
public class WoodenBarrelBlockEntity extends BlockEntity {
    public static final int FLUID_CAPACITY = FluidType.BUCKET_VOLUME;
    public static final int BOTTLE_AMOUNT = FLUID_CAPACITY / 4;
    public static final int RAIN_AMOUNT = 50;

    private final WoodenBarrelFluidTank fluidTank = new WoodenBarrelFluidTank();

    public WoodenBarrelBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.WOODEN_BARREL.get(), pos, blockState);
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    public boolean hasFluid() {
        return !fluidTank.getFluid().isEmpty();
    }

    public boolean canInsert(FluidStack resource, int amount) {
        if (resource == null || resource.isEmpty() || amount <= 0) {
            return false;
        }

        return fluidTank.fill(resource.copyWithAmount(amount), IFluidHandler.FluidAction.SIMULATE) == amount;
    }

    public int insert(FluidStack resource, int amount) {
        if (resource == null || resource.isEmpty() || amount <= 0) {
            return 0;
        }

        return fluidTank.fill(resource.copyWithAmount(amount), IFluidHandler.FluidAction.EXECUTE);
    }

    public boolean insertExactly(FluidStack resource, int amount) {
        if (!canInsert(resource, amount)) {
            return false;
        }

        return fluidTank.fill(resource.copyWithAmount(amount), IFluidHandler.FluidAction.EXECUTE) == amount;
    }

    public boolean canExtract(FluidStack resource, int amount) {
        return resource != null
                && !resource.isEmpty()
                && amount > 0
                && fluidTank.drain(resource.copyWithAmount(amount), IFluidHandler.FluidAction.SIMULATE)
                .getAmount() == amount;
    }

    public boolean extractExactly(FluidStack resource, int amount) {
        if (!canExtract(resource, amount)) {
            return false;
        }

        return fluidTank.drain(resource.copyWithAmount(amount), IFluidHandler.FluidAction.EXECUTE)
                .getAmount() == amount;
    }

    public boolean canInsertWater(int amount) {
        return amount > 0 && canInsert(new FluidStack(WATER, amount), amount);
    }

    public boolean canExtractWater(int amount) {
        return amount > 0 && canExtract(new FluidStack(WATER, amount), amount);
    }

    public int insertWater(int amount) {
        return amount > 0 ? insert(new FluidStack(WATER, amount), amount) : 0;
    }

    public boolean insertWaterExactly(int amount) {
        return amount > 0 && insertExactly(new FluidStack(WATER, amount), amount);
    }

    public boolean extractWaterExactly(int amount) {
        return amount > 0 && extractExactly(new FluidStack(WATER, amount), amount);
    }

    public void fillFromRain() {
        if (level != null && !level.isClientSide()) {
            insertWater(RAIN_AMOUNT);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag input, HolderLookup.Provider registries) {
        super.loadAdditional(input, registries);
        fluidTank.readFromNBT(registries, input);
    }

    @Override
    protected void saveAdditional(CompoundTag output, HolderLookup.Provider registries) {
        super.saveAdditional(output, registries);
        fluidTank.writeToNBT(registries, output);
    }

    private void onFluidChanged() {
        setChanged();
        if (level != null && !level.isClientSide()) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    private class WoodenBarrelFluidTank extends FluidTank {
        private WoodenBarrelFluidTank() {
            super(FLUID_CAPACITY);
        }

        @Override
        protected void onContentsChanged() {
            WoodenBarrelBlockEntity.this.onFluidChanged();
        }

        @Override
        public boolean isFluidValid(FluidStack resource) {
            return resource != null && !resource.isEmpty()
                    && (getFluid().isEmpty() || FluidStack.isSameFluidSameComponents(getFluid(), resource));
        }

        @Override
        public boolean isFluidValid(int index, FluidStack resource) {
            return index == 0 && isFluidValid(resource);
        }
    }
}
