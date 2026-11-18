package com.mental.christmasgrove.datagen;

import com.mental.christmasgrove.registry.ModDimensions;
import com.mental.christmasgrove.registry.ModEntities;
import com.mental.christmasgrove.worldgen.ModPlacedFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.attribute.AmbientParticle;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.util.ARGB;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * The five biomes of the dimension. Which biome appears where is decided by the climate parameters
 * in data/christmas_grove/dimension/christmas_grove.json; the ground blocks by the surface rule
 * in the noise settings.
 */
final class ModBiomes {

    private ModBiomes() {
    }

    static void bootstrap(BootstrapContext<Biome> context) {
        HolderGetter<PlacedFeature> placed = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<WorldCarver> carvers = context.lookup(Registries.CARVER);

        context.register(ModDimensions.GROVE_BIOME, biome(0.7F, 0.4F,
                colours(0x9EC3F0, 0xEAF0FA, 0x6B3E26, 0x3D2214), 0.012F,
                features(placed, carvers)
                        .vegetal(ModPlacedFeatures.CANDY_CANE_TREES, ModPlacedFeatures.SPARSE_FESTIVE_FIR_TREES,
                                ModPlacedFeatures.GUMDROP_BUSHES, ModPlacedFeatures.SWEET_FLOWERS,
                                ModPlacedFeatures.SUGAR_GRASS, ModPlacedFeatures.GIFT_BOXES)
                        .spawn(MobCategory.CREATURE, ModEntities.GINGERBREAD_MAN.get(), 12, 2, 4)
                        .spawn(MobCategory.CREATURE, ModEntities.PEPPERMINT_PUP.get(), 5, 1, 3)
                        .spawn(MobCategory.CREATURE, ModEntities.ELF.get(), 2, 1, 2)
                        .landMonsters()));

        context.register(ModDimensions.FESTIVE_FIR_FOREST, biome(0.3F, 0.8F,
                colours(0x86AEE6, 0xDCE6F6, 0x6B3E26, 0x3D2214), 0.02F,
                features(placed, carvers)
                        .vegetal(ModPlacedFeatures.DENSE_FESTIVE_FIR_TREES, ModPlacedFeatures.SWEET_FLOWERS,
                                ModPlacedFeatures.SUGAR_GRASS, ModPlacedFeatures.GIFT_BOXES_COMMON)
                        .spawn(MobCategory.CREATURE, ModEntities.REINDEER.get(), 8, 2, 4)
                        .spawn(MobCategory.CREATURE, ModEntities.GINGERBREAD_MAN.get(), 6, 1, 3)
                        .spawn(MobCategory.CREATURE, ModEntities.ELF.get(), 3, 1, 3)
                        .landMonsters()));

        context.register(ModDimensions.FROSTED_FLATS, biome(0.3F, 0.2F,
                colours(0xB4D2F4, 0xF2F6FC, 0x6B3E26, 0x3D2214), 0.025F,
                features(placed, carvers)
                        .local(ModPlacedFeatures.FROSTING_PATCHES)
                        .surfaceStructures(ModPlacedFeatures.GIANT_CANDY_CANES)
                        .vegetal(ModPlacedFeatures.SPARSE_CANDY_CANE_TREES, ModPlacedFeatures.SUGAR_GRASS_DENSE,
                                ModPlacedFeatures.SWEET_FLOWERS, ModPlacedFeatures.GIFT_BOXES)
                        .spawn(MobCategory.CREATURE, ModEntities.REINDEER.get(), 12, 3, 6)
                        .spawn(MobCategory.CREATURE, ModEntities.GINGERBREAD_MAN.get(), 4, 1, 2)
                        .landMonsters()
                        .spawn(MobCategory.MONSTER, ModEntities.EVIL_SNOWMAN.get(), 40, 1, 3)));

        context.register(ModDimensions.PEPPERMINT_PEAKS, biome(0.4F, 0.3F,
                colours(0xB7A9EE, 0xEDE6FA, 0x6B3E26, 0x3D2214), 0.015F,
                features(placed, carvers)
                        .ores(ModPlacedFeatures.ORE_CINNAMON_EXTRA)
                        .vegetal(ModPlacedFeatures.SPARSE_FESTIVE_FIR_TREES, ModPlacedFeatures.SUGAR_GRASS)
                        .spawn(MobCategory.CREATURE, ModEntities.REINDEER.get(), 4, 1, 3)
                        .landMonsters()));

        context.register(ModDimensions.COCOA_SEA, biome(0.8F, 0.5F,
                colours(0xF2B88C, 0xF0D2B8, 0x5A3420, 0x2E1A10), 0.006F,
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
        // Since 1.21.11 sky, fog, particles and ambience are environment attributes; colours are RGB vectors.
        BiomeSpecialEffects effects = new BiomeSpecialEffects.Builder()
                .waterColor(c.water())
                .grassColorOverride(0xE8D5CB)
                .foliageColorOverride(0xE8828E)
                .build();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(temperature)
                .downfall(downfall)
                .specialEffects(effects)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.vector3fFromRGB24(c.sky()))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.vector3fFromRGB24(c.fog()))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.vector3fFromRGB24(c.waterFog()))
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(ParticleTypes.WHITE_ASH, flakes))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                .mobSpawnSettings(features.spawns.build())
                .generationSettings(features.build())
                .build();
    }

    private static Features features(HolderGetter<PlacedFeature> placed, HolderGetter<WorldCarver> carvers) {
        return new Features(placed, carvers);
    }

    /** Small wrapper so each biome lists only what makes it different. */
    private static final class Features {
        private final HolderGetter<PlacedFeature> placed;
        private final BiomeGenerationSettings.Builder builder;
        final MobSpawnSettings.Builder spawns = new MobSpawnSettings.Builder();

        Features(HolderGetter<PlacedFeature> placed, HolderGetter<WorldCarver> carvers) {
            this.placed = placed;
            this.builder = new BiomeGenerationSettings.Builder(placed, carvers);
            builder.addCarver(carvers.getOrThrow(Carvers.CAVE));
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

        Features spawn(MobCategory category, EntityType<?> type, int weight, int min, int max) {
            spawns.addSpawn(type, category, weight, UniformInt.of(min, max));
            return this;
        }

        /** Night-time and cave monsters shared by every land biome. */
        Features landMonsters() {
            return spawn(MobCategory.MONSTER, ModEntities.GUMDROP_BLOB.get(), 60, 1, 3)
                    .spawn(MobCategory.MONSTER, ModEntities.COOKIE_SOLDIER.get(), 45, 1, 2)
                    .spawn(MobCategory.MONSTER, ModEntities.EVIL_SNOWMAN.get(), 35, 1, 2);
        }

        BiomeGenerationSettings build() {
            return builder.build();
        }
    }
}
