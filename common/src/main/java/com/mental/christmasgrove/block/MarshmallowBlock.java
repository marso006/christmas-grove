package com.mental.christmasgrove.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Squishy and springy: no fall damage, and things bounce off it (sneak to land without bouncing). */
public class MarshmallowBlock extends Block {

    public static final MapCodec<MarshmallowBlock> CODEC = simpleCodec(MarshmallowBlock::new);

    public MarshmallowBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (entity.isSuppressingBounce()) {
            super.fallOn(level, state, pos, entity, fallDistance);
        } else {
            entity.causeFallDamage(fallDistance, 0.0F, level.damageSources().fall());
        }
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        if (entity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(level, entity);
            return;
        }
        Vec3 motion = entity.getDeltaMovement();
        if (motion.y < 0.0) {
            double factor = entity instanceof LivingEntity ? 0.85 : 0.6;
            entity.setDeltaMovement(motion.x, -motion.y * factor, motion.z);
        }
    }
}
