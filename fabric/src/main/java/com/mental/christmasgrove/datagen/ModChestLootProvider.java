package com.mental.christmasgrove.datagen;

import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import com.mental.christmasgrove.block.GiftBoxBlock;
import com.mental.christmasgrove.block.entity.GiftBoxBlockEntity;
import com.mental.christmasgrove.registry.ModBlocks;
import com.mental.christmasgrove.registry.ModItems;
import com.mental.christmasgrove.registry.ModLootTables;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.loot.functions.SetCustomDataFunction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/** Loot tables rolled by code or by chests: gift boxes now, structure chests later. */
public class ModChestLootProvider extends SimpleFabricLootTableSubProvider {

    public ModChestLootProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, LootContextParamSets.CHEST);
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(ModLootTables.GIFT_BOX, LootTable.lootTable()
                // Two to four treats and festive bits...
                .withPool(LootPool.lootPool().setRolls(ContextIntProviders.between(2, 4))
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
                .withPool(LootPool.lootPool().setRolls(ContextIntProviders.exactly(1))
                        .add(item(ModItems.SUGAR_CRYSTAL.get(), 30, 1, 3))
                        .add(item(Items.EMERALD, 20, 1, 3))
                        .add(item(Items.GOLD_INGOT, 20, 1, 3))
                        .add(item(Items.DIAMOND, 6, 1, 1))
                        .add(item(Items.GOLDEN_APPLE, 6, 1, 1))
                        .add(item(Items.NAME_TAG, 5, 1, 1))
                        .add(item(Items.MUSIC_DISC_CAT, 2, 1, 1))
                        .add(item(ModItems.GINGERBREAD_HEART.get(), 3, 1, 1))
                        .add(item(Items.ENCHANTED_GOLDEN_APPLE, 1, 1, 1))));

        // A cosy home: food, sweets and building bits.
        output.accept(ModLootTables.GINGERBREAD_HOUSE, LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ContextIntProviders.between(4, 7))
                        .add(item(ModItems.GINGERBREAD_COOKIE.get(), 20, 2, 6))
                        .add(item(ModItems.SUGAR_COOKIE.get(), 15, 2, 6))
                        .add(item(ModItems.HOT_COCOA.get(), 10, 1, 2))
                        .add(item(ModItems.CANDY_CANE.get(), 15, 1, 4))
                        .add(item(ModItems.CINNAMON_STICK.get(), 12, 1, 4))
                        .add(item(ModItems.SUGAR_CRYSTAL.get(), 8, 1, 3))
                        .add(item(ModBlocks.FESTIVE_LIGHTS.get(), 10, 2, 8))
                        .add(item(ModItems.MUG.get(), 6, 1, 2))
                        .add(item(Items.EMERALD, 6, 1, 3))
                        .add(item(ModBlocks.CANDY_CANE.sapling().get(), 6, 1, 2))
                        .add(item(ModItems.SNOW_GLOBE.get(), 2, 1, 1))));

        // Santa's own stores: the best presents and Santa Suit pieces.
        output.accept(ModLootTables.SANTAS_WORKSHOP, LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ContextIntProviders.between(3, 6))
                        .add(item(ModBlocks.RED_GIFT_BOX.get(), 15, 1, 2))
                        .add(item(ModBlocks.GREEN_GIFT_BOX.get(), 15, 1, 2))
                        .add(item(ModBlocks.BLUE_GIFT_BOX.get(), 15, 1, 2))
                        .add(item(ModItems.SUGAR_CRYSTAL.get(), 15, 2, 5))
                        .add(item(ModItems.EGGNOG.get(), 10, 1, 2))
                        .add(item(ModBlocks.CHRISTMAS_STAR.get(), 6, 1, 1))
                        .add(item(ModBlocks.FESTIVE_FIR.sapling().get(), 8, 1, 3))
                        .add(item(Items.DIAMOND, 4, 1, 2)))
                .withPool(LootPool.lootPool().setRolls(ContextIntProviders.between(0, 1))
                        .add(item(ModItems.SANTA_HAT.get(), 1, 1, 1))
                        .add(item(ModItems.SANTA_COAT.get(), 1, 1, 1))
                        .add(item(ModItems.SANTA_TROUSERS.get(), 1, 1, 1))
                        .add(item(ModItems.SANTA_BOOTS.get(), 1, 1, 1))));

        // The guarded tower: weapons, crystals and treasure.
        output.accept(ModLootTables.CANDY_CANE_OUTPOST, LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ContextIntProviders.between(4, 8))
                        .add(item(ModItems.SUGAR_CRYSTAL.get(), 20, 2, 6))
                        .add(item(ModItems.CINNAMON_STICK.get(), 15, 2, 6))
                        .add(item(ModItems.CANDY_CANE.get(), 15, 2, 5))
                        .add(item(Items.IRON_INGOT, 12, 2, 5))
                        .add(item(Items.GOLD_INGOT, 10, 2, 4))
                        .add(item(Items.DIAMOND, 5, 1, 3))
                        .add(item(ModItems.GINGERBREAD_HEART.get(), 3, 1, 1)))
                .withPool(LootPool.lootPool().setRolls(ContextIntProviders.exactly(1))
                        .add(item(ModItems.CANDY_CANE_SWORD.get(), 3, 1, 1))
                        .add(item(ModItems.SUGAR_CRYSTAL_PICKAXE.get(), 3, 1, 1))
                        .add(item(ModItems.SUGAR_CRYSTAL_AXE.get(), 2, 1, 1))
                        .add(item(Items.GOLDEN_APPLE, 4, 1, 2))
                        .add(item(ModItems.KRAMPUS_BELL.get(), 1, 1, 1))));

        // What the snowmen have hoarded.
        output.accept(ModLootTables.SNOWMAN_CAMP, LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ContextIntProviders.between(3, 5))
                        .add(item(Items.SNOWBALL, 20, 4, 16))
                        .add(item(Items.COAL, 15, 2, 6))
                        .add(item(Items.CARROT, 12, 1, 4))
                        .add(item(ModItems.MARSHMALLOW.get(), 12, 2, 5))
                        .add(item(ModItems.HOT_COCOA.get(), 8, 1, 2))
                        .add(item(Items.PACKED_ICE, 8, 2, 6))
                        .add(item(ModItems.SUGAR_CRYSTAL.get(), 6, 1, 2))
                        .add(item(Items.LEATHER_BOOTS, 3, 1, 1))));
    }

    private static LootPoolEntryContainer.Builder<?> item(ItemLike item, int weight, int min, int max) {
        var entry = LootItem.lootTableItem(item).setWeight(weight);
        if (item.asItem() instanceof net.minecraft.world.item.BlockItem block && block.getBlock() instanceof GiftBoxBlock) {
            CompoundTag surprise = new CompoundTag();
            surprise.putBoolean(GiftBoxBlockEntity.SURPRISE_TAG, true);
            entry.apply(SetCustomDataFunction.setCustomData(surprise)); // found presents roll random loot
        }
        if (min != 1 || max != 1) {
            entry.apply(SetItemCountFunction.setCount(ContextIntProviders.between(min, max)));
        }
        return entry;
    }
}
