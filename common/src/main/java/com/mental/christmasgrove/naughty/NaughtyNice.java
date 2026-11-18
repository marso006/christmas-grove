package com.mental.christmasgrove.naughty;

import com.mental.christmasgrove.Constants;
import com.mental.christmasgrove.util.PlayerSounds;
import net.minecraft.core.UUIDUtil;
import com.mental.christmasgrove.entity.Krampus;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Santa's list. Every player has a score from -50 (very naughty) to +50 (very nice), saved with the
 * world. Hurting friendly festive creatures or Santa lowers it; trading with elves, taming pups and
 * fighting festive monsters raises it. Santa's presents depend on it, and sinking to
 * {@link #KRAMPUS_THRESHOLD} brings Krampus.
 */
public final class NaughtyNice extends SavedData {

    public static final int MIN = -50;
    public static final int MAX = 50;
    public static final int NAUGHTY_THRESHOLD = -10;
    public static final int NICE_THRESHOLD = 15;
    public static final int KRAMPUS_THRESHOLD = -15;

    private static final String ID = "christmas_grove_naughty_nice";
    /** Same file format as before; since 1.21.5 saved data is described by a codec. */
    private static final SavedDataType<NaughtyNice> TYPE = new SavedDataType<>(Constants.id("naughty_nice"), NaughtyNice::new,
            CompoundTag.CODEC.xmap(NaughtyNice::load, NaughtyNice::toTag), DataFixTypes.SAVED_DATA_COMMAND_STORAGE);

    private final Map<UUID, Integer> scores = new HashMap<>();

    public enum Standing {
        NAUGHTY, NEUTRAL, NICE
    }

    // ---- Public API ----------------------------------------------------------------------------

    public static int score(ServerPlayer player) {
        return get(player.level().getServer()).scores.getOrDefault(player.getUUID(), 0);
    }

    public static Standing standing(int score) {
        if (score <= NAUGHTY_THRESHOLD) {
            return Standing.NAUGHTY;
        }
        return score >= NICE_THRESHOLD ? Standing.NICE : Standing.NEUTRAL;
    }

    /**
     * Changes a player's score and tells them about it. {@code reason} is a translation key suffix
     * under {@code naughty_nice.christmas_grove.reason.}.
     */
    public static void change(ServerPlayer player, int delta, String reason) {
        if (delta == 0 || player.isCreative() || player.isSpectator()) {
            return;
        }
        NaughtyNice data = get(player.level().getServer());
        int before = data.scores.getOrDefault(player.getUUID(), 0);
        int after = Mth.clamp(before + delta, MIN, MAX);
        data.scores.put(player.getUUID(), after);
        data.setDirty();

        Component why = Component.translatable("naughty_nice.christmas_grove.reason." + reason);
        if (delta < 0) {
            player.sendOverlayMessage(Component.translatable("naughty_nice.christmas_grove.naughty", why)
                    .withStyle(ChatFormatting.RED));
            PlayerSounds.play(player, SoundEvents.VILLAGER_NO, SoundSource.PLAYERS, 0.6F, 0.8F);
        } else {
            player.sendOverlayMessage(Component.translatable("naughty_nice.christmas_grove.nice", why)
                    .withStyle(ChatFormatting.GREEN));
            PlayerSounds.play(player, SoundEvents.NOTE_BLOCK_CHIME, SoundSource.PLAYERS, 0.6F, 1.6F);
        }

        if (after <= KRAMPUS_THRESHOLD && before > KRAMPUS_THRESHOLD) {
            Krampus.summonFor(player, false);
        }
    }

    /** Wipes the slate clean (after defeating Krampus). */
    public static void reset(ServerPlayer player) {
        NaughtyNice data = get(player.level().getServer());
        data.scores.put(player.getUUID(), 0);
        data.setDirty();
    }

    // ---- Storage -------------------------------------------------------------------------------

    private static NaughtyNice get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    private static NaughtyNice load(CompoundTag tag) {
        NaughtyNice data = new NaughtyNice();
        for (Tag t : tag.getListOrEmpty("Scores")) {
            CompoundTag entry = (CompoundTag) t;
            data.scores.put(entry.read("Player", UUIDUtil.CODEC).orElseThrow(), entry.getIntOr("Score", 0));
        }
        return data;
    }

    private CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        scores.forEach((uuid, score) -> {
            CompoundTag entry = new CompoundTag();
            entry.store("Player", UUIDUtil.CODEC, uuid);
            entry.putInt("Score", score);
            list.add(entry);
        });
        tag.put("Scores", list);
        return tag;
    }
}
