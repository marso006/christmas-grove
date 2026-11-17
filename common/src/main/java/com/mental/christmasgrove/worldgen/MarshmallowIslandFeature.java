package com.mental.christmasgrove.worldgen;

import com.mental.christmasgrove.registry.ModBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * A puffy marshmallow bobbing on the Cocoa Sea: a flattened blob of bouncy Marshmallow Blocks half
 * sunk at the water line, sometimes with a few smaller ones clustered around it.
 */
public class MarshmallowIslandFeature extends Feature<NoneFeatureConfiguration> {

    public MarshmallowIslandFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        // The origin is the first air block above the surface; over the sea that is just above the water.
        BlockPos waterline = context.origin().below();
        if (!level.getBlockState(waterline).is(Blocks.WATER)) {
            return false;
        }
        BlockState mallow = ModBlocks.MARSHMALLOW_BLOCK.get().defaultBlockState();
        int pieces = 1 + random.nextInt(3);
        for (int i = 0; i < pieces; i++) {
            BlockPos centre = i == 0 ? waterline
                    : waterline.offset(random.nextInt(9) - 4, 0, random.nextInt(9) - 4);
            float radius = (i == 0 ? 2.5F : 1.5F) + random.nextFloat();
            blob(level, centre, radius, mallow);
        }
        return true;
    }

    private static void blob(WorldGenLevel level, BlockPos centre, float radius, BlockState state) {
        int r = (int) Math.ceil(radius);
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                for (int dy = -1; dy <= 2; dy++) {
                    float squash = dy * 1.6F; // flatter than a sphere, like a marshmallow
                    if (dx * dx + dz * dz + squash * squash <= radius * radius) {
                        BlockPos pos = centre.offset(dx, dy, dz);
                        BlockState existing = level.getBlockState(pos);
                        if (existing.isAir() || existing.is(Blocks.WATER)) {
                            level.setBlock(pos, state, 2);
                        }
                    }
                }
            }
        }
    }
}
