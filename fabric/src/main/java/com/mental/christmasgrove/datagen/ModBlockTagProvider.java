package com.mental.christmasgrove.datagen;

import com.mental.christmasgrove.registry.ModBlocks;
import com.mental.christmasgrove.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        // Ground: shovel, and plantable (saplings and flowers check #dirt)
        valueLookupBuilder(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.FROSTING.get(), ModBlocks.FROSTED_GINGERBREAD.get(),
                ModBlocks.GINGERBREAD.get(), ModBlocks.SUGAR_SAND.get(), ModBlocks.MARSHMALLOW_BLOCK.get());
        // Frosting counts too, so trees and flowers also grow on iced high ground.
        valueLookupBuilder(BlockTags.DIRT).add(ModBlocks.FROSTED_GINGERBREAD.get(), ModBlocks.GINGERBREAD.get(),
                ModBlocks.FROSTING.get());
        valueLookupBuilder(BlockTags.SAND).add(ModBlocks.SUGAR_SAND.get());
        valueLookupBuilder(BlockTags.SNOW).add(ModBlocks.FROSTING.get());

        // Stone sets, ores: pickaxe
        var pickaxe = valueLookupBuilder(BlockTags.MINEABLE_WITH_PICKAXE);
        for (ModBlocks.StoneSet set : ModBlocks.stoneSets()) {
            pickaxe.add(set.base().get(), set.stairs().get(), set.slab().get());
            valueLookupBuilder(BlockTags.STAIRS).add(set.stairs().get());
            valueLookupBuilder(BlockTags.SLABS).add(set.slab().get());
            if (set.wall() != null) {
                pickaxe.add(set.wall().get());
                valueLookupBuilder(BlockTags.WALLS).add(set.wall().get());
            }
        }
        pickaxe.add(ModBlocks.SUGAR_CRYSTAL_ORE.get(), ModBlocks.CINNAMON_ORE.get(), ModBlocks.SUGAR_CRYSTAL_BLOCK.get());
        valueLookupBuilder(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.SUGAR_CRYSTAL_ORE.get(), ModBlocks.SUGAR_CRYSTAL_BLOCK.get());
        valueLookupBuilder(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.CINNAMON_ORE.get());
        valueLookupBuilder(ModTags.Blocks.SUGAR_CRYSTAL_ORES).add(ModBlocks.SUGAR_CRYSTAL_ORE.get());
        valueLookupBuilder(ModTags.Blocks.CINNAMON_ORES).add(ModBlocks.CINNAMON_ORE.get());

        // Woods
        var axe = valueLookupBuilder(BlockTags.MINEABLE_WITH_AXE);
        for (ModBlocks.WoodSet wood : ModBlocks.woodSets()) {
            var logs = valueLookupBuilder(ModTags.Blocks.logs(wood.name()))
                    .add(wood.log().get(), wood.wood().get(), wood.strippedLog().get(), wood.strippedWood().get());
            valueLookupBuilder(BlockTags.LOGS).addTag(ModTags.Blocks.logs(wood.name()));
            valueLookupBuilder(BlockTags.OVERWORLD_NATURAL_LOGS).add(wood.log().get());
            axe.addTag(ModTags.Blocks.logs(wood.name()));
            for (Block b : new Block[]{wood.planks().get(), wood.stairs().get(), wood.slab().get(), wood.fence().get(),
                    wood.fenceGate().get(), wood.door().get(), wood.trapdoor().get(), wood.button().get(),
                    wood.pressurePlate().get()}) {
                axe.add(b);
            }
            valueLookupBuilder(BlockTags.PLANKS).add(wood.planks().get());
            valueLookupBuilder(BlockTags.WOODEN_STAIRS).add(wood.stairs().get());
            valueLookupBuilder(BlockTags.WOODEN_SLABS).add(wood.slab().get());
            valueLookupBuilder(BlockTags.WOODEN_FENCES).add(wood.fence().get());
            valueLookupBuilder(BlockTags.FENCE_GATES).add(wood.fenceGate().get());
            valueLookupBuilder(BlockTags.WOODEN_DOORS).add(wood.door().get());
            valueLookupBuilder(BlockTags.WOODEN_TRAPDOORS).add(wood.trapdoor().get());
            valueLookupBuilder(BlockTags.WOODEN_BUTTONS).add(wood.button().get());
            valueLookupBuilder(BlockTags.WOODEN_PRESSURE_PLATES).add(wood.pressurePlate().get());
            valueLookupBuilder(BlockTags.LEAVES).add(wood.leaves().get());
            valueLookupBuilder(BlockTags.MINEABLE_WITH_HOE).add(wood.leaves().get());
            valueLookupBuilder(BlockTags.SAPLINGS).add(wood.sapling().get());
        }
        for (Block lit : new Block[]{ModBlocks.RED_LIT_FIR_LEAVES.get(), ModBlocks.GOLD_LIT_FIR_LEAVES.get(),
                ModBlocks.BLUE_LIT_FIR_LEAVES.get()}) {
            valueLookupBuilder(BlockTags.LEAVES).add(lit);
            valueLookupBuilder(BlockTags.MINEABLE_WITH_HOE).add(lit);
        }

        // Plants
        valueLookupBuilder(BlockTags.SMALL_FLOWERS).add(ModBlocks.LOLLIPOP_FLOWER.get(), ModBlocks.POINSETTIA.get(),
                ModBlocks.HOLLY.get());
        valueLookupBuilder(BlockTags.SWORD_EFFICIENT).add(ModBlocks.GUMDROP_BUSH.get(), ModBlocks.SUGAR_GRASS.get());
        valueLookupBuilder(BlockTags.REPLACEABLE_BY_TREES).add(ModBlocks.SUGAR_GRASS.get());
        valueLookupBuilder(BlockTags.REPLACEABLE).add(ModBlocks.SUGAR_GRASS.get());

        valueLookupBuilder(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.ADVENT_CALENDAR.get());

        // Glass
        valueLookupBuilder(BlockTags.IMPERMEABLE).add(ModBlocks.CANDY_GLASS.get());
    }
}
