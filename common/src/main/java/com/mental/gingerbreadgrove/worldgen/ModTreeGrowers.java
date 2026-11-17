package com.mental.gingerbreadgrove.worldgen;

import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

/** Saplings grow into the tree features defined in {@link ModConfiguredFeatures}. */
public final class ModTreeGrowers {

    public static final TreeGrower CANDY_CANE = new TreeGrower("gingerbread_grove_candy_cane",
            Optional.empty(), Optional.of(ModConfiguredFeatures.CANDY_CANE_TREE), Optional.empty());

    public static final TreeGrower FESTIVE_FIR = new TreeGrower("gingerbread_grove_festive_fir",
            Optional.empty(), Optional.of(ModConfiguredFeatures.FESTIVE_FIR_TREE), Optional.empty());

    private ModTreeGrowers() {
    }
}
