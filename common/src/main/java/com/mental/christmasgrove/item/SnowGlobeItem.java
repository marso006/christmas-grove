package com.mental.christmasgrove.item;

import com.mental.christmasgrove.registry.ModBlocks;
import com.mental.christmasgrove.registry.ModDimensions;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

/**
 * Shake it to travel to the Christmas Grove; shake it there to return to the Overworld.
 * You arrive near the same X/Z, on top of the highest solid block. In the Grove the globe steers you
 * away from the Cocoa Sea to the nearest dry biome, and if you would still land in liquid it conjures
 * a small marshmallow raft under your feet.
 */
public class SnowGlobeItem extends Item {

    private static final int COOLDOWN_TICKS = 100;
    private static final int DRY_LAND_SEARCH_RADIUS = 768;

    public SnowGlobeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer serverPlayer) || !(level instanceof ServerLevel current)) {
            return InteractionResult.SUCCESS;
        }

        boolean inGrove = current.dimension() == ModDimensions.GROVE;
        ServerLevel target = current.getServer().getLevel(inGrove ? Level.OVERWORLD : ModDimensions.GROVE);
        if (target == null) {
            // The dimension JSON failed to load, e.g. a datapack error. Say so instead of failing silently.
            player.displayClientMessage(Component.translatable("item.christmas_grove.snow_globe.missing"), true);
            return InteractionResult.FAIL;
        }

        BlockPos column = player.blockPosition();
        if (!inGrove) {
            column = awayFromTheSea(target, column);
        }
        Vec3 destination = safeSurface(target, column, !inGrove);

        level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.4F);
        serverPlayer.teleport(new TeleportTransition(target, destination, Vec3.ZERO,
                player.getYRot(), player.getXRot(), TeleportTransition.PLAY_PORTAL_SOUND));
        player.getCooldowns().addCooldown(player.getItemInHand(hand), COOLDOWN_TICKS);
        return InteractionResult.SUCCESS;
    }

    /** If this column is in the Cocoa Sea, the nearest column in any other biome (by climate, no chunk loading). */
    private static BlockPos awayFromTheSea(ServerLevel grove, BlockPos from) {
        Holder<Biome> here = grove.getBiome(from.atY(64));
        if (!here.is(ModDimensions.COCOA_SEA)) {
            return from;
        }
        Pair<BlockPos, Holder<Biome>> found = grove.findClosestBiome3d(
                biome -> !biome.is(ModDimensions.COCOA_SEA), from.atY(64), DRY_LAND_SEARCH_RADIUS, 32, 64);
        return found == null ? from : found.getFirst();
    }

    /** The block above the highest motion-blocking block at this X/Z (loads the chunk if needed). */
    private static Vec3 safeSurface(ServerLevel level, BlockPos from, boolean allowRaft) {
        level.getChunk(from); // make sure the column is generated before reading the heightmap
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, from.getX(), from.getZ());
        y = Math.max(y, level.getMinY() + 1);
        BlockPos feet = new BlockPos(from.getX(), y, from.getZ());

        BlockState below = level.getBlockState(feet.below());
        if (allowRaft && !below.getFluidState().isEmpty()) {
            BlockState mallow = ModBlocks.MARSHMALLOW_BLOCK.get().defaultBlockState();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    level.setBlockAndUpdate(feet.below().offset(dx, 0, dz), mallow);
                }
            }
        }
        return Vec3.atBottomCenterOf(feet);
    }
}
