package com.mental.gingerbreadgrove.worldgen;

import com.mental.gingerbreadgrove.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/** Keys for placed features, which biomes list. Definitions are written by datagen. */
public final class ModPlacedFeatures {

    public static final ResourceKey<PlacedFeature> CANDY_CANE_TREES = key("candy_cane_trees");
    public static final ResourceKey<PlacedFeature> FESTIVE_FIR_TREES = key("festive_fir_trees");
    public static final ResourceKey<PlacedFeature> SWEET_FLOWERS = key("sweet_flowers");
    public static final ResourceKey<PlacedFeature> SUGAR_GRASS = key("sugar_grass");
    public static final ResourceKey<PlacedFeature> GUMDROP_BUSHES = key("gumdrop_bushes");
    public static final ResourceKey<PlacedFeature> GIFT_BOXES = key("gift_boxes");
    public static final ResourceKey<PlacedFeature> ORE_SUGAR_CRYSTAL = key("ore_sugar_crystal");
    public static final ResourceKey<PlacedFeature> ORE_CINNAMON = key("ore_cinnamon");

    private ModPlacedFeatures() {
    }

    public static ResourceKey<PlacedFeature> key(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Constants.id(name));
    }
}
