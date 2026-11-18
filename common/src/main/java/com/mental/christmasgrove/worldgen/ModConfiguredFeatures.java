package com.mental.christmasgrove.worldgen;

import com.mental.christmasgrove.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;

/** Keys for configured features. Their definitions are written by datagen (ModWorldgenProvider). */
public final class ModConfiguredFeatures {

    public static final ResourceKey<Feature> CANDY_CANE_TREE = key("candy_cane_tree");
    public static final ResourceKey<Feature> FESTIVE_FIR_TREE = key("festive_fir_tree");
    public static final ResourceKey<Feature> SWEET_FLOWERS = key("sweet_flowers");
    public static final ResourceKey<Feature> SUGAR_GRASS = key("sugar_grass");
    public static final ResourceKey<Feature> GUMDROP_BUSHES = key("gumdrop_bushes");
    public static final ResourceKey<Feature> GIFT_BOXES = key("gift_boxes");
    public static final ResourceKey<Feature> ORE_SUGAR_CRYSTAL = key("ore_sugar_crystal");
    public static final ResourceKey<Feature> ORE_CINNAMON = key("ore_cinnamon");
    public static final ResourceKey<Feature> GIANT_CANDY_CANE = key("giant_candy_cane");
    public static final ResourceKey<Feature> MARSHMALLOW_ISLAND = key("marshmallow_island");
    public static final ResourceKey<Feature> FROSTING_PATCH = key("frosting_patch");

    private ModConfiguredFeatures() {
    }

    public static ResourceKey<Feature> key(String name) {
        return ResourceKey.create(Registries.FEATURE, Constants.id(name));
    }
}
