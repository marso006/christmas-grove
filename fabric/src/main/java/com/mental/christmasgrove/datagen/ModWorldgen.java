package com.mental.christmasgrove.datagen;

import com.mental.christmasgrove.registry.ModBlocks;
import com.mental.christmasgrove.worldgen.GiantCandyCaneFeature;
import com.mental.christmasgrove.worldgen.MarshmallowIslandFeature;
import com.mental.christmasgrove.worldgen.ModConfiguredFeatures;
import com.mental.christmasgrove.worldgen.ModPlacedFeatures;
import com.mental.christmasgrove.worldgen.StarTopDecorator;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.DiskFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.OffsetPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Features and placed features, built in Java so mistakes are compile errors rather than worldgen
 * crashes. Datagen serialises them to data/christmas_grove/worldgen/.
 *
 * <p>Since Minecraft 26.x a feature is a self-contained record (no separate configuration), and the
 * old "random patch" feature is gone: a patch is a single-block feature whose placement adds a
 * number of tries, a triangular offset and a block filter.
 */
final class ModWorldgen {

    private ModWorldgen() {
    }

    static void features(BootstrapContext<Feature> context) {
        Holder<BlockStateProvider> gingerbreadSoil = BlockStateProvider.holderOf(ModBlocks.GINGERBREAD.get());

        // A striped trunk under a round ball of gumdrop leaves.
        context.register(ModConfiguredFeatures.CANDY_CANE_TREE, new TreeFeature.Builder(
                BlockStateProvider.of(ModBlocks.CANDY_CANE.log().get()),
                new StraightTrunkPlacer(5, 2, 0),
                BlockStateProvider.of(ModBlocks.CANDY_CANE.leaves().get()),
                new BlobFoliagePlacer(ConstantInt.of(3), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1),
                gingerbreadSoil)
                .ignoreVines()
                .build());

        // A Christmas tree: spruce shape, fairy lights mixed into the leaves, a star on top.
        context.register(ModConfiguredFeatures.FESTIVE_FIR_TREE, new TreeFeature.Builder(
                BlockStateProvider.of(ModBlocks.FESTIVE_FIR.log().get()),
                new StraightTrunkPlacer(6, 2, 1),
                new WeightedStateProvider(WeightedList.<BlockState>builder()
                        .add(ModBlocks.FESTIVE_FIR.leaves().get().defaultBlockState(), 14)
                        .add(ModBlocks.RED_LIT_FIR_LEAVES.get().defaultBlockState(), 2)
                        .add(ModBlocks.GOLD_LIT_FIR_LEAVES.get().defaultBlockState(), 2)
                        .add(ModBlocks.BLUE_LIT_FIR_LEAVES.get().defaultBlockState(), 2)),
                new SpruceFoliagePlacer(UniformInt.of(2, 3), UniformInt.of(0, 2), UniformInt.of(1, 2)),
                new TwoLayersFeatureSize(2, 0, 2),
                gingerbreadSoil)
                .decorators(List.of(StarTopDecorator.INSTANCE))
                .ignoreVines()
                .build());

        // Single blocks; their placements below scatter them into patches.
        context.register(ModConfiguredFeatures.SWEET_FLOWERS, new SimpleBlockFeature(new WeightedStateProvider(WeightedList.<BlockState>builder()
                .add(ModBlocks.LOLLIPOP_FLOWER.get().defaultBlockState(), 3)
                .add(ModBlocks.POINSETTIA.get().defaultBlockState(), 3)
                .add(ModBlocks.HOLLY.get().defaultBlockState(), 2))));
        context.register(ModConfiguredFeatures.SUGAR_GRASS, new SimpleBlockFeature(BlockStateProvider.of(ModBlocks.SUGAR_GRASS.get())));
        context.register(ModConfiguredFeatures.GUMDROP_BUSHES, new SimpleBlockFeature(BlockStateProvider.of(
                ModBlocks.GUMDROP_BUSH.get().defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3))));
        // A few wrapped presents left on the ground.
        context.register(ModConfiguredFeatures.GIFT_BOXES, new SimpleBlockFeature(new WeightedStateProvider(WeightedList.<BlockState>builder()
                .add(ModBlocks.RED_GIFT_BOX.get().defaultBlockState(), 1)
                .add(ModBlocks.GREEN_GIFT_BOX.get().defaultBlockState(), 1)
                .add(ModBlocks.BLUE_GIFT_BOX.get().defaultBlockState(), 1))));

