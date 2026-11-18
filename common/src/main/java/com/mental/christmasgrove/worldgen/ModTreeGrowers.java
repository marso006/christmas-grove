package com.mental.christmasgrove.worldgen;

import net.minecraft.world.level.block.grower.TreeGrower;

import net.minecraft.util.random.WeightedList;

/** Saplings grow into the tree features defined in {@link ModConfiguredFeatures}. */
public final class ModTreeGrowers {

    public static final TreeGrower CANDY_CANE = new TreeGrower("christmas_grove_candy_cane",
            WeightedList.of(ModConfiguredFeatures.CANDY_CANE_TREE), WeightedList.of(), WeightedList.of(), ModConfiguredFeatures.CANDY_CANE_TREE);

    public static final TreeGrower FESTIVE_FIR = new TreeGrower("christmas_grove_festive_fir",
            WeightedList.of(ModConfiguredFeatures.FESTIVE_FIR_TREE), WeightedList.of(), WeightedList.of(), ModConfiguredFeatures.FESTIVE_FIR_TREE);

    private ModTreeGrowers() {
    }
}
