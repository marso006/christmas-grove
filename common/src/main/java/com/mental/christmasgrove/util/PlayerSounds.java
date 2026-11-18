package com.mental.christmasgrove.util;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

/** Plays a sound only this player hears (what ServerPlayer.playNotifySound did before 1.21.11). */
public final class PlayerSounds {

    private PlayerSounds() {
    }

    public static void play(ServerPlayer player, Holder<SoundEvent> sound, SoundSource source, float volume, float pitch) {
        player.connection.send(new ClientboundSoundPacket(sound, source, player.getX(), player.getY(), player.getZ(),
                volume, pitch, player.getRandom().nextLong()));
    }

    public static void play(ServerPlayer player, SoundEvent sound, SoundSource source, float volume, float pitch) {
        play(player, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), source, volume, pitch);
    }
}
