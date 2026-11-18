package com.mental.christmasgrove.registry;

import com.mental.christmasgrove.item.KrampusBellItem;
import com.mental.christmasgrove.item.LetterToSantaItem;
import com.mental.christmasgrove.item.NiceListItem;
import com.mental.christmasgrove.item.SnowGlobeItem;
import com.mental.christmasgrove.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Every item in the mod. Since Minecraft 1.21.2 an item's properties must carry its registry id, so
 * each registration receives fresh {@code Item.Properties} with the id already set.
 */
public final class ModItems {

    /** Every item this mod adds (block items included), in registration order, for the creative tab. */
    private static final List<Supplier<? extends Item>> ALL = new ArrayList<>();
    /** Plain items that use the standard flat "generated" item model (block items excluded). */
    private static final List<Supplier<? extends Item>> FLAT = new ArrayList<>();
    /** Spawn eggs, which use the vanilla spawn egg model. */
    private static final List<Supplier<? extends Item>> SPAWN_EGGS = new ArrayList<>();
    /** Tools and weapons, which use the "handheld" item model (held diagonally). */
    private static final List<Supplier<? extends Item>> HANDHELD = new ArrayList<>();

    static {
        // Block items first. Touching ModBlocks here guarantees every block is registered before any
        // item that refers to one (Fabric registers immediately, so order matters there).
        ModBlocks.blocksWithItems().forEach((name, block) ->
                registerRaw(name, p -> new BlockItem(block.get(), p.useBlockDescriptionPrefix())));
    }

    /** The way into the dimension (and back). */
    public static final Supplier<SnowGlobeItem> SNOW_GLOBE = flat(registerRaw("snow_globe",
            p -> new SnowGlobeItem(p.stacksTo(1).rarity(Rarity.UNCOMMON))));

    // ---- Materials ------------------------------------------------------------------------------

    public static final Supplier<Item> SUGAR_CRYSTAL = simple("sugar_crystal", p -> p);
    public static final Supplier<Item> CINNAMON_STICK = simple("cinnamon_stick", p -> p);
    public static final Supplier<Item> MUG = simple("mug", p -> p.stacksTo(16));
    public static final Supplier<NiceListItem> NICE_LIST = flat(registerRaw("nice_list", p -> new NiceListItem(p.stacksTo(1))));
    public static final Supplier<KrampusBellItem> KRAMPUS_BELL = flat(registerRaw("krampus_bell",
            p -> new KrampusBellItem(p.stacksTo(1).rarity(Rarity.RARE))));
    public static final Supplier<Item> KRAMPUS_HORN = simple("krampus_horn", p -> p.rarity(Rarity.EPIC));
    /** Send it and Santa's sleigh flies over, dropping presents. */
    public static final Supplier<LetterToSantaItem> LETTER_TO_SANTA = flat(registerRaw("letter_to_santa",
            p -> new LetterToSantaItem(p.stacksTo(16).rarity(Rarity.UNCOMMON))));
    /** Put them on a tamed reindeer and it glides when ridden. */
    public static final Supplier<Item> SLEIGH_BELLS = simple("sleigh_bells", p -> p.stacksTo(1).rarity(Rarity.UNCOMMON));

    // ---- Food -----------------------------------------------------------------------------------

    /** Also plants a Gumdrop Bush, the way sweet berries plant a berry bush. */
    public static final Supplier<Item> GUMDROPS = flat(registerRaw("gumdrops",
            p -> new BlockItem(ModBlocks.GUMDROP_BUSH.get(), p.food(ModFoods.GUMDROPS, ModFoods.QUICK))));
    public static final Supplier<Item> CANDY_CANE = simple("candy_cane", p -> p.food(ModFoods.CANDY_CANE, ModFoods.CANDY_CANE_EFFECT));
    public static final Supplier<Item> GINGERBREAD_COOKIE = simple("gingerbread_cookie", p -> p.food(ModFoods.GINGERBREAD_COOKIE));
    public static final Supplier<Item> SUGAR_COOKIE = simple("sugar_cookie", p -> p.food(ModFoods.SUGAR_COOKIE, ModFoods.QUICK));
    public static final Supplier<Item> MARSHMALLOW = simple("marshmallow", p -> p.food(ModFoods.MARSHMALLOW, ModFoods.QUICK));
    public static final Supplier<Item> ROASTED_MARSHMALLOW = simple("roasted_marshmallow", p -> p.food(ModFoods.ROASTED_MARSHMALLOW));
    public static final Supplier<Item> FRUITCAKE_SLICE = simple("fruitcake_slice", p -> p.food(ModFoods.FRUITCAKE_SLICE));
    public static final Supplier<Item> GINGERBREAD_HEART = simple("gingerbread_heart",
            p -> p.food(ModFoods.GINGERBREAD_HEART, ModFoods.GINGERBREAD_HEART_EFFECT).rarity(Rarity.RARE));
    /** Drinks leave their container behind. */
    public static final Supplier<Item> HOT_COCOA = simple("hot_cocoa",
            p -> p.food(ModFoods.HOT_COCOA, ModFoods.HOT_COCOA_EFFECT).usingConvertsTo(MUG.get()).stacksTo(16));
    public static final Supplier<Item> EGGNOG = simple("eggnog",
            p -> p.food(ModFoods.EGGNOG, ModFoods.EGGNOG_EFFECT).usingConvertsTo(Items.GLASS_BOTTLE).stacksTo(16));

    // ---- Tools (candy-cane handles, sugar-crystal heads) ----------------------------------------

