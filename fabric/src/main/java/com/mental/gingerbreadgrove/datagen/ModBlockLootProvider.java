package com.mental.gingerbreadgrove.datagen;

import com.mental.gingerbreadgrove.registry.ModBlocks;
import com.mental.gingerbreadgrove.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/** Block drops. Anything without a special rule below simply drops itself. */
public class ModBlockLootProvider extends FabricBlockLootTableProvider {

    private final Set<Block> handled = new HashSet<>();

    public ModBlockLootProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void generate() {
        // Like grass, the frosted top is lost unless mined with Silk Touch.
        special(ModBlocks.FROSTED_GINGERBREAD, b -> add(b, createSingleItemTableWithSilkTouch(b, ModBlocks.GINGERBREAD.get())));

        for (ModBlocks.StoneSet set : ModBlocks.stoneSets()) {
            special(set.slab(), b -> add(b, createSlabItemTable(b)));
        }
        for (ModBlocks.WoodSet wood : ModBlocks.woodSets()) {
            special(wood.slab(), b -> add(b, createSlabItemTable(b)));
            special(wood.door(), b -> add(b, createDoorTable(b)));
            special(wood.leaves(), b -> add(b, createLeavesDrops(b, wood.sapling().get(), NORMAL_LEAVES_SAPLING_CHANCES)));
        }
        Block firSapling = ModBlocks.FESTIVE_FIR.sapling().get();
        special(ModBlocks.RED_LIT_FIR_LEAVES, b -> add(b, createLeavesDrops(b, firSapling, NORMAL_LEAVES_SAPLING_CHANCES)));
        special(ModBlocks.GOLD_LIT_FIR_LEAVES, b -> add(b, createLeavesDrops(b, firSapling, NORMAL_LEAVES_SAPLING_CHANCES)));
        special(ModBlocks.BLUE_LIT_FIR_LEAVES, b -> add(b, createLeavesDrops(b, firSapling, NORMAL_LEAVES_SAPLING_CHANCES)));

        special(ModBlocks.CANDY_GLASS, this::dropWhenSilkTouch);
        special(ModBlocks.CANDY_GLASS_PANE, this::dropWhenSilkTouch);
        special(ModBlocks.SUGAR_GRASS, b -> add(b, createShearsOnlyDrop(b)));
        special(ModBlocks.GUMDROP_BUSH, b -> add(b, createSingleItemTable(ModItems.GUMDROPS.get())));
        special(ModBlocks.SUGAR_CRYSTAL_ORE, b -> add(b, createOreDrop(b, ModItems.SUGAR_CRYSTAL.get())));
        special(ModBlocks.CINNAMON_ORE, b -> add(b, createOreDrop(b, ModItems.CINNAMON_STICK.get())));

        for (Supplier<? extends Block> block : ModBlocks.all()) {
            if (!handled.contains(block.get())) {
                dropSelf(block.get());
            }
        }
    }

    private void special(Supplier<? extends Block> block, java.util.function.Consumer<Block> rule) {
        handled.add(block.get());
        rule.accept(block.get());
    }
}
