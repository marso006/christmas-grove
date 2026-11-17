package com.mental.gingerbreadgrove.registry;

import com.mental.gingerbreadgrove.platform.Services;
import com.mental.gingerbreadgrove.worldgen.GiantCandyCaneFeature;
import com.mental.gingerbreadgrove.worldgen.MarshmallowIslandFeature;
import com.mental.gingerbreadgrove.worldgen.StarTopDecorator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.function.Supplier;

/** Code-defined worldgen types (tree decorators, features) that data files refer to by id. */
public final class ModWorldgenTypes {

    public static final Supplier<TreeDecoratorType<StarTopDecorator>> STAR_TOP = Services.REGISTRY.register(
            BuiltInRegistries.TREE_DECORATOR_TYPE, "star_top", () -> new TreeDecoratorType<>(StarTopDecorator.CODEC));

    public static final Supplier<GiantCandyCaneFeature> GIANT_CANDY_CANE = Services.REGISTRY.register(
            BuiltInRegistries.FEATURE, "giant_candy_cane", () -> new GiantCandyCaneFeature(NoneFeatureConfiguration.CODEC));

    public static final Supplier<MarshmallowIslandFeature> MARSHMALLOW_ISLAND = Services.REGISTRY.register(
            BuiltInRegistries.FEATURE, "marshmallow_island", () -> new MarshmallowIslandFeature(NoneFeatureConfiguration.CODEC));

    private ModWorldgenTypes() {
    }

    /** Forces class loading so the static fields above register. */
    public static void init() {
    }
}
