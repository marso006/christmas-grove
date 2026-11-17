package com.mental.christmasgrove.event;

import com.mental.christmasgrove.entity.SantaSleigh;
import com.mental.christmasgrove.registry.ModDimensions;
import com.mental.christmasgrove.registry.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.time.LocalDate;
import java.time.Month;

/**
 * Decides when Santa flies over. In the Christmas Grove he visits every so often (more at night);
 * in the Overworld only on December nights. A Letter to Santa calls him straight away.
 */
public final class SantaSleighEvents {

    private static final int CHECK_INTERVAL = 600; // every 30 seconds
    private static final double SPAWN_DISTANCE = 100.0;

    private SantaSleighEvents() {
    }

    public static void tick(ServerLevel level) {
        if (level.getGameTime() % CHECK_INTERVAL != 0 || level.players().isEmpty()) {
            return;
        }
        boolean night = level.isNight();
        double chance;
        if (level.dimension() == ModDimensions.GROVE) {
            chance = night ? 0.08 : 0.025;
        } else if (level.dimension() == Level.OVERWORLD && LocalDate.now().getMonth() == Month.DECEMBER && night) {
            chance = 0.04;
        } else {
            return;
        }
        for (ServerPlayer player : level.players()) {
            if (level.random.nextDouble() < chance && !sleighNearby(level, player)) {
                flyOver(player, false);
                return; // at most one sleigh per check
            }
        }
    }

    private static boolean sleighNearby(ServerLevel level, ServerPlayer player) {
        return !level.getEntitiesOfClass(SantaSleigh.class, new AABB(player.blockPosition()).inflate(256)).isEmpty();
    }

    /** Starts a sleigh far from the player, heading on a path that passes just beside them. */
    public static void flyOver(ServerPlayer player, boolean fromLetter) {
        ServerLevel level = player.serverLevel();
        SantaSleigh sleigh = ModEntities.SANTA_SLEIGH.get().create(level);
        if (sleigh == null) {
            return;
        }
        double angle = level.random.nextDouble() * Math.PI * 2;
        Vec3 start = player.position().add(Math.cos(angle) * SPAWN_DISTANCE, 0, Math.sin(angle) * SPAWN_DISTANCE);
        int ground = Math.max(level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) player.getX(), (int) player.getZ()),
                level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) start.x, (int) start.z));
        double y = Math.max(player.getY(), ground) + 22 + level.random.nextInt(8);
        Vec3 sideways = new Vec3(-Math.sin(angle), 0, Math.cos(angle)).scale(level.random.nextInt(13) - 6);
        Vec3 target = player.position().add(sideways);

        sleigh.moveTo(start.x, y, start.z, 0, 0);
        sleigh.launch(target.subtract(start));
        level.addFreshEntity(sleigh);

        Component message = Component.translatable(fromLetter
                ? "entity.christmas_grove.santa_sleigh.letter" : "entity.christmas_grove.santa_sleigh.incoming")
                .withStyle(ChatFormatting.GOLD);
        for (ServerPlayer nearby : level.players()) {
            if (nearby.distanceToSqr(player) < 160 * 160) {
                nearby.sendSystemMessage(message);
                nearby.playNotifySound(SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.NEUTRAL, 1.0F, 2.0F);
            }
        }
    }
}
