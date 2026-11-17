package com.mental.gingerbreadgrove.datagen;

import com.mental.gingerbreadgrove.registry.ModBlocks;
import net.minecraft.data.BlockFamily;

import java.util.ArrayList;
import java.util.List;

/**
 * Vanilla {@link BlockFamily} descriptions of the stone and wood sets. Model and recipe providers
 * use them to generate every shape of a set in one call.
 */
final class ModFamilies {

    private static List<BlockFamily> stone;
    private static List<BlockFamily> wood;

    private ModFamilies() {
    }

    static List<BlockFamily> stone() {
        if (stone == null) {
            stone = new ArrayList<>();
            for (ModBlocks.StoneSet set : ModBlocks.stoneSets()) {
                BlockFamily.Builder builder = new BlockFamily.Builder(set.base().get())
                        .stairs(set.stairs().get())
                        .slab(set.slab().get());
                if (set.wall() != null) {
                    builder.wall(set.wall().get());
                }
                stone.add(builder.getFamily());
            }
        }
        return stone;
    }

    static List<BlockFamily> wood() {
        if (wood == null) {
            wood = new ArrayList<>();
            for (ModBlocks.WoodSet set : ModBlocks.woodSets()) {
                wood.add(new BlockFamily.Builder(set.planks().get())
                        .stairs(set.stairs().get())
                        .slab(set.slab().get())
                        .fence(set.fence().get())
                        .fenceGate(set.fenceGate().get())
                        .door(set.door().get())
                        .trapdoor(set.trapdoor().get())
                        .button(set.button().get())
                        .pressurePlate(set.pressurePlate().get())
                        .recipeGroupPrefix("wooden")
                        .recipeUnlockedBy("has_planks")
                        .getFamily());
            }
        }
        return wood;
    }
}
