package com.mental.gingerbreadgrove.naughty;

import com.mental.gingerbreadgrove.entity.Krampus;
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

    private static final String ID = "gingerbread_grove_naughty_nice";
    private static final Factory<NaughtyNice> FACTORY = new Factory<>(NaughtyNice::new, NaughtyNice::load,
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE);

    private final Map<UUID, Integer> scores = new HashMap<>();

    public enum Standing {
        NAUGHTY, NEUTRAL, NICE
    }

    // ---- Public API ----------------------------------------------------------------------------

    public static int score(ServerPlayer player) {
        return get(player.server).scores.getOrDefault(player.getUUID(), 0);
    }

    public static Standing standing(int score) {
        if (score <= NAUGHTY_THRESHOLD) {
            return Standing.NAUGHTY;
        }
        return score >= NICE_THRESHOLD ? Standing.NICE : Standing.NEUTRAL;
    }

    /**
     * Changes a player's score and tells them about it. {@code reason} is a translation key suffix
     * under {@code naughty_nice.gingerbread_grove.reason.}.
     */
    public static void change(ServerPlayer player, int delta, String reason) {
        if (delta == 0 || player.isCreative() || player.isSpectator()) {
            return;
        }
        NaughtyNice data = get(player.server);
        int before = data.scores.getOrDefault(player.getUUID(), 0);
        int after = Mth.clamp(before + delta, MIN, MAX);
        data.scores.put(player.getUUID(), after);
        data.setDirty();

        Component why = Component.translatable("naughty_nice.gingerbread_grove.reason." + reason);
        if (delta < 0) {
            player.displayClientMessage(Component.translatable("naughty_nice.gingerbread_grove.naughty", why)
                    .withStyle(ChatFormatting.RED), true);
            player.playNotifySound(SoundEvents.VILLAGER_NO, SoundSource.PLAYERS, 0.6F, 0.8F);
        } else {
            player.displayClientMessage(Component.translatable("naughty_nice.gingerbread_grove.nice", why)
                    .withStyle(ChatFormatting.GREEN), true);
            player.playNotifySound(SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.PLAYERS, 0.6F, 1.6F);
        }

        if (after <= KRAMPUS_THRESHOLD && before > KRAMPUS_THRESHOLD) {
            Krampus.summonFor(player, false);
        }
    }

    /** Wipes the slate clean (after defeating Krampus). */
    public static void reset(ServerPlayer player) {
        NaughtyNice data = get(player.server);
        data.scores.put(player.getUUID(), 0);
        data.setDirty();
    }

    // ---- Storage -------------------------------------------------------------------------------

    private static NaughtyNice get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(FACTORY, ID);
    }

    private static NaughtyNice load(CompoundTag tag, HolderLookup.Provider registries) {
        NaughtyNice data = new NaughtyNice();
        for (Tag t : tag.getList("Scores", Tag.TAG_COMPOUND)) {
            CompoundTag entry = (CompoundTag) t;
            data.scores.put(NbtUtils.loadUUID(entry.get("Player")), entry.getInt("Score"));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        scores.forEach((uuid, score) -> {
            CompoundTag entry = new CompoundTag();
            entry.put("Player", NbtUtils.createUUID(uuid));
            entry.putInt("Score", score);
            list.add(entry);
        });
        tag.put("Scores", list);
        return tag;
    }
}
