package com.mental.gingerbreadgrove.datagen;

import com.mental.gingerbreadgrove.registry.ModDimensions;
import com.mental.gingerbreadgrove.worldgen.ModPlacedFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * The five biomes of the dimension. Which biome appears where is decided by the climate parameters
 * in data/gingerbread_grove/dimension/gingerbread_grove.json; the ground blocks by the surface rule
 * in the noise settings.
 */
final class ModBiomes {

    private ModBiomes() {
    }

    static void bootstrap(BootstrapContext<Biome> context) {
        HolderGetter<PlacedFeature> placed = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carvers = context.lookup(Registries.CONFIGURED_CARVER);

        context.register(ModDimensions.GROVE_BIOME, biome(0.7F, 0.4F,
                colours(0xF8E6C8, 0xF9E1EA, 0x6B3E26, 0x3D2214), 0.012F,
                features(placed, carvers)
                        .vegetal(ModPlacedFeatures.CANDY_CANE_TREES, ModPlacedFeatures.SPARSE_FESTIVE_FIR_TREES,
                                ModPlacedFeatures.GUMDROP_BUSHES, ModPlacedFeatures.SWEET_FLOWERS,
                                ModPlacedFeatures.SUGAR_GRASS, ModPlacedFeatures.GIFT_BOXES)));

        context.register(ModDimensions.FESTIVE_FIR_FOREST, biome(0.3F, 0.8F,
                colours(0xD8E4F4, 0xE8F0FA, 0x6B3E26, 0x3D2214), 0.02F,
                features(placed, carvers)
                        .vegetal(ModPlacedFeatures.DENSE_FESTIVE_FIR_TREES, ModPlacedFeatures.SWEET_FLOWERS,
                                ModPlacedFeatures.SUGAR_GRASS, ModPlacedFeatures.GIFT_BOXES_COMMON)));

        context.register(ModDimensions.FROSTED_FLATS, biome(0.3F, 0.2F,
                colours(0xFDF3E7, 0xFFFFFF, 0x6B3E26, 0x3D2214), 0.025F,
                features(placed, carvers)
                        .local(ModPlacedFeatures.FROSTING_PATCHES)
                        .surfaceStructures(ModPlacedFeatures.GIANT_CANDY_CANES)
                        .vegetal(ModPlacedFeatures.SPARSE_CANDY_CANE_TREES, ModPlacedFeatures.SUGAR_GRASS_DENSE,
                                ModPlacedFeatures.SWEET_FLOWERS, ModPlacedFeatures.GIFT_BOXES)));

        context.register(ModDimensions.PEPPERMINT_PEAKS, biome(0.4F, 0.3F,
                colours(0xEADCF2, 0xF3E9F7, 0x6B3E26, 0x3D2214), 0.015F,
                features(placed, carvers)
                        .ores(ModPlacedFeatures.ORE_CINNAMON_EXTRA)
                        .vegetal(ModPlacedFeatures.SPARSE_FESTIVE_FIR_TREES, ModPlacedFeatures.SUGAR_GRASS)));

        context.register(ModDimensions.COCOA_SEA, biome(0.8F, 0.5F,
                colours(0xF5D9C4, 0xE9CDB8, 0x5A3420, 0x2E1A10), 0.006F,
                features(placed, carvers)
                        .surfaceStructures(ModPlacedFeatures.MARSHMALLOW_ISLANDS)));
    }

    // ---- Builders -------------------------------------------------------------------------------

    private record Colours(int sky, int fog, int water, int waterFog) {
    }

    private static Colours colours(int sky, int fog, int water, int waterFog) {
        return new Colours(sky, fog, water, waterFog);
    }

    /** Every biome: no rain (the flakes are particles), shared caves and ores, candy-coloured skies. */
    private static Biome biome(float temperature, float downfall, Colours c, float flakes, Features features) {
        BiomeSpecialEffects effects = new BiomeSpecialEffects.Builder()
                .skyColor(c.sky())
                .fogColor(c.fog())
                .waterColor(c.water())
                .waterFogColor(c.waterFog())
                .grassColorOverride(0xE8D5CB)
                .foliageColorOverride(0xE8828E)
                .ambientParticle(new AmbientParticleSettings(ParticleTypes.WHITE_ASH, flakes))
                .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                .build();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(temperature)
                .downfall(downfall)
                .specialEffects(effects)
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(features.build())
                .build();
    }

    private static Features features(HolderGetter<PlacedFeature> placed, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        return new Features(placed, carvers);
    }

    /** Small wrapper so each biome lists only what makes it different. */
    private static final class Features {
        private final HolderGetter<PlacedFeature> placed;
        private final BiomeGenerationSettings.Builder builder;

        Features(HolderGetter<PlacedFeature> placed, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
            this.placed = placed;
            this.builder = new BiomeGenerationSettings.Builder(placed, carvers);
            builder.addCarver(GenerationStep.Carving.AIR, Carvers.CAVE);
            add(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.ORE_SUGAR_CRYSTAL, ModPlacedFeatures.ORE_CINNAMON);
        }

        @SafeVarargs
        private Features add(GenerationStep.Decoration step, ResourceKey<PlacedFeature>... keys) {
            for (ResourceKey<PlacedFeature> key : keys) {
                builder.addFeature(step, placed.getOrThrow(key));
            }
            return this;
        }

        @SafeVarargs
        final Features local(ResourceKey<PlacedFeature>... keys) {
            return add(GenerationStep.Decoration.LOCAL_MODIFICATIONS, keys);
        }

        @SafeVarargs
        final Features surfaceStructures(ResourceKey<PlacedFeature>... keys) {
            return add(GenerationStep.Decoration.SURFACE_STRUCTURES, keys);
        }

        @SafeVarargs
        final Features ores(ResourceKey<PlacedFeature>... keys) {
            return add(GenerationStep.Decoration.UNDERGROUND_ORES, keys);
        }

        @SafeVarargs
        final Features vegetal(ResourceKey<PlacedFeature>... keys) {
            return add(GenerationStep.Decoration.VEGETAL_DECORATION, keys);
        }

        BiomeGenerationSettings build() {
            return builder.build();
        }
    }
}
