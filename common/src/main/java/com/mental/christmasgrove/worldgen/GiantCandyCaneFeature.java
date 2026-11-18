package com.mental.christmasgrove.worldgen;

import net.minecraft.world.level.chunk.ChunkGenerator;
import com.mental.christmasgrove.registry.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.List;

/**
 * A landmark: a candy cane taller than a house stuck in the ground, built from striped candy cane
 * logs. A straight shaft, then a hook that bends over to one side and curls back down.
 */
public record GiantCandyCaneFeature() implements Feature {

    /** Since 26.x a feature is a self-contained record; this one has no settings. */
    public static final MapCodec<GiantCandyCaneFeature> CODEC = MapCodec.unit(GiantCandyCaneFeature::new);

    @Override
    public MapCodec<GiantCandyCaneFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        BlockPos base = origin;
        if (!level.getBlockState(base.below()).is(BlockTags.DIRT)) {
            return false;
        }

        int height = 7 + random.nextInt(6);
        Direction hookSide = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockState vertical = ModBlocks.CANDY_CANE.log().get().defaultBlockState();
        BlockState horizontal = vertical.setValue(RotatedPillarBlock.AXIS, hookSide.getAxis());

        BlockPos top = base.above(height);
        List<BlockPos> hook = List.of(top.relative(hookSide, 1), top.relative(hookSide, 2), top.relative(hookSide, 3),
                top.relative(hookSide, 3).below(), top.relative(hookSide, 3).below(2));

        // Check the space first so canes never cut into trees or cliffs.
        for (int y = 0; y <= height; y++) {
            if (!level.isEmptyBlock(base.above(y))) {
                return false;
            }
        }
        for (BlockPos pos : hook) {
            if (!level.isEmptyBlock(pos)) {
                return false;
            }
        }

        level.setBlock(base.below(), ModBlocks.GINGERBREAD.get().defaultBlockState(), 2); // planted firmly
        for (int y = 0; y <= height; y++) {
            level.setBlock(base.above(y), vertical, 2);
        }
        // The hook: over three blocks, then curling down two.
        level.setBlock(hook.get(0), horizontal, 2);
        level.setBlock(hook.get(1), horizontal, 2);
        for (int i = 2; i < hook.size(); i++) {
            level.setBlock(hook.get(i), vertical, 2);
        }
        return true;
    }
}
