package com.mental.christmasgrove.registry;

import com.mental.christmasgrove.platform.Services;
import com.mental.christmasgrove.worldgen.GiantCandyCaneFeature;
import com.mental.christmasgrove.worldgen.MarshmallowIslandFeature;
import com.mental.christmasgrove.worldgen.StarTopDecorator;
import net.minecraft.core.registries.BuiltInRegistries;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.function.Supplier;

/** Code-defined worldgen types (tree decorators, features) that data files refer to by id. */
public final class ModWorldgenTypes {

    public static final Supplier<TreeDecoratorType<StarTopDecorator>> STAR_TOP = Services.REGISTRY.register(
            BuiltInRegistries.TREE_DECORATOR_TYPE, "star_top", () -> new TreeDecoratorType<>(StarTopDecorator.CODEC));

    // Since 26.x feature types are registered as codecs; the features themselves are data.
    public static final Supplier<MapCodec<? extends Feature>> GIANT_CANDY_CANE = Services.REGISTRY.register(
            BuiltInRegistries.FEATURE_TYPE, "giant_candy_cane", () -> GiantCandyCaneFeature.CODEC);

    public static final Supplier<MapCodec<? extends Feature>> MARSHMALLOW_ISLAND = Services.REGISTRY.register(
            BuiltInRegistries.FEATURE_TYPE, "marshmallow_island", () -> MarshmallowIslandFeature.CODEC);

    private ModWorldgenTypes() {
    }

    /** Forces class loading so the static fields above register. */
    public static void init() {
    }
}
