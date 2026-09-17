package com.sshakusora.shadowsandpetals.blockentity;

import com.sshakusora.shadowsandpetals.block.decoration.irori.IroriBlock;
import com.sshakusora.shadowsandpetals.block.decoration.irori.IroriGrillBlock;
import com.sshakusora.shadowsandpetals.block.decoration.irori.IroriGrillCopperTeapotBlock;
import com.sshakusora.shadowsandpetals.block.decoration.irori.IroriGrillPartHolder;
import com.sshakusora.shadowsandpetals.blockentity.irori.IroriBlockEntity;
import com.sshakusora.shadowsandpetals.data.BuiltinLanguageKeys;
import com.sshakusora.shadowsandpetals.menu.TeapotMenu;
import com.sshakusora.shadowsandpetals.recipe.TeapotRecipe;
import com.sshakusora.shadowsandpetals.recipe.TeapotRecipeInput;
import com.sshakusora.shadowsandpetals.registries.BlockEntityRegistry;
import com.sshakusora.shadowsandpetals.registries.RecipeSerializerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class CopperTeapotBlockEntity extends RandomizableContainerBlockEntity {
    public static final int TEA_SLOT = 0;
    public static final int FLUID_CONTAINER_SLOT = 1;
    public static final int CONTAINER_SIZE = 2;
    public static final int FLUID_CAPACITY = FluidType.BUCKET_VOLUME;
    public static final float MAX_LID_LIFT = 1.5F / 16.0F;

    private static final int OPEN_EVENT_ID = 1;
    private static final float LID_SPEED = 0.1F;
    private static final String BREW_PROGRESS_KEY = "BrewProgress";
    private static final String ACTIVE_RECIPE_KEY = "ActiveRecipe";
    private static final Component DEFAULT_NAME =
            Component.translatable(BuiltinLanguageKeys.COPPER_TEAPOT_CONTAINER_NAME.key());

    private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    private final TeapotFluidTank fluidTank = new TeapotFluidTank();
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> BuiltInRegistries.FLUID.getId(fluidTank.getFluid().getFluid());
                case 1 -> fluidTank.getFluidAmount();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 2;
        }
    };
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState blockState) {
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState blockState) {
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState blockState, int previous, int current) {
            level.blockEvent(pos, blockState.getBlock(), OPEN_EVENT_ID, current);
        }

        @Override
        public boolean isOwnContainer(Player player) {
            if (player.containerMenu instanceof TeapotMenu teapotMenu) {
                Container container = teapotMenu.getContainer();
                return container == CopperTeapotBlockEntity.this;
            }
            return false;
        }
    };

    private int openCount;
    private int brewProgress;
    private @Nullable ResourceLocation activeRecipeId;
    private float lidProgress;
    private float lidProgressOld;

    @Override
    public void setRemoved() {
        cleanupInstalledGrillAfterRemoval();
        super.setRemoved();
    }

    private void cleanupInstalledGrillAfterRemoval() {
        Level level = getLevel();
        BlockState previousState = getBlockState();
        if (level == null
                || level.isClientSide()
                || !(previousState.getBlock() instanceof IroriGrillCopperTeapotBlock)) {
            return;
        }

        BlockState replacement = level.getBlockState(getBlockPos());
        if (replacement.getBlock() == previousState.getBlock()) {
            // The chunk is unloading, or the state changed without removing the entity.
            return;
        }

        boolean preservesGrillPart = IroriGrillPartHolder.isGrillPart(replacement)
                && IroriGrillPartHolder.masterPosition(getBlockPos(), previousState)
                .equals(IroriGrillPartHolder.masterPosition(getBlockPos(), replacement));
        if (!preservesGrillPart
                && IroriGrillBlock.isValidLower(level.getBlockState(getBlockPos().below()))) {
            IroriBlockEntity.removeInstalledGrill(
                    level,
                    getBlockPos().below(),
                    getBlockPos(),
                    true
            );
        }
    }

    public CopperTeapotBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.COPPER_TEAPOT.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CopperTeapotBlockEntity blockEntity) {
        if (!level.isClientSide()) {
            blockEntity.tryBrew((ServerLevel) level);
            return;
        }

        blockEntity.lidProgressOld = blockEntity.lidProgress;
        if (blockEntity.openCount > 0) {
            blockEntity.lidProgress = Math.min(1.0F, blockEntity.lidProgress + LID_SPEED);
        } else {
            blockEntity.lidProgress = Math.max(0.0F, blockEntity.lidProgress - LID_SPEED);
        }
    }

    public float getLidProgress(float partialTick) {
        return lidProgressOld + (lidProgress - lidProgressOld) * partialTick;
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    public static boolean isFluidContainer(ItemStack stack) {
        return !stack.isEmpty()
                && stack.copyWithCount(1).getCapability(Capabilities.FluidHandler.ITEM) != null;
    }

    public ContainerData getDataAccess() {
        return dataAccess;
    }

    public void recheckOpen() {
        if (!remove) {
            openersCounter.recheckOpeners(getLevel(), getBlockPos(), getBlockState());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag input, HolderLookup.Provider registries) {
        super.loadAdditional(input, registries);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        if (!tryLoadLootTable(input)) {
            ContainerHelper.loadAllItems(input, items, registries);
        }
        fluidTank.readFromNBT(registries, input);
        brewProgress = Math.max(0, input.getInt(BREW_PROGRESS_KEY));
        ResourceLocation parsedRecipe = ResourceLocation.tryParse(input.getString(ACTIVE_RECIPE_KEY));
        activeRecipeId = parsedRecipe;
        if (activeRecipeId == null) {
            brewProgress = 0;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag output, HolderLookup.Provider registries) {
        super.saveAdditional(output, registries);
        if (!trySaveLootTable(output)) {
            ContainerHelper.saveAllItems(output, items, registries);
        }
        fluidTank.writeToNBT(registries, output);
        if (brewProgress > 0 && activeRecipeId != null) {
            output.putInt(BREW_PROGRESS_KEY, brewProgress);
            output.putString(ACTIVE_RECIPE_KEY, activeRecipeId.toString());
        }
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return switch (slot) {
            case TEA_SLOT -> !isFluidContainer(stack);
            case FLUID_CONTAINER_SLOT -> isFluidContainer(stack);
            default -> false;
        };
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        super.setItem(slot, stack);
        if (slot == FLUID_CONTAINER_SLOT) {
            tryTransferFluidContainer();
        }
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    private void tryTransferFluidContainer() {
        if (level == null || level.isClientSide() || getItem(FLUID_CONTAINER_SLOT).isEmpty()) {
            return;
        }

        ItemStack container = getItem(FLUID_CONTAINER_SLOT);
        if (container.getCount() != 1) {
            return;
        }

        IFluidHandlerItem containerHandler = container.getCapability(Capabilities.FluidHandler.ITEM);
        if (containerHandler == null) {
            return;
        }

        FluidActionResult result;
        if (hasFluid(containerHandler) && fluidTank.getFluidAmount() == 0) {
            result = FluidUtil.tryEmptyContainer(container, fluidTank, FLUID_CAPACITY, null, true);
        } else if (!hasFluid(containerHandler) && fluidTank.getFluidAmount() > 0) {
            result = FluidUtil.tryFillContainer(container, fluidTank, FLUID_CAPACITY, null, true);
        } else {
            return;
        }

        if (result.isSuccess()) {
            setContainerSlotWithoutTransfer(result.getResult());
        }
    }

    private void setContainerSlotWithoutTransfer(ItemStack stack) {
        items.set(FLUID_CONTAINER_SLOT, stack);
        setChanged();
    }

    private static boolean hasFluid(IFluidHandler handler) {
        for (int index = 0; index < handler.getTanks(); index++) {
            if (!handler.getFluidInTank(index).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void tryBrew(ServerLevel level) {
        ItemStack ingredientStack = getItem(TEA_SLOT);
        FluidStack fluid = fluidTank.getFluid();
        int fluidAmount = fluidTank.getFluidAmount();
        if (ingredientStack.isEmpty() || fluid.isEmpty() || fluidAmount == 0) {
            resetBrewProgress();
            return;
        }

        TeapotRecipeInput input = new TeapotRecipeInput(
                fluid.copy(), ingredientStack);
        var recipeHolder = level.getRecipeManager()
                .getRecipeFor(RecipeSerializerRegistry.TEAPOT_BREWING_TYPE.get(), input, level);
        if (recipeHolder.isEmpty()) {
            resetBrewProgress();
            return;
        }

        var holder = recipeHolder.get();
        ResourceLocation recipeId = holder.id();
        TeapotRecipe recipe = holder.value();
        if (!recipeId.equals(activeRecipeId)) {
            activeRecipeId = recipeId;
            brewProgress = 0;
            setChanged();
        }
        if (!hasHeatSource(level)) {
            return;
        }

        brewProgress = Math.min(brewProgress + 1, recipe.processingTime());
        setChanged();
        if (brewProgress >= recipe.processingTime() && applyRecipe(recipe)) {
            resetBrewProgress();
        }
    }

    private boolean hasHeatSource(ServerLevel level) {
        BlockPos belowPos = worldPosition.below();
        BlockState belowState = level.getBlockState(belowPos);
        if (belowState.hasProperty(BlockStateProperties.LIT) && belowState.getValue(BlockStateProperties.LIT)) {
            return true;
        }
        return IroriBlock.hasGrill(belowState)
                && level.getBlockEntity(belowPos) instanceof IroriBlockEntity irori
                && irori.getBurnTime() > 0;
    }

    private void resetBrewProgress() {
        if (brewProgress != 0 || activeRecipeId != null) {
            brewProgress = 0;
            activeRecipeId = null;
            setChanged();
        }
    }

    private boolean applyRecipe(TeapotRecipe recipe) {
        int inputAmount = recipe.fluid().amount();
        FluidStack inputFluid = fluidTank.getFluid();
        FluidStack resultFluid = recipe.result().copy();
        ItemStack ingredientStack = getItem(TEA_SLOT);

        if (inputFluid.isEmpty()
                || inputFluid.getAmount() < inputAmount
                || !recipe.fluid().test(inputFluid)
                || !recipe.ingredient().test(ingredientStack)
                || resultFluid.isEmpty()
                || resultFluid.getAmount() <= 0) {
            return false;
        }

        int remainingAmount = inputFluid.getAmount() - inputAmount;
        if (remainingAmount > 0
                && !FluidStack.isSameFluidSameComponents(inputFluid, resultFluid)) {
            return false;
        }

        long finalAmount = (long) remainingAmount + resultFluid.getAmount();
        if (finalAmount > FLUID_CAPACITY) {
            return false;
        }

        FluidStack finalFluid = FluidStack.isSameFluidSameComponents(inputFluid, resultFluid)
                ? inputFluid.copyWithAmount((int) finalAmount)
                : resultFluid.copy();
        ItemStack remainingIngredient = ingredientStack.copy();
        remainingIngredient.shrink(1);

        items.set(TEA_SLOT, remainingIngredient);
        fluidTank.setFluid(finalFluid);
        setChanged();
        return true;
    }

    @Override
    protected Component getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new TeapotMenu(containerId, inventory, this, dataAccess);
    }

    @Override
    public void startOpen(Player player) {
        if (!remove && !player.isSpectator()) {
            openersCounter.incrementOpeners(
                    player,
                    getLevel(),
                    getBlockPos(),
                    getBlockState()
            );
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!remove && !player.isSpectator()) {
            openersCounter.decrementOpeners(
                    player, getLevel(), getBlockPos(), getBlockState());
        }
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == OPEN_EVENT_ID) {
            openCount = type;
            return true;
        }
        return super.triggerEvent(id, type);
    }

    private class TeapotFluidTank extends FluidTank {
        private TeapotFluidTank() {
            super(FLUID_CAPACITY);
        }

        @Override
        protected void onContentsChanged() {
            CopperTeapotBlockEntity.this.setChanged();
        }

        @Override
        public boolean isFluidValid(int index, FluidStack resource) {
            return index == 0 && resource != null && !resource.isEmpty();
        }
    }
}
