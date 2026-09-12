package com.sshakusora.shadowsandpetals.block.nature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class SAPLeavesBlock extends LeavesBlock {
    private final float leafParticleChance;
    private final Supplier<ParticleOptions> fallingLeafParticleSupplier;

    public static final MapCodec<SAPLeavesBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.floatRange(0.0F, 1.0F)
                            .fieldOf("leaf_particle_chance")
                            .forGetter(block -> block.leafParticleChance),
                    propertiesCodec()
            ).apply(instance, SAPLeavesBlock::new)
    );

    public SAPLeavesBlock(BlockBehaviour.Properties properties) {
        this(0.01F, properties);
    }

    public SAPLeavesBlock(float leafParticleChance, BlockBehaviour.Properties properties) {
        super(properties);
        this.leafParticleChance = leafParticleChance;
        this.fallingLeafParticleSupplier = () -> ParticleTypes.CHERRY_LEAVES;
    }

    public SAPLeavesBlock(float leafParticleChance, BlockBehaviour.Properties properties,
                          Supplier<? extends ParticleOptions> fallingLeafParticleSupplier) {
        super(properties);
        this.leafParticleChance = leafParticleChance;
        this.fallingLeafParticleSupplier = fallingLeafParticleSupplier::get;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (random.nextFloat() >= this.leafParticleChance) {
            return;
        }

        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        if (!isFaceFull(belowState.getCollisionShape(level, below), Direction.UP)) {
            ParticleUtils.spawnParticleBelow(
                    level,
                    pos,
                    random,
                    this.fallingLeafParticleSupplier.get()
            );
        }
    }

    @Override
    public MapCodec<SAPLeavesBlock> codec() {
        return CODEC;
    }
}