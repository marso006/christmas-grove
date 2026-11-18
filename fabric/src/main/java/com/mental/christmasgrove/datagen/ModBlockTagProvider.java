package com.mental.christmasgrove.datagen;

import net.minecraft.resources.ResourceKey;
import com.mental.christmasgrove.registry.ModBlocks;
import com.mental.christmasgrove.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {

    public ModBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        // Ground: shovel, and plantable (saplings and flowers check #dirt)
        builder(BlockTags.MINEABLE_WITH_SHOVEL).add(key(ModBlocks.FROSTING.get()), key(ModBlocks.FROSTED_GINGERBREAD.get()),
                key(ModBlocks.GINGERBREAD.get()), key(ModBlocks.SUGAR_SAND.get()), key(ModBlocks.MARSHMALLOW_BLOCK.get()));
        // Frosting counts too, so trees and flowers also grow on iced high ground.
        builder(BlockTags.DIRT).add(key(ModBlocks.FROSTED_GINGERBREAD.get()), key(ModBlocks.GINGERBREAD.get()),
                key(ModBlocks.FROSTING.get()));
        builder(BlockTags.SAND).add(key(ModBlocks.SUGAR_SAND.get()));
        builder(BlockTags.SNOW).add(key(ModBlocks.FROSTING.get()));

        // Stone sets, ores: pickaxe
        var pickaxe = builder(BlockTags.MINEABLE_WITH_PICKAXE);
        for (ModBlocks.StoneSet set : ModBlocks.stoneSets()) {
            pickaxe.add(key(set.base().get()), key(set.stairs().get()), key(set.slab().get()));
            builder(BlockTags.STAIRS).add(key(set.stairs().get()));
            builder(BlockTags.SLABS).add(key(set.slab().get()));
            if (set.wall() != null) {
                pickaxe.add(key(set.wall().get()));
                builder(BlockTags.WALLS).add(key(set.wall().get()));
            }
        }
        pickaxe.add(key(ModBlocks.SUGAR_CRYSTAL_ORE.get()), key(ModBlocks.CINNAMON_ORE.get()), key(ModBlocks.SUGAR_CRYSTAL_BLOCK.get()));
        builder(BlockTags.NEEDS_IRON_TOOL).add(key(ModBlocks.SUGAR_CRYSTAL_ORE.get()), key(ModBlocks.SUGAR_CRYSTAL_BLOCK.get()));
        builder(BlockTags.NEEDS_STONE_TOOL).add(key(ModBlocks.CINNAMON_ORE.get()));
        builder(ModTags.Blocks.SUGAR_CRYSTAL_ORES).add(key(ModBlocks.SUGAR_CRYSTAL_ORE.get()));
        builder(ModTags.Blocks.CINNAMON_ORES).add(key(ModBlocks.CINNAMON_ORE.get()));

        // Woods
        var axe = builder(BlockTags.MINEABLE_WITH_AXE);
        for (ModBlocks.WoodSet wood : ModBlocks.woodSets()) {
            var logs = builder(ModTags.Blocks.logs(wood.name()))
                    .add(key(wood.log().get()), key(wood.wood().get()), key(wood.strippedLog().get()), key(wood.strippedWood().get()));
            builder(BlockTags.LOGS).addTag(ModTags.Blocks.logs(wood.name()));
            builder(BlockTags.OVERWORLD_NATURAL_LOGS).add(key(wood.log().get()));
            axe.addTag(ModTags.Blocks.logs(wood.name()));
            for (Block b : new Block[]{wood.planks().get(), wood.stairs().get(), wood.slab().get(), wood.fence().get(),
                    wood.fenceGate().get(), wood.door().get(), wood.trapdoor().get(), wood.button().get(),
                    wood.pressurePlate().get()}) {
                axe.add(key(b));
            }
            builder(BlockTags.PLANKS).add(key(wood.planks().get()));
            builder(BlockTags.WOODEN_STAIRS).add(key(wood.stairs().get()));
            builder(BlockTags.WOODEN_SLABS).add(key(wood.slab().get()));
            builder(BlockTags.WOODEN_FENCES).add(key(wood.fence().get()));
            builder(BlockTags.FENCE_GATES).add(key(wood.fenceGate().get()));
            builder(BlockTags.WOODEN_DOORS).add(key(wood.door().get()));
            builder(BlockTags.WOODEN_TRAPDOORS).add(key(wood.trapdoor().get()));
            builder(BlockTags.WOODEN_BUTTONS).add(key(wood.button().get()));
            builder(BlockTags.WOODEN_PRESSURE_PLATES).add(key(wood.pressurePlate().get()));
            builder(BlockTags.LEAVES).add(key(wood.leaves().get()));
            builder(BlockTags.MINEABLE_WITH_HOE).add(key(wood.leaves().get()));
            builder(BlockTags.SAPLINGS).add(key(wood.sapling().get()));
        }
        for (Block lit : new Block[]{ModBlocks.RED_LIT_FIR_LEAVES.get(), ModBlocks.GOLD_LIT_FIR_LEAVES.get(),
                ModBlocks.BLUE_LIT_FIR_LEAVES.get()}) {
            builder(BlockTags.LEAVES).add(key(lit));
            builder(BlockTags.MINEABLE_WITH_HOE).add(key(lit));
        }

        // Plants
        builder(BlockTags.SMALL_FLOWERS).add(key(ModBlocks.LOLLIPOP_FLOWER.get()), key(ModBlocks.POINSETTIA.get()),
                key(ModBlocks.HOLLY.get()));
        builder(BlockTags.SWORD_EFFICIENT).add(key(ModBlocks.GUMDROP_BUSH.get()), key(ModBlocks.SUGAR_GRASS.get()));
        builder(BlockTags.REPLACEABLE_BY_TREES).add(key(ModBlocks.SUGAR_GRASS.get()));
        builder(BlockTags.REPLACEABLE).add(key(ModBlocks.SUGAR_GRASS.get()));

        builder(BlockTags.MINEABLE_WITH_AXE).add(key(ModBlocks.ADVENT_CALENDAR.get()));

        // Glass
        builder(BlockTags.IMPERMEABLE).add(key(ModBlocks.CANDY_GLASS.get()));
    }

    /** Since 26.x tag builders take registry keys. */
    private static ResourceKey<Block> key(Block block) {
        return block.builtInRegistryHolder().key();
    }
}
