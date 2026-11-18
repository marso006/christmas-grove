package com.mental.christmasgrove.entity;

import com.mental.christmasgrove.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * A wobbling, sugar-coated gumdrop that hops toward players and splits into smaller gumdrops when
 * killed (all the slime behaviour comes from {@link Slime}). Comes in four flavours, picked from its
 * UUID by the renderer. Spawns in the dark anywhere in the dimension, not just in "slime chunks".
 */
public class GumdropBlob extends Slime {

    public GumdropBlob(EntityType<? extends GumdropBlob> type, Level level) {
        super(type, level);
    }

    @Override
    protected ParticleOptions getParticleType() {
        return new ItemParticleOption(ParticleTypes.ITEM, ModItems.GUMDROPS.get());
    }

    /** Flavour 0-3: cherry, lime, lemon, grape. */
    public int flavour() {
        return Math.floorMod(getUUID().hashCode(), 4);
    }

    public static boolean checkGumdropSpawnRules(EntityType<GumdropBlob> type, ServerLevelAccessor level, EntitySpawnReason reason,
                                                 BlockPos pos, RandomSource random) {
        return level.getDifficulty() != Difficulty.PEACEFUL
                && Monster.isDarkEnoughToSpawn(level, pos, random)
                && checkMobSpawnRules(type, level, reason, pos, random);
    }
}
