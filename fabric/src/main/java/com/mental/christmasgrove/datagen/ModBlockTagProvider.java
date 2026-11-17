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
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.FROSTING.get(), ModBlocks.FROSTED_GINGERBREAD.get(),
                ModBlocks.GINGERBREAD.get(), ModBlocks.SUGAR_SAND.get(), ModBlocks.MARSHMALLOW_BLOCK.get());
        getOrCreateTagBuilder(BlockTags.DIRT).add(ModBlocks.FROSTED_GINGERBREAD.get(), ModBlocks.GINGERBREAD.get());
        getOrCreateTagBuilder(BlockTags.SAND).add(ModBlocks.SUGAR_SAND.get());
        getOrCreateTagBuilder(BlockTags.SNOW).add(ModBlocks.FROSTING.get());

        // Stone sets, ores: pickaxe
        var pickaxe = getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE);
        for (ModBlocks.StoneSet set : ModBlocks.stoneSets()) {
            pickaxe.add(set.base().get(), set.stairs().get(), set.slab().get());
            getOrCreateTagBuilder(BlockTags.STAIRS).add(set.stairs().get());
            getOrCreateTagBuilder(BlockTags.SLABS).add(set.slab().get());
            if (set.wall() != null) {
                pickaxe.add(set.wall().get());
                getOrCreateTagBuilder(BlockTags.WALLS).add(set.wall().get());
            }
        }
        pickaxe.add(ModBlocks.SUGAR_CRYSTAL_ORE.get(), ModBlocks.CINNAMON_ORE.get(), ModBlocks.SUGAR_CRYSTAL_BLOCK.get());
        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.SUGAR_CRYSTAL_ORE.get(), ModBlocks.SUGAR_CRYSTAL_BLOCK.get());
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.CINNAMON_ORE.get());
        getOrCreateTagBuilder(ModTags.Blocks.SUGAR_CRYSTAL_ORES).add(ModBlocks.SUGAR_CRYSTAL_ORE.get());
        getOrCreateTagBuilder(ModTags.Blocks.CINNAMON_ORES).add(ModBlocks.CINNAMON_ORE.get());

        // Woods
        var axe = getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE);
        for (ModBlocks.WoodSet wood : ModBlocks.woodSets()) {
            var logs = getOrCreateTagBuilder(ModTags.Blocks.logs(wood.name()))
                    .add(wood.log().get(), wood.wood().get(), wood.strippedLog().get(), wood.strippedWood().get());
            getOrCreateTagBuilder(BlockTags.LOGS).addTag(ModTags.Blocks.logs(wood.name()));
            getOrCreateTagBuilder(BlockTags.OVERWORLD_NATURAL_LOGS).add(wood.log().get());
            axe.addTag(ModTags.Blocks.logs(wood.name()));
            for (Block b : new Block[]{wood.planks().get(), wood.stairs().get(), wood.slab().get(), wood.fence().get(),
                    wood.fenceGate().get(), wood.door().get(), wood.trapdoor().get(), wood.button().get(),
                    wood.pressurePlate().get()}) {
                axe.add(b);
            }
            getOrCreateTagBuilder(BlockTags.PLANKS).add(wood.planks().get());
            getOrCreateTagBuilder(BlockTags.WOODEN_STAIRS).add(wood.stairs().get());
            getOrCreateTagBuilder(BlockTags.WOODEN_SLABS).add(wood.slab().get());
            getOrCreateTagBuilder(BlockTags.WOODEN_FENCES).add(wood.fence().get());
            getOrCreateTagBuilder(BlockTags.FENCE_GATES).add(wood.fenceGate().get());
            getOrCreateTagBuilder(BlockTags.WOODEN_DOORS).add(wood.door().get());
            getOrCreateTagBuilder(BlockTags.WOODEN_TRAPDOORS).add(wood.trapdoor().get());
            getOrCreateTagBuilder(BlockTags.WOODEN_BUTTONS).add(wood.button().get());
            getOrCreateTagBuilder(BlockTags.WOODEN_PRESSURE_PLATES).add(wood.pressurePlate().get());
            getOrCreateTagBuilder(BlockTags.LEAVES).add(wood.leaves().get());
            getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_HOE).add(wood.leaves().get());
            getOrCreateTagBuilder(BlockTags.SAPLINGS).add(wood.sapling().get());
        }
        for (Block lit : new Block[]{ModBlocks.RED_LIT_FIR_LEAVES.get(), ModBlocks.GOLD_LIT_FIR_LEAVES.get(),
                ModBlocks.BLUE_LIT_FIR_LEAVES.get()}) {
            getOrCreateTagBuilder(BlockTags.LEAVES).add(lit);
            getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_HOE).add(lit);
        }

        // Plants
        getOrCreateTagBuilder(BlockTags.SMALL_FLOWERS).add(ModBlocks.LOLLIPOP_FLOWER.get(), ModBlocks.POINSETTIA.get(),
                ModBlocks.HOLLY.get());
        getOrCreateTagBuilder(BlockTags.SWORD_EFFICIENT).add(ModBlocks.GUMDROP_BUSH.get(), ModBlocks.SUGAR_GRASS.get());
        getOrCreateTagBuilder(BlockTags.REPLACEABLE_BY_TREES).add(ModBlocks.SUGAR_GRASS.get());
        getOrCreateTagBuilder(BlockTags.REPLACEABLE).add(ModBlocks.SUGAR_GRASS.get());

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.ADVENT_CALENDAR.get());

        // Glass
        getOrCreateTagBuilder(BlockTags.IMPERMEABLE).add(ModBlocks.CANDY_GLASS.get());
    }
}
