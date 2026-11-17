package com.mental.gingerbreadgrove.registry;

import com.mental.gingerbreadgrove.item.DrinkItem;
import com.mental.gingerbreadgrove.item.SnowGlobeItem;
import com.mental.gingerbreadgrove.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class ModItems {

    /** Every item this mod adds (block items included), in registration order, for the creative tab. */
    private static final List<Supplier<? extends Item>> ALL = new ArrayList<>();
    /** Plain items that use the standard flat "generated" item model (block items excluded). */
    private static final List<Supplier<? extends Item>> FLAT = new ArrayList<>();
    /** Tools and weapons, which use the "handheld" item model (held diagonally). */
    private static final List<Supplier<? extends Item>> HANDHELD = new ArrayList<>();

    static {
        // Block items first. Touching ModBlocks here guarantees every block is registered before any
        // item that refers to one (Fabric registers immediately, so order matters there).
        ModBlocks.blocksWithItems().forEach((name, block) ->
                registerRaw(name, () -> new BlockItem(block.get(), new Item.Properties())));
    }

    /** The way into the dimension (and back). */
    public static final Supplier<SnowGlobeItem> SNOW_GLOBE = flat(registerRaw("snow_globe",
            () -> new SnowGlobeItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON))));

    // ---- Materials ------------------------------------------------------------------------------

    public static final Supplier<Item> SUGAR_CRYSTAL = simple("sugar_crystal", new Item.Properties());
    public static final Supplier<Item> CINNAMON_STICK = simple("cinnamon_stick", new Item.Properties());
    public static final Supplier<Item> MUG = simple("mug", new Item.Properties().stacksTo(16));

    // ---- Food -----------------------------------------------------------------------------------

    /** Also plants a Gumdrop Bush, the way sweet berries plant a berry bush. */
    public static final Supplier<Item> GUMDROPS = flat(registerRaw("gumdrops",
            () -> new ItemNameBlockItem(ModBlocks.GUMDROP_BUSH.get(), new Item.Properties().food(ModFoods.GUMDROPS))));
    public static final Supplier<Item> CANDY_CANE = simple("candy_cane", new Item.Properties().food(ModFoods.CANDY_CANE));
    public static final Supplier<Item> GINGERBREAD_COOKIE = simple("gingerbread_cookie",
            new Item.Properties().food(ModFoods.GINGERBREAD_COOKIE));
    public static final Supplier<Item> SUGAR_COOKIE = simple("sugar_cookie", new Item.Properties().food(ModFoods.SUGAR_COOKIE));
    public static final Supplier<Item> MARSHMALLOW = simple("marshmallow", new Item.Properties().food(ModFoods.MARSHMALLOW));
    public static final Supplier<Item> ROASTED_MARSHMALLOW = simple("roasted_marshmallow",
            new Item.Properties().food(ModFoods.ROASTED_MARSHMALLOW));
    public static final Supplier<Item> FRUITCAKE_SLICE = simple("fruitcake_slice",
            new Item.Properties().food(ModFoods.FRUITCAKE_SLICE));
    public static final Supplier<Item> GINGERBREAD_HEART = simple("gingerbread_heart",
            new Item.Properties().food(ModFoods.GINGERBREAD_HEART).rarity(Rarity.RARE));
    public static final Supplier<DrinkItem> HOT_COCOA = flat(registerRaw("hot_cocoa",
            () -> new DrinkItem(MUG, new Item.Properties().food(ModFoods.HOT_COCOA).stacksTo(16))));
    public static final Supplier<DrinkItem> EGGNOG = flat(registerRaw("eggnog",
            () -> new DrinkItem(() -> Items.GLASS_BOTTLE, new Item.Properties().food(ModFoods.EGGNOG).stacksTo(16))));

    // ---- Tools (candy-cane handles, sugar-crystal heads) ----------------------------------------

    public static final Supplier<SwordItem> CANDY_CANE_SWORD = handheld(registerRaw("candy_cane_sword",
            () -> new SwordItem(ModTiers.SUGAR_CRYSTAL, new Item.Properties()
                    .attributes(SwordItem.createAttributes(ModTiers.SUGAR_CRYSTAL, 3, -2.4F)))));
    public static final Supplier<PickaxeItem> SUGAR_CRYSTAL_PICKAXE = handheld(registerRaw("sugar_crystal_pickaxe",
            () -> new PickaxeItem(ModTiers.SUGAR_CRYSTAL, new Item.Properties()
                    .attributes(DiggerItem.createAttributes(ModTiers.SUGAR_CRYSTAL, 1.0F, -2.8F)))));
    public static final Supplier<AxeItem> SUGAR_CRYSTAL_AXE = handheld(registerRaw("sugar_crystal_axe",
            () -> new AxeItem(ModTiers.SUGAR_CRYSTAL, new Item.Properties()
                    .attributes(DiggerItem.createAttributes(ModTiers.SUGAR_CRYSTAL, 5.5F, -3.1F)))));
    public static final Supplier<ShovelItem> SUGAR_CRYSTAL_SHOVEL = handheld(registerRaw("sugar_crystal_shovel",
            () -> new ShovelItem(ModTiers.SUGAR_CRYSTAL, new Item.Properties()
                    .attributes(DiggerItem.createAttributes(ModTiers.SUGAR_CRYSTAL, 1.5F, -3.0F)))));
    public static final Supplier<HoeItem> SUGAR_CRYSTAL_HOE = handheld(registerRaw("sugar_crystal_hoe",
            () -> new HoeItem(ModTiers.SUGAR_CRYSTAL, new Item.Properties()
                    .attributes(DiggerItem.createAttributes(ModTiers.SUGAR_CRYSTAL, -2.5F, -0.5F)))));

    // ---- Santa Suit -----------------------------------------------------------------------------

    public static final Supplier<ArmorItem> SANTA_HAT = santaSuit("santa_hat", ArmorItem.Type.HELMET);
    public static final Supplier<ArmorItem> SANTA_COAT = santaSuit("santa_coat", ArmorItem.Type.CHESTPLATE);
    public static final Supplier<ArmorItem> SANTA_TROUSERS = santaSuit("santa_trousers", ArmorItem.Type.LEGGINGS);
    public static final Supplier<ArmorItem> SANTA_BOOTS = santaSuit("santa_boots", ArmorItem.Type.BOOTS);

    private ModItems() {
    }

    private static Supplier<ArmorItem> santaSuit(String name, ArmorItem.Type type) {
        return flat(registerRaw(name, () -> new ArmorItem(ModArmorMaterials.SANTA_SUIT, type,
                new Item.Properties().durability(type.getDurability(20)))));
    }

    private static <I extends Item> Supplier<I> handheld(Supplier<I> item) {
        HANDHELD.add(item);
        return item;
    }

    private static Supplier<Item> simple(String name, Item.Properties properties) {
        return flat(registerRaw(name, () -> new Item(properties)));
    }

    private static <I extends Item> Supplier<I> flat(Supplier<I> item) {
        FLAT.add(item);
        return item;
    }

    private static <I extends Item> Supplier<I> registerRaw(String name, Supplier<I> factory) {
        Supplier<I> item = Services.REGISTRY.register(BuiltInRegistries.ITEM, name, factory);
        ALL.add(item);
        return item;
    }

    public static List<Supplier<? extends Item>> all() {
        return Collections.unmodifiableList(ALL);
    }

    /** Items that get a plain flat item model from {@code textures/item/<name>.png}. */
    public static List<Supplier<? extends Item>> flatItems() {
        return Collections.unmodifiableList(FLAT);
    }

    /** Tools and weapons that get the handheld item model from {@code textures/item/<name>.png}. */
    public static List<Supplier<? extends Item>> handheldItems() {
        return Collections.unmodifiableList(HANDHELD);
    }

    /** Forces class loading so the static fields above register. */
    public static void init() {
    }
}
