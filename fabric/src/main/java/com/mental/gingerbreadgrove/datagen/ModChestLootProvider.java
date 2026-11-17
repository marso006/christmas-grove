package com.mental.gingerbreadgrove.datagen;

import com.mental.gingerbreadgrove.registry.ModBlocks;
import com.mental.gingerbreadgrove.registry.ModItems;
import com.mental.gingerbreadgrove.registry.ModLootTables;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/** Loot tables rolled by code or by chests: gift boxes now, structure chests later. */
public class ModChestLootProvider extends SimpleFabricLootTableProvider {

    public ModChestLootProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, LootContextParamSets.CHEST);
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(ModLootTables.GIFT_BOX, LootTable.lootTable()
                // Two to four treats and festive bits...
                .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(2, 4))
                        .add(item(ModItems.GUMDROPS.get(), 20, 2, 6))
                        .add(item(ModItems.CANDY_CANE.get(), 20, 1, 3))
                        .add(item(ModItems.GINGERBREAD_COOKIE.get(), 20, 2, 5))
                        .add(item(ModItems.SUGAR_COOKIE.get(), 15, 2, 5))
                        .add(item(ModItems.MARSHMALLOW.get(), 15, 2, 4))
                        .add(item(ModItems.HOT_COCOA.get(), 8, 1, 1))
                        .add(item(ModItems.EGGNOG.get(), 8, 1, 1))
                        .add(item(ModItems.FRUITCAKE_SLICE.get(), 8, 1, 2))
                        .add(item(ModBlocks.FESTIVE_LIGHTS.get(), 12, 2, 6))
                        .add(item(ModBlocks.WREATH.get(), 6, 1, 1))
                        .add(item(Items.SNOWBALL, 10, 4, 16))
                        .add(item(ModItems.CINNAMON_STICK.get(), 10, 2, 4)))
                // ...plus one "real" present, sometimes a very good one.
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(item(ModItems.SUGAR_CRYSTAL.get(), 30, 1, 3))
                        .add(item(Items.EMERALD, 20, 1, 3))
                        .add(item(Items.GOLD_INGOT, 20, 1, 3))
                        .add(item(Items.DIAMOND, 6, 1, 1))
                        .add(item(Items.GOLDEN_APPLE, 6, 1, 1))
                        .add(item(Items.NAME_TAG, 5, 1, 1))
                        .add(item(Items.MUSIC_DISC_CAT, 2, 1, 1))
                        .add(item(ModItems.GINGERBREAD_HEART.get(), 3, 1, 1))
                        .add(item(Items.ENCHANTED_GOLDEN_APPLE, 1, 1, 1))));
    }

    private static LootPoolEntryContainer.Builder<?> item(ItemLike item, int weight, int min, int max) {
        var entry = LootItem.lootTableItem(item).setWeight(weight);
        if (min != 1 || max != 1) {
            entry.apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)));
        }
        return entry;
    }
}
