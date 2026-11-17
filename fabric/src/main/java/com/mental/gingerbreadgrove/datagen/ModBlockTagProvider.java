package com.mental.gingerbreadgrove.datagen;

import com.mental.gingerbreadgrove.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.FROSTING.get(), ModBlocks.FROSTED_GINGERBREAD.get(), ModBlocks.GINGERBREAD.get());
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.PEPPERMINT_STONE.get());
        // Lets saplings and flowers from this mod (and vanilla) be planted on the biome's ground.
        getOrCreateTagBuilder(BlockTags.DIRT)
                .add(ModBlocks.FROSTED_GINGERBREAD.get(), ModBlocks.GINGERBREAD.get());
    }
}
