package com.mental.christmasgrove.registry;

import com.mental.christmasgrove.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

/** Keys for loot tables that code rolls directly. The tables themselves are generated data. */
public final class ModLootTables {

    public static final ResourceKey<LootTable> GIFT_BOX = key("gameplay/gift_box");
    public static final ResourceKey<LootTable> GINGERBREAD_HOUSE = key("chests/gingerbread_house");
    public static final ResourceKey<LootTable> SANTAS_WORKSHOP = key("chests/santas_workshop");
    public static final ResourceKey<LootTable> CANDY_CANE_OUTPOST = key("chests/candy_cane_outpost");
    public static final ResourceKey<LootTable> SNOWMAN_CAMP = key("chests/snowman_camp");

    private ModLootTables() {
    }

    private static ResourceKey<LootTable> key(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, Constants.id(path));
    }
}
