package com.mental.gingerbreadgrove.registry;

import com.mental.gingerbreadgrove.item.SnowGlobeItem;
import com.mental.gingerbreadgrove.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class ModItems {

    /** Every item this mod adds (block items included), in registration order, for the creative tab. */
    private static final List<Supplier<? extends Item>> ALL = new ArrayList<>();

    /** The way into the dimension (and back). */
    public static final Supplier<SnowGlobeItem> SNOW_GLOBE = registerRaw("snow_globe",
            () -> new SnowGlobeItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    private ModItems() {
    }

    static <I extends Item> Supplier<I> registerRaw(String name, Supplier<I> factory) {
        Supplier<I> item = Services.REGISTRY.register(BuiltInRegistries.ITEM, name, factory);
        ALL.add(item);
        return item;
    }

    public static List<Supplier<? extends Item>> all() {
        return Collections.unmodifiableList(ALL);
    }

    /** Forces class loading so the static fields above register. */
    public static void init() {
    }
}
