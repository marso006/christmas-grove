package com.mental.gingerbreadgrove.datagen;

import com.mental.gingerbreadgrove.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModBlockLootProvider extends FabricBlockLootTableProvider {

    public ModBlockLootProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void generate() {
        dropSelf(ModBlocks.FROSTING.get());
        dropSelf(ModBlocks.GINGERBREAD.get());
        // Like grass, the frosted top is lost unless mined with Silk Touch.
        add(ModBlocks.FROSTED_GINGERBREAD.get(), block -> createSingleItemTableWithSilkTouch(block, ModBlocks.GINGERBREAD.get()));
        dropSelf(ModBlocks.PEPPERMINT_STONE.get());
    }
}
