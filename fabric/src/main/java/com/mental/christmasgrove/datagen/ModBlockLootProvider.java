package com.mental.christmasgrove.datagen;

import com.mental.christmasgrove.registry.ModBlocks;
import com.mental.christmasgrove.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
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
        for (Supplier<Block> box : java.util.List.of(ModBlocks.RED_GIFT_BOX, ModBlocks.GREEN_GIFT_BOX, ModBlocks.BLUE_GIFT_BOX)) {
            special(box, b -> add(b, LootTable.lootTable().withPool(applyExplosionCondition(b, LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(b).apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                            .include(DataComponents.CONTAINER)
                            .include(DataComponents.CUSTOM_DATA)))))));
        }

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
