package com.mental.christmasgrove.worldgen;

import com.mental.christmasgrove.Constants;
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
    public static final ResourceKey<PlacedFeature> ORE_CINNAMON_EXTRA = key("ore_cinnamon_extra");
    public static final ResourceKey<PlacedFeature> SPARSE_CANDY_CANE_TREES = key("sparse_candy_cane_trees");
    public static final ResourceKey<PlacedFeature> DENSE_FESTIVE_FIR_TREES = key("dense_festive_fir_trees");
    public static final ResourceKey<PlacedFeature> SPARSE_FESTIVE_FIR_TREES = key("sparse_festive_fir_trees");
    public static final ResourceKey<PlacedFeature> GIFT_BOXES_COMMON = key("gift_boxes_common");
    public static final ResourceKey<PlacedFeature> SUGAR_GRASS_DENSE = key("sugar_grass_dense");
    public static final ResourceKey<PlacedFeature> GIANT_CANDY_CANES = key("giant_candy_canes");
    public static final ResourceKey<PlacedFeature> MARSHMALLOW_ISLANDS = key("marshmallow_islands");
    public static final ResourceKey<PlacedFeature> FROSTING_PATCHES = key("frosting_patches");

    private ModPlacedFeatures() {
    }

    public static ResourceKey<PlacedFeature> key(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Constants.id(name));
    }
}
