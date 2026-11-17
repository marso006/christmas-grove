package com.mental.gingerbreadgrove.datagen;

import com.mental.gingerbreadgrove.registry.ModEntities;
import com.mental.gingerbreadgrove.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/** What each mob drops when killed. */
public class ModEntityLootProvider extends SimpleFabricLootTableProvider {

    public ModEntityLootProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, LootContextParamSets.ENTITY);
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(key(ModEntities.GINGERBREAD_MAN.get()), LootTable.lootTable()
                .withPool(pool(ModItems.GINGERBREAD_COOKIE.get(), 1, 2))
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ModItems.GINGERBREAD_HEART.get()))
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                        .when(LootItemRandomChanceCondition.randomChance(0.05F))));
        output.accept(key(ModEntities.REINDEER.get()), LootTable.lootTable().withPool(pool(Items.LEATHER, 0, 2)));
        output.accept(key(ModEntities.COOKIE_SOLDIER.get()), LootTable.lootTable()
                .withPool(pool(ModItems.CINNAMON_STICK.get(), 0, 2))
                .withPool(pool(ModItems.SUGAR_COOKIE.get(), 0, 2)));
        output.accept(key(ModEntities.GUMDROP_BLOB.get()), LootTable.lootTable()
                .withPool(pool(ModItems.GUMDROPS.get(), 1, 3))
                .withPool(pool(Items.SUGAR, 0, 2)));
        output.accept(key(ModEntities.EVIL_SNOWMAN.get()), LootTable.lootTable()
                .withPool(pool(Items.SNOWBALL, 2, 6))
                .withPool(pool(Items.COAL, 0, 1)));
        // Elves, Santa and pups drop nothing; empty tables keep the game from warning about missing ones.
        output.accept(key(ModEntities.ELF.get()), LootTable.lootTable());
        output.accept(key(ModEntities.SANTA_CLAUS.get()), LootTable.lootTable());
        output.accept(key(ModEntities.PEPPERMINT_PUP.get()), LootTable.lootTable());
    }

    private static ResourceKey<LootTable> key(EntityType<?> type) {
        return type.getDefaultLootTable();
    }

    private static LootPool.Builder pool(ItemLike item, int min, int max) {
        return LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max))));
    }
}
