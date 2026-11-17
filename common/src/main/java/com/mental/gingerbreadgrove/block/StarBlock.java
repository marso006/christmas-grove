package com.mental.gingerbreadgrove.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** The golden star on top of a Festive Fir. Glows at light level 15 and sparkles now and then. */
public class StarBlock extends Block {

    public static final MapCodec<StarBlock> CODEC = simpleCodec(StarBlock::new);
    private static final VoxelShape SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 14.0, 13.0);

    public StarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(4) == 0) {
            level.addParticle(ParticleTypes.END_ROD,
                    pos.getX() + 0.2 + random.nextDouble() * 0.6,
                    pos.getY() + 0.3 + random.nextDouble() * 0.6,
                    pos.getZ() + 0.2 + random.nextDouble() * 0.6,
                    0.0, 0.01, 0.0);
        }
    }
}
