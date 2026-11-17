package com.mental.christmasgrove.worldgen;

import com.mental.christmasgrove.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

/** Keys for configured features. Their definitions are written by datagen (ModWorldgenProvider). */
public final class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> CANDY_CANE_TREE = key("candy_cane_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FESTIVE_FIR_TREE = key("festive_fir_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SWEET_FLOWERS = key("sweet_flowers");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SUGAR_GRASS = key("sugar_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GUMDROP_BUSHES = key("gumdrop_bushes");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GIFT_BOXES = key("gift_boxes");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_SUGAR_CRYSTAL = key("ore_sugar_crystal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_CINNAMON = key("ore_cinnamon");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GIANT_CANDY_CANE = key("giant_candy_cane");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MARSHMALLOW_ISLAND = key("marshmallow_island");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FROSTING_PATCH = key("frosting_patch");

    private ModConfiguredFeatures() {
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> key(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Constants.id(name));
    }
}
