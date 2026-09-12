package com.sshakusora.shadowsandpetals.client.model.registry;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

/** Client-only registry for decorators applied to baked block-state models. */
public final class BlockStateModelDecoratorRegistry {
    private static final java.util.List<Decorator> DECORATORS = new java.util.ArrayList<>();

    private BlockStateModelDecoratorRegistry() {
    }

    public static <B extends Block> Builder<B> forBlock(Class<B> blockType) {
        return new Builder<>(blockType);
    }

    public static void applyAll(ModelEvent.ModifyBakingResult event) {
        Map<ModelResourceLocation, BlockState> states = representativeStates();
        event.getModels().replaceAll((location, original) -> {
            BlockState state = states.get(location);
            if (state == null) {
                return original;
            }
            BakedModel model = original;
            for (Decorator decorator : DECORATORS) {
                if (decorator.matches(state.getBlock())) {
                    model = decorator.apply(state.getBlock(), state, model);
                }
            }
            return model;
        });
    }

    private static Map<ModelResourceLocation, BlockState> representativeStates() {
        Map<ModelResourceLocation, BlockState> result = new HashMap<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                result.putIfAbsent(BlockModelShaper.stateToModelLocation(state), state);
            }
        }
        return result;
    }

    public static final class Builder<B extends Block> {
        private final Class<B> blockType;
        private BiFunction<? super B, ? super BakedModel, ? extends BakedModel> wrapper;
        private StateAwareWrapper<B> stateAwareWrapper;

        private Builder(Class<B> blockType) {
            this.blockType = Objects.requireNonNull(blockType, "blockType");
        }

        public Builder<B> wrap(BiFunction<? super B, ? super BakedModel, ? extends BakedModel> wrapper) {
            this.wrapper = Objects.requireNonNull(wrapper, "wrapper");
            this.stateAwareWrapper = null;
            return this;
        }

        public Builder<B> wrapWithState(StateAwareWrapper<B> wrapper) {
            this.stateAwareWrapper = Objects.requireNonNull(wrapper, "wrapper");
            this.wrapper = null;
            return this;
        }

        public void register() {
            if (wrapper == null && stateAwareWrapper == null) {
                throw new IllegalStateException("Block-state model decorator is required for " + blockType.getName());
            }
            DECORATORS.add(new TypedDecorator<>(blockType, wrapper, stateAwareWrapper));
        }
    }

    @FunctionalInterface
    public interface StateAwareWrapper<B extends Block> {
        BakedModel apply(B block, BlockState state, BakedModel model);
    }

    private interface Decorator {
        boolean matches(Block block);

        BakedModel apply(Block block, BlockState state, BakedModel model);
    }

    private record TypedDecorator<B extends Block>(
            Class<B> blockType,
            BiFunction<? super B, ? super BakedModel, ? extends BakedModel> wrapper,
            StateAwareWrapper<B> stateAwareWrapper
    ) implements Decorator {
        @Override
        public boolean matches(Block block) {
            return blockType.isInstance(block);
        }

        @Override
        public BakedModel apply(Block block, BlockState state, BakedModel model) {
            B typedBlock = blockType.cast(block);
            return stateAwareWrapper != null
                    ? stateAwareWrapper.apply(typedBlock, state, model)
                    : wrapper.apply(typedBlock, model);
        }
    }
}
