package com.mental.gingerbreadgrove.datagen;

import com.mental.gingerbreadgrove.registry.ModBlocks;
import com.mental.gingerbreadgrove.worldgen.ModConfiguredFeatures;
import com.mental.gingerbreadgrove.worldgen.ModPlacedFeatures;
import com.mental.gingerbreadgrove.worldgen.StarTopDecorator;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;

import java.util.List;

/**
 * Configured and placed features, built in Java so mistakes are compile errors rather than
 * worldgen crashes. Datagen serialises them to data/gingerbread_grove/worldgen/.
 */
final class ModWorldgen {

    private ModWorldgen() {
    }

    static void configured(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        // A striped trunk under a round ball of gumdrop leaves.
        FeatureUtils.register(context, ModConfiguredFeatures.CANDY_CANE_TREE, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(ModBlocks.CANDY_CANE.log().get()),
                        new StraightTrunkPlacer(5, 2, 0),
                        BlockStateProvider.simple(ModBlocks.CANDY_CANE.leaves().get()),
                        new BlobFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), 3),
                        new TwoLayersFeatureSize(1, 0, 1))
                        .dirt(BlockStateProvider.simple(ModBlocks.GINGERBREAD.get()))
                        .ignoreVines()
                        .build());

        // A Christmas tree: spruce shape, fairy lights mixed into the leaves, a star on top.
        FeatureUtils.register(context, ModConfiguredFeatures.FESTIVE_FIR_TREE, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(ModBlocks.FESTIVE_FIR.log().get()),
                        new StraightTrunkPlacer(6, 2, 1),
                        new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                                .add(ModBlocks.FESTIVE_FIR.leaves().get().defaultBlockState(), 14)
                                .add(ModBlocks.RED_LIT_FIR_LEAVES.get().defaultBlockState(), 2)
                                .add(ModBlocks.GOLD_LIT_FIR_LEAVES.get().defaultBlockState(), 2)
                                .add(ModBlocks.BLUE_LIT_FIR_LEAVES.get().defaultBlockState(), 2)),
                        new SpruceFoliagePlacer(UniformInt.of(2, 3), UniformInt.of(0, 2), UniformInt.of(1, 2)),
                        new TwoLayersFeatureSize(2, 0, 2))
                        .dirt(BlockStateProvider.simple(ModBlocks.GINGERBREAD.get()))
                        .decorators(List.of(StarTopDecorator.INSTANCE))
                        .ignoreVines()
                        .build());

        FeatureUtils.register(context, ModConfiguredFeatures.SWEET_FLOWERS, Feature.FLOWER,
                FeatureUtils.simpleRandomPatchConfiguration(48, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                                .add(ModBlocks.LOLLIPOP_FLOWER.get().defaultBlockState(), 3)
                                .add(ModBlocks.POINSETTIA.get().defaultBlockState(), 3)
                                .add(ModBlocks.HOLLY.get().defaultBlockState(), 2))))));

        FeatureUtils.register(context, ModConfiguredFeatures.SUGAR_GRASS, Feature.RANDOM_PATCH,
                FeatureUtils.simpleRandomPatchConfiguration(32, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.SUGAR_GRASS.get())))));

        FeatureUtils.register(context, ModConfiguredFeatures.GUMDROP_BUSHES, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ModBlocks.GUMDROP_BUSH.get().defaultBlockState()
                                .setValue(SweetBerryBushBlock.AGE, 3))),
                        List.of(ModBlocks.FROSTED_GINGERBREAD.get())));

        // A few wrapped presents left on the ground, never on leaves or in water.
        FeatureUtils.register(context, ModConfiguredFeatures.GIFT_BOXES, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(6, 5, 2, PlacementUtils.filtered(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
                                .add(ModBlocks.RED_GIFT_BOX.get().defaultBlockState(), 1)
                                .add(ModBlocks.GREEN_GIFT_BOX.get().defaultBlockState(), 1)
                                .add(ModBlocks.BLUE_GIFT_BOX.get().defaultBlockState(), 1))),
                        BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE,
                                BlockPredicate.matchesBlocks(Direction.DOWN.getNormal(),
                                        ModBlocks.FROSTED_GINGERBREAD.get(), ModBlocks.FROSTING.get())))));

        Block stone = ModBlocks.PEPPERMINT_STONE.base().get();
        FeatureUtils.register(context, ModConfiguredFeatures.ORE_SUGAR_CRYSTAL, Feature.ORE,
                new OreConfiguration(new BlockMatchTest(stone), ModBlocks.SUGAR_CRYSTAL_ORE.get().defaultBlockState(), 7));
        FeatureUtils.register(context, ModConfiguredFeatures.ORE_CINNAMON, Feature.ORE,
                new OreConfiguration(new BlockMatchTest(stone), ModBlocks.CINNAMON_ORE.get().defaultBlockState(), 10));
    }

    static void placed(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> features = context.lookup(net.minecraft.core.registries.Registries.CONFIGURED_FEATURE);

        PlacementUtils.register(context, ModPlacedFeatures.CANDY_CANE_TREES, features.getOrThrow(ModConfiguredFeatures.CANDY_CANE_TREE),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1F, 1), ModBlocks.CANDY_CANE.sapling().get()));
        PlacementUtils.register(context, ModPlacedFeatures.FESTIVE_FIR_TREES, features.getOrThrow(ModConfiguredFeatures.FESTIVE_FIR_TREE),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(1, 0.5F, 1), ModBlocks.FESTIVE_FIR.sapling().get()));

        PlacementUtils.register(context, ModPlacedFeatures.SWEET_FLOWERS, features.getOrThrow(ModConfiguredFeatures.SWEET_FLOWERS),
                RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register(context, ModPlacedFeatures.SUGAR_GRASS, features.getOrThrow(ModConfiguredFeatures.SUGAR_GRASS),
                CountPlacement.of(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register(context, ModPlacedFeatures.GUMDROP_BUSHES, features.getOrThrow(ModConfiguredFeatures.GUMDROP_BUSHES),
                RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register(context, ModPlacedFeatures.GIFT_BOXES, features.getOrThrow(ModConfiguredFeatures.GIFT_BOXES),
                RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());

        PlacementUtils.register(context, ModPlacedFeatures.ORE_SUGAR_CRYSTAL, features.getOrThrow(ModConfiguredFeatures.ORE_SUGAR_CRYSTAL),
                CountPlacement.of(10), InSquarePlacement.spread(),
                HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(64)), BiomeFilter.biome());
        PlacementUtils.register(context, ModPlacedFeatures.ORE_CINNAMON, features.getOrThrow(ModConfiguredFeatures.ORE_CINNAMON),
                CountPlacement.of(18), InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(160)), BiomeFilter.biome());
    }
}
