package com.mental.gingerbreadgrove.registry;

import com.mental.gingerbreadgrove.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

/**
 * Keys for the dimension and biome. The dimension itself is pure data: see
 * {@code data/gingerbread_grove/dimension/} and {@code dimension_type/} in common resources.
 */
public final class ModDimensions {

    public static final ResourceKey<Level> GROVE = ResourceKey.create(Registries.DIMENSION, Constants.id("gingerbread_grove"));
    public static final ResourceKey<Biome> GROVE_BIOME = ResourceKey.create(Registries.BIOME, Constants.id("gingerbread_grove"));

    private ModDimensions() {
    }
}
