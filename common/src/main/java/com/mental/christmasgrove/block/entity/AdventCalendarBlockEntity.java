package com.mental.christmasgrove.block.entity;

import com.mental.christmasgrove.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Remembers which doors of the calendar each player has opened this year. */
public class AdventCalendarBlockEntity extends BlockEntity {

    private int year;
    private final Map<UUID, BitSet> opened = new HashMap<>();

    public AdventCalendarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ADVENT_CALENDAR.get(), pos, state);
    }

    /** Marks a door as opened for this player. Returns false if it was already open. */
    public boolean open(UUID player, int year, int day) {
        if (year != this.year) {
            this.year = year;
            opened.clear(); // a new December, a fresh calendar
        }
        BitSet doors = opened.computeIfAbsent(player, k -> new BitSet(26));
        if (doors.get(day)) {
            return false;
        }
        doors.set(day);
        setChanged();
        return true;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        year = tag.getInt("Year");
        opened.clear();
        for (Tag t : tag.getList("Opened", Tag.TAG_COMPOUND)) {
            CompoundTag entry = (CompoundTag) t;
            BitSet doors = new BitSet(26);
            for (int day : entry.getIntArray("Days")) {
                doors.set(day);
            }
            opened.put(NbtUtils.loadUUID(entry.get("Player")), doors);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Year", year);
        ListTag list = new ListTag();
        opened.forEach((uuid, doors) -> {
            CompoundTag entry = new CompoundTag();
            entry.put("Player", NbtUtils.createUUID(uuid));
            entry.put("Days", new IntArrayTag(doors.stream().toArray()));
            list.add(entry);
        });
        tag.put("Opened", list);
    }
}
