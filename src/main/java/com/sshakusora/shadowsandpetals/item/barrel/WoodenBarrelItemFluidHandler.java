package com.sshakusora.shadowsandpetals.item.barrel;

import com.sshakusora.shadowsandpetals.blockentity.WoodenBarrelBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

/**
 * NeoForge fluid capability for a wooden barrel item.
 *
 * <p>The item uses the same typed block-entity component as a dropped barrel
 * block, which means fluid operations and block placement share one format.</p>
 */
public final class WoodenBarrelItemFluidHandler implements IFluidHandlerItem {
    private final ItemStack container;

    public WoodenBarrelItemFluidHandler(ItemStack container) {
        this.container = container;
    }

    @Override
    public ItemStack getContainer() {
        return container;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return tank == 0
                ? WoodenBarrelItemFluid.read(container).orElse(FluidStack.EMPTY)
                : FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == 0 ? WoodenBarrelBlockEntity.FLUID_CAPACITY : 0;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack resource) {
        if (tank != 0 || resource == null || resource.isEmpty() || container.getCount() != 1) {
            return false;
        }
        FluidStack current = getFluidInTank(0);
        return current.isEmpty() || FluidStack.isSameFluidSameComponents(current, resource);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (container.getCount() != 1 || resource == null || resource.isEmpty()
                || !isFluidValid(0, resource)) {
            return 0;
        }

        FluidStack current = getFluidInTank(0);
        int amount = Math.min(
                resource.getAmount(),
                Math.max(0, WoodenBarrelBlockEntity.FLUID_CAPACITY - current.getAmount())
        );
        if (amount > 0 && action.execute()) {
            FluidStack updated = current.isEmpty()
                    ? resource.copyWithAmount(amount)
                    : current.copyWithAmount(current.getAmount() + amount);
            WoodenBarrelItemFluid.write(container, updated);
        }
        return amount;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource == null || resource.isEmpty()
                || !FluidStack.isSameFluidSameComponents(resource, getFluidInTank(0))) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (container.getCount() != 1 || maxDrain <= 0) {
            return FluidStack.EMPTY;
        }

        FluidStack current = getFluidInTank(0);
        if (current.isEmpty()) {
            return FluidStack.EMPTY;
        }

        int amount = Math.min(maxDrain, current.getAmount());
        FluidStack drained = current.copyWithAmount(amount);
        if (action.execute()) {
            if (amount == current.getAmount()) {
                WoodenBarrelItemFluid.withoutFluid(container);
            } else {
                WoodenBarrelItemFluid.write(container, current.copyWithAmount(current.getAmount() - amount));
            }
        }
        return drained;
    }
}
