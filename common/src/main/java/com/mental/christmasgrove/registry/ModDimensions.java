package com.mental.christmasgrove.registry;

import com.mental.christmasgrove.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

/**
 * Keys for the dimension and its biomes. The dimension itself is data
 * ({@code data/christmas_grove/dimension/}); the biomes are written by datagen.
 */
public final class ModDimensions {

    public static final ResourceKey<Level> GROVE = ResourceKey.create(Registries.DIMENSION, Constants.id("christmas_grove"));

    /** Warm candy-cane forest: the heart of the dimension. */
    public static final ResourceKey<Biome> GROVE_BIOME = biome("gingerbread_grove");
    /** Cold, damp forest of decorated Christmas trees with presents underneath. */
    public static final ResourceKey<Biome> FESTIVE_FIR_FOREST = biome("festive_fir_forest");
    /** Cold, open frosting plains with giant candy canes. */
    public static final ResourceKey<Biome> FROSTED_FLATS = biome("frosted_flats");
    /** Striped peppermint mountains with iced summits. */
    public static final ResourceKey<Biome> PEPPERMINT_PEAKS = biome("peppermint_peaks");
    /** A hot-chocolate ocean with marshmallows floating on it. */
    public static final ResourceKey<Biome> COCOA_SEA = biome("cocoa_sea");

    private ModDimensions() {
    }

    private static ResourceKey<Biome> biome(String name) {
        return ResourceKey.create(Registries.BIOME, Constants.id(name));
    }
}
