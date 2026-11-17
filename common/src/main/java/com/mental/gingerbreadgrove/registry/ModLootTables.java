package com.mental.gingerbreadgrove.registry;

import com.mental.gingerbreadgrove.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

/** Keys for loot tables that code rolls directly. The tables themselves are generated data. */
public final class ModLootTables {

    public static final ResourceKey<LootTable> GIFT_BOX = key("gameplay/gift_box");

    private ModLootTables() {
    }

    private static ResourceKey<LootTable> key(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, Constants.id(path));
    }
}