        context.register(ModConfiguredFeatures.GIANT_CANDY_CANE, new GiantCandyCaneFeature());
        context.register(ModConfiguredFeatures.MARSHMALLOW_ISLAND, new MarshmallowIslandFeature());
        // Patches of pure white frosting on the Frosted Flats.
        context.register(ModConfiguredFeatures.FROSTING_PATCH, new DiskFeature(BlockStateProvider.holderOf(ModBlocks.FROSTING.get()),
                BlockPredicate.matchesBlocks(ModBlocks.FROSTED_GINGERBREAD.get()), UniformInt.of(2, 5), 1));

        Block stone = ModBlocks.PEPPERMINT_STONE.base().get();
        context.register(ModConfiguredFeatures.ORE_SUGAR_CRYSTAL,
                new OreFeature(new BlockMatchTest(stone), ModBlocks.SUGAR_CRYSTAL_ORE.get().defaultBlockState(), 7));
        context.register(ModConfiguredFeatures.ORE_CINNAMON,
                new OreFeature(new BlockMatchTest(stone), ModBlocks.CINNAMON_ORE.get().defaultBlockState(), 10));
    }

    static void placed(BootstrapContext<PlacedFeature> context) {
        HolderGetter<Feature> features = context.lookup(Registries.FEATURE);

        PlacementUtils.register(context, ModPlacedFeatures.CANDY_CANE_TREES, features.getOrThrow(ModConfiguredFeatures.CANDY_CANE_TREE),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1F, 1), ModBlocks.CANDY_CANE.sapling().get()));
        PlacementUtils.register(context, ModPlacedFeatures.FESTIVE_FIR_TREES, features.getOrThrow(ModConfiguredFeatures.FESTIVE_FIR_TREE),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(1, 0.5F, 1), ModBlocks.FESTIVE_FIR.sapling().get()));

        BlockPredicate onGingerbread = BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE,
                BlockPredicate.matchesBlocks(Direction.DOWN, ModBlocks.FROSTED_GINGERBREAD.get()));
        BlockPredicate onGround = BlockPredicate.allOf(BlockPredicate.ONLY_IN_AIR_PREDICATE,
                BlockPredicate.matchesBlocks(Direction.DOWN, ModBlocks.FROSTED_GINGERBREAD.get(), ModBlocks.FROSTING.get()));

        PlacementUtils.register(context, ModPlacedFeatures.SWEET_FLOWERS, features.getOrThrow(ModConfiguredFeatures.SWEET_FLOWERS),
                patch(RarityFilter.onAverageOnceEvery(2), 48, 7, 3, BlockPredicate.ONLY_IN_AIR_PREDICATE));
        PlacementUtils.register(context, ModPlacedFeatures.SUGAR_GRASS, features.getOrThrow(ModConfiguredFeatures.SUGAR_GRASS),
                patch(CountPlacement.of(3), 32, 7, 3, BlockPredicate.ONLY_IN_AIR_PREDICATE));
        PlacementUtils.register(context, ModPlacedFeatures.GUMDROP_BUSHES, features.getOrThrow(ModConfiguredFeatures.GUMDROP_BUSHES),
                patch(RarityFilter.onAverageOnceEvery(4), 96, 7, 3, onGingerbread));
        PlacementUtils.register(context, ModPlacedFeatures.GIFT_BOXES, features.getOrThrow(ModConfiguredFeatures.GIFT_BOXES),
                patch(RarityFilter.onAverageOnceEvery(6), 6, 5, 2, onGround));

        PlacementUtils.register(context, ModPlacedFeatures.ORE_SUGAR_CRYSTAL, features.getOrThrow(ModConfiguredFeatures.ORE_SUGAR_CRYSTAL),
                CountPlacement.of(10), InSquarePlacement.spread(),
                HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(64)), BiomeFilter.biome());
        PlacementUtils.register(context, ModPlacedFeatures.ORE_CINNAMON, features.getOrThrow(ModConfiguredFeatures.ORE_CINNAMON),
                CountPlacement.of(18), InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(160)), BiomeFilter.biome());
        PlacementUtils.register(context, ModPlacedFeatures.ORE_CINNAMON_EXTRA, features.getOrThrow(ModConfiguredFeatures.ORE_CINNAMON),
                CountPlacement.of(20), InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(60), VerticalAnchor.absolute(256)), BiomeFilter.biome());

        // Biome-specific densities of the same features
        PlacementUtils.register(context, ModPlacedFeatures.SPARSE_CANDY_CANE_TREES, features.getOrThrow(ModConfiguredFeatures.CANDY_CANE_TREE),
                VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(6), ModBlocks.CANDY_CANE.sapling().get()));
        PlacementUtils.register(context, ModPlacedFeatures.DENSE_FESTIVE_FIR_TREES, features.getOrThrow(ModConfiguredFeatures.FESTIVE_FIR_TREE),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(7, 0.1F, 2), ModBlocks.FESTIVE_FIR.sapling().get()));
        PlacementUtils.register(context, ModPlacedFeatures.SPARSE_FESTIVE_FIR_TREES, features.getOrThrow(ModConfiguredFeatures.FESTIVE_FIR_TREE),
                VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(3), ModBlocks.FESTIVE_FIR.sapling().get()));
        PlacementUtils.register(context, ModPlacedFeatures.GIFT_BOXES_COMMON, features.getOrThrow(ModConfiguredFeatures.GIFT_BOXES),
                patch(RarityFilter.onAverageOnceEvery(2), 6, 5, 2, onGround));
        PlacementUtils.register(context, ModPlacedFeatures.SUGAR_GRASS_DENSE, features.getOrThrow(ModConfiguredFeatures.SUGAR_GRASS),
                patch(CountPlacement.of(8), 32, 7, 3, BlockPredicate.ONLY_IN_AIR_PREDICATE));
        PlacementUtils.register(context, ModPlacedFeatures.GIANT_CANDY_CANES, features.getOrThrow(ModConfiguredFeatures.GIANT_CANDY_CANE),
                RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
        PlacementUtils.register(context, ModPlacedFeatures.MARSHMALLOW_ISLANDS, features.getOrThrow(ModConfiguredFeatures.MARSHMALLOW_ISLAND),
                RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
        PlacementUtils.register(context, ModPlacedFeatures.FROSTING_PATCHES, features.getOrThrow(ModConfiguredFeatures.FROSTING_PATCH),
                CountPlacement.of(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
    }

    /**
     * Where a patch goes (frequency, spread, surface, biome), then the patch itself: {@code tries}
     * attempts within a triangle of {@code xzSpread}/{@code ySpread}, each kept only if {@code filter}
     * passes. This is what the pre-26 random patch feature did internally.
     */
    private static List<PlacementModifier> patch(PlacementModifier frequency, int tries, int xzSpread, int ySpread, BlockPredicate filter) {
        List<PlacementModifier> modifiers = new ArrayList<>();
        modifiers.add(frequency);
        modifiers.add(InSquarePlacement.spread());
        modifiers.add(PlacementUtils.HEIGHTMAP);
        modifiers.add(BiomeFilter.biome());
        modifiers.add(CountPlacement.of(tries));
        modifiers.add(OffsetPlacement.ofTriangle(xzSpread, ySpread));
        modifiers.add(BlockPredicateFilter.forPredicate(filter));
        return modifiers;
    }
}
