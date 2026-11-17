package com.mental.gingerbreadgrove.item;

import com.mental.gingerbreadgrove.registry.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

/**
 * Shake it to travel to the Gingerbread Grove; shake it there to return to the Overworld.
 * You arrive at the same X/Z, on top of the highest solid block.
 */
public class SnowGlobeItem extends Item {

    private static final int COOLDOWN_TICKS = 100;

    public SnowGlobeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer serverPlayer) || !(level instanceof ServerLevel current)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        boolean inGrove = current.dimension() == ModDimensions.GROVE;
        ServerLevel target = current.getServer().getLevel(inGrove ? Level.OVERWORLD : ModDimensions.GROVE);
        if (target == null) {
            // The dimension JSON failed to load, e.g. a datapack error. Say so instead of failing silently.
            player.displayClientMessage(Component.translatable("item.gingerbread_grove.snow_globe.missing"), true);
            return InteractionResultHolder.fail(stack);
        }

        Vec3 destination = surfaceAbove(target, player.blockPosition());
        level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.4F);
        serverPlayer.changeDimension(new DimensionTransition(target, destination, Vec3.ZERO,
                player.getYRot(), player.getXRot(), DimensionTransition.PLAY_PORTAL_SOUND));
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        return InteractionResultHolder.success(stack);
    }

    /** The centre of the block above the highest motion-blocking block at this X/Z (loads the chunk if needed). */
    private static Vec3 surfaceAbove(ServerLevel level, BlockPos from) {
        level.getChunk(from); // make sure the column is generated before reading the heightmap
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, from.getX(), from.getZ());
        y = Math.max(y, level.getMinBuildHeight() + 1);
        return Vec3.atBottomCenterOf(new BlockPos(from.getX(), y, from.getZ()));
    }
}