    public static final Supplier<SwordItem> CANDY_CANE_SWORD = handheld(registerRaw("candy_cane_sword",
            p -> new SwordItem(ModTiers.SUGAR_CRYSTAL, 3.0F, -2.4F, p)));
    public static final Supplier<PickaxeItem> SUGAR_CRYSTAL_PICKAXE = handheld(registerRaw("sugar_crystal_pickaxe",
            p -> new PickaxeItem(ModTiers.SUGAR_CRYSTAL, 1.0F, -2.8F, p)));
    public static final Supplier<AxeItem> SUGAR_CRYSTAL_AXE = handheld(registerRaw("sugar_crystal_axe",
            p -> new AxeItem(ModTiers.SUGAR_CRYSTAL, 5.5F, -3.1F, p)));
    public static final Supplier<ShovelItem> SUGAR_CRYSTAL_SHOVEL = handheld(registerRaw("sugar_crystal_shovel",
            p -> new ShovelItem(ModTiers.SUGAR_CRYSTAL, 1.5F, -3.0F, p)));
    public static final Supplier<HoeItem> SUGAR_CRYSTAL_HOE = handheld(registerRaw("sugar_crystal_hoe",
            p -> new HoeItem(ModTiers.SUGAR_CRYSTAL, -2.5F, -0.5F, p)));

    // ---- Santa Suit -----------------------------------------------------------------------------

    public static final Supplier<ArmorItem> SANTA_HAT = armor("santa_hat", ModArmorMaterials.SANTA_HAT, ArmorType.HELMET);
    public static final Supplier<ArmorItem> SANTA_COAT = armor("santa_coat", ModArmorMaterials.SANTA_SUIT, ArmorType.CHESTPLATE);
    public static final Supplier<ArmorItem> SANTA_TROUSERS = armor("santa_trousers", ModArmorMaterials.SANTA_SUIT, ArmorType.LEGGINGS);
    public static final Supplier<ArmorItem> SANTA_BOOTS = armor("santa_boots", ModArmorMaterials.SANTA_SUIT, ArmorType.BOOTS);

    // ---- Spawn eggs ----------------------------------------------------------------------------

    public static final Supplier<Item> GINGERBREAD_MAN_SPAWN_EGG = spawnEgg("gingerbread_man", ModEntities.GINGERBREAD_MAN, 0xB0682F, 0xFFFFFF);
    public static final Supplier<Item> REINDEER_SPAWN_EGG = spawnEgg("reindeer", ModEntities.REINDEER, 0x7A5230, 0xE8D8C0);
    public static final Supplier<Item> PEPPERMINT_PUP_SPAWN_EGG = spawnEgg("peppermint_pup", ModEntities.PEPPERMINT_PUP, 0xFFFFFF, 0xD62834);
    public static final Supplier<Item> ELF_SPAWN_EGG = spawnEgg("elf", ModEntities.ELF, 0x2E8B3A, 0xD62834);
    public static final Supplier<Item> SANTA_CLAUS_SPAWN_EGG = spawnEgg("santa_claus", ModEntities.SANTA_CLAUS, 0xC81E2A, 0xFFFFFF);
    public static final Supplier<Item> COOKIE_SOLDIER_SPAWN_EGG = spawnEgg("cookie_soldier", ModEntities.COOKIE_SOLDIER, 0x9A2A2A, 0xE0B070);
    public static final Supplier<Item> GUMDROP_BLOB_SPAWN_EGG = spawnEgg("gumdrop_blob", ModEntities.GUMDROP_BLOB, 0xEC4078, 0x5AC85A);
    public static final Supplier<Item> EVIL_SNOWMAN_SPAWN_EGG = spawnEgg("evil_snowman", ModEntities.EVIL_SNOWMAN, 0xF4F4F4, 0x222222);
    public static final Supplier<Item> KRAMPUS_SPAWN_EGG = spawnEgg("krampus", ModEntities.KRAMPUS, 0x2A1A12, 0xB01020);

    private ModItems() {
    }

    // ---- Helpers --------------------------------------------------------------------------------

    /** A plain item; {@code properties} adds to the (id-carrying) default properties. */
    private static Supplier<Item> simple(String name, Function<Item.Properties, Item.Properties> properties) {
        return flat(registerRaw(name, p -> new Item(properties.apply(p))));
    }

    private static Supplier<ArmorItem> armor(String name, ArmorMaterial material, ArmorType type) {
        return flat(registerRaw(name, p -> new ArmorItem(material, type, p)));
    }

    private static Supplier<Item> spawnEgg(String entity, Supplier<? extends EntityType<? extends Mob>> type, int background, int highlight) {
        Supplier<Item> egg = registerRaw(entity + "_spawn_egg", p -> Services.ENTITIES.createSpawnEgg(type, background, highlight, p));
        SPAWN_EGGS.add(egg);
        return egg;
    }

    private static <I extends Item> Supplier<I> flat(Supplier<I> item) {
        FLAT.add(item);
        return item;
    }

    private static <I extends Item> Supplier<I> handheld(Supplier<I> item) {
        HANDHELD.add(item);
        return item;
    }

    /** Registers an item; the constructor receives fresh properties with the registry id set. */
    private static <I extends Item> Supplier<I> registerRaw(String name, Function<Item.Properties, I> constructor) {
        Supplier<I> item = Services.REGISTRY.registerKeyed(BuiltInRegistries.ITEM, name,
                key -> constructor.apply(new Item.Properties().setId(key)));
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

    public static List<Supplier<? extends Item>> spawnEggs() {
        return Collections.unmodifiableList(SPAWN_EGGS);
    }

    /** Tools and weapons that get the handheld item model from {@code textures/item/<name>.png}. */
    public static List<Supplier<? extends Item>> handheldItems() {
        return Collections.unmodifiableList(HANDHELD);
    }

    /** Forces class loading so the static fields above register. */
    public static void init() {
    }
}
