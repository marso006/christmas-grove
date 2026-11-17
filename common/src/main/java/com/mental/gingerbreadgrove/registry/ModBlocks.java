package com.mental.gingerbreadgrove.registry;

import com.mental.gingerbreadgrove.block.AdventCalendarBlock;
import com.mental.gingerbreadgrove.block.CookieJarBlock;
import com.mental.gingerbreadgrove.block.FestiveWallDecorBlock;
import com.mental.gingerbreadgrove.block.GiftBoxBlock;
import com.mental.gingerbreadgrove.block.GumdropBushBlock;
import com.mental.gingerbreadgrove.block.MarshmallowBlock;
import com.mental.gingerbreadgrove.block.StarBlock;
import com.mental.gingerbreadgrove.platform.Services;
import com.mental.gingerbreadgrove.worldgen.ModTreeGrowers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Every block in the mod. Families (stone sets, wood sets) are registered through helpers so that a
 * whole set of stairs, slabs, walls, doors and so on costs one line here; datagen picks the sets up
 * from {@link #stoneSets()} and {@link #woodSets()}.
 */
public final class ModBlocks {

    private static final List<Supplier<? extends Block>> ALL = new ArrayList<>();
    /** Blocks that get a plain BlockItem, registered later by ModItems once every block exists. */
    private static final Map<String, Supplier<? extends Block>> WITH_ITEM = new LinkedHashMap<>();
    private static final List<Supplier<? extends Block>> CUTOUT = new ArrayList<>();
    private static final List<Supplier<? extends Block>> TRANSLUCENT = new ArrayList<>();
    private static final List<StoneSet> STONE_SETS = new ArrayList<>();
    private static final List<WoodSet> WOOD_SETS = new ArrayList<>();

    // ---- Ground ---------------------------------------------------------------------------------

    /** Iced summits and the top of the highest peaks. */
    public static final Supplier<Block> FROSTING = register("frosting", () -> new Block(
            Properties.of().mapColor(MapColor.SNOW).strength(0.4F).sound(SoundType.SNOW)));

    /** Gingerbread with a frosting top: the "grass block" of the dimension. */
    public static final Supplier<Block> FROSTED_GINGERBREAD = register("frosted_gingerbread", () -> new Block(
            Properties.of().mapColor(MapColor.SNOW).strength(0.7F).sound(SoundType.MUD_BRICKS)));

    /** Baked dough under the surface and on cliffs: the "dirt" of the dimension. */
    public static final Supplier<Block> GINGERBREAD = register("gingerbread", () -> new Block(
            Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(0.7F).sound(SoundType.MUD_BRICKS)));

    /** Beaches of the Cocoa Sea. Falls like sand. */
    public static final Supplier<Block> SUGAR_SAND = register("sugar_sand", () -> new ColoredFallingBlock(
            new ColorRGBA(0xF4ECDDFF), Properties.of().mapColor(MapColor.SAND).strength(0.5F).sound(SoundType.SAND)));

    /** Bouncy, soft, and you never take fall damage on it. */
    public static final Supplier<Block> MARSHMALLOW_BLOCK = register("marshmallow_block", () -> new MarshmallowBlock(
            Properties.of().mapColor(MapColor.SNOW).strength(0.5F).sound(SoundType.WOOL)));

    // ---- Stone and building sets ----------------------------------------------------------------

    public static final StoneSet PEPPERMINT_STONE = stoneSet("peppermint_stone", true, Properties.of()
            .mapColor(MapColor.QUARTZ).instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.CALCITE));
    public static final StoneSet PEPPERMINT_BRICKS = stoneSet("peppermint_bricks", true, Properties.of()
            .mapColor(MapColor.QUARTZ).instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.CALCITE));
    public static final StoneSet ICING_BRICKS = stoneSet("icing_bricks", true, Properties.of()
            .mapColor(MapColor.SNOW).instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops().strength(1.2F, 5.0F).sound(SoundType.CALCITE));
    public static final StoneSet GINGERBREAD_BRICKS = stoneSet("gingerbread_bricks", true, Properties.of()
            .mapColor(MapColor.TERRACOTTA_ORANGE).instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops().strength(1.2F, 5.0F).sound(SoundType.MUD_BRICKS));
    public static final StoneSet COOKIE_TILES = stoneSet("cookie_tiles", false, Properties.of()
            .mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops().strength(1.2F, 5.0F).sound(SoundType.PACKED_MUD));

    public static final Supplier<Block> CANDY_GLASS = translucent(register("candy_glass", () -> new TransparentBlock(
            Properties.ofFullCopy(Blocks.PINK_STAINED_GLASS))));
    public static final Supplier<Block> CANDY_GLASS_PANE = translucent(register("candy_glass_pane", () -> new IronBarsBlock(
            Properties.ofFullCopy(Blocks.PINK_STAINED_GLASS_PANE))));

    // ---- Woods ----------------------------------------------------------------------------------

    public static final WoodSet CANDY_CANE = woodSet("candy_cane", "gumdrop_leaves", MapColor.COLOR_RED, MapColor.SNOW,
            ModTreeGrowers.CANDY_CANE, BlockSetType.CHERRY, WoodType.CHERRY);
    public static final WoodSet FESTIVE_FIR = woodSet("festive_fir", "festive_fir_leaves", MapColor.PODZOL, MapColor.COLOR_GREEN,
            ModTreeGrowers.FESTIVE_FIR, BlockSetType.SPRUCE, WoodType.SPRUCE);

    /** Fir leaves strung with fairy lights. They glow, and decorated firs mix them in randomly. */
    public static final Supplier<Block> RED_LIT_FIR_LEAVES = cutout(register("red_lit_festive_fir_leaves", () -> litLeaves()));
    public static final Supplier<Block> GOLD_LIT_FIR_LEAVES = cutout(register("gold_lit_festive_fir_leaves", () -> litLeaves()));
    public static final Supplier<Block> BLUE_LIT_FIR_LEAVES = cutout(register("blue_lit_festive_fir_leaves", () -> litLeaves()));

    // ---- Plants ---------------------------------------------------------------------------------

    /** Grows gumdrops like a sweet berry bush, without the thorns. Planted by placing gumdrops. */
    public static final Supplier<Block> GUMDROP_BUSH = cutout(registerNoItem("gumdrop_bush", () -> new GumdropBushBlock(
            Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).mapColor(MapColor.COLOR_PINK))));
    public static final Supplier<Block> LOLLIPOP_FLOWER = cutout(register("lollipop_flower", () -> new FlowerBlock(
            MobEffects.MOVEMENT_SPEED, 6.0F, Properties.ofFullCopy(Blocks.POPPY).mapColor(MapColor.COLOR_PINK))));
    public static final Supplier<Block> POINSETTIA = cutout(register("poinsettia", () -> new FlowerBlock(
            MobEffects.REGENERATION, 4.0F, Properties.ofFullCopy(Blocks.POPPY).mapColor(MapColor.COLOR_RED))));
    public static final Supplier<Block> HOLLY = cutout(register("holly", () -> new FlowerBlock(
            MobEffects.ABSORPTION, 6.0F, Properties.ofFullCopy(Blocks.POPPY).mapColor(MapColor.COLOR_GREEN))));
    public static final Supplier<Block> SUGAR_GRASS = cutout(register("sugar_grass", () -> new TallGrassBlock(
            Properties.ofFullCopy(Blocks.SHORT_GRASS).mapColor(MapColor.SNOW))));

    // ---- Ores -----------------------------------------------------------------------------------

    public static final Supplier<Block> SUGAR_CRYSTAL_ORE = register("sugar_crystal_ore", () -> new DropExperienceBlock(
            UniformInt.of(3, 7), Properties.of().mapColor(MapColor.QUARTZ).instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops().strength(3.0F, 3.0F).sound(SoundType.CALCITE)));
    public static final Supplier<Block> CINNAMON_ORE = register("cinnamon_ore", () -> new DropExperienceBlock(
            UniformInt.of(1, 3), Properties.of().mapColor(MapColor.QUARTZ).instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops().strength(3.0F, 3.0F).sound(SoundType.CALCITE)));
    public static final Supplier<Block> SUGAR_CRYSTAL_BLOCK = register("sugar_crystal_block", () -> new Block(
            Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F).sound(SoundType.AMETHYST)));

    // ---- Festive decor --------------------------------------------------------------------------

    /** Strings of coloured bulbs, hung on walls like a ladder. */
    public static final Supplier<Block> FESTIVE_LIGHTS = cutout(register("festive_lights", () -> new FestiveWallDecorBlock(
            Properties.of().mapColor(MapColor.COLOR_RED).noCollission().strength(0.2F).sound(SoundType.GLASS)
                    .lightLevel(state -> 12).pushReaction(PushReaction.DESTROY))));
    public static final Supplier<Block> WREATH = cutout(register("wreath", () -> new FestiveWallDecorBlock(
            Properties.of().mapColor(MapColor.COLOR_GREEN).noCollission().strength(0.2F).sound(SoundType.AZALEA_LEAVES)
                    .pushReaction(PushReaction.DESTROY))));
    /** Grows on top of Festive Firs. Brightest light in the mod. */
    public static final Supplier<Block> CHRISTMAS_STAR = cutout(register("christmas_star", () -> new StarBlock(
            Properties.of().mapColor(MapColor.GOLD).noCollission().instabreak().sound(SoundType.AMETHYST_CLUSTER)
                    .lightLevel(state -> 15).pushReaction(PushReaction.DESTROY))));

    public static final Supplier<Block> RED_GIFT_BOX = cutout(register("red_gift_box", () -> giftBox(MapColor.COLOR_RED)));
    public static final Supplier<Block> GREEN_GIFT_BOX = cutout(register("green_gift_box", () -> giftBox(MapColor.COLOR_GREEN)));
    public static final Supplier<Block> BLUE_GIFT_BOX = cutout(register("blue_gift_box", () -> giftBox(MapColor.COLOR_BLUE)));

    /** Nine slots of storage in a glass jar. */
    public static final Supplier<Block> COOKIE_JAR = cutout(register("cookie_jar", () -> new CookieJarBlock(
            Properties.of().mapColor(MapColor.NONE).strength(0.5F).sound(SoundType.GLASS).noOcclusion())));
    /** One gift per real-world day from December 1st to 25th. */
    public static final Supplier<Block> ADVENT_CALENDAR = register("advent_calendar", () -> new AdventCalendarBlock(
            Properties.of().mapColor(MapColor.COLOR_RED).strength(1.0F).sound(SoundType.WOOD)));

    private ModBlocks() {
    }

    // ---- Factories ------------------------------------------------------------------------------

    private static Block litLeaves() {
        return new LeavesBlock(Properties.ofFullCopy(Blocks.SPRUCE_LEAVES).lightLevel(state -> 9));
    }

    private static Block giftBox(MapColor colour) {
        return new GiftBoxBlock(Properties.of().mapColor(colour).strength(0.4F).sound(SoundType.WOOL)
                .pushReaction(PushReaction.DESTROY));
    }

    // ---- Registration helpers -------------------------------------------------------------------

    /** Registers a block and queues a matching block item (see {@link #blocksWithItems()}). */
    private static <B extends Block> Supplier<B> register(String name, Supplier<B> factory) {
        Supplier<B> block = registerNoItem(name, factory);
        WITH_ITEM.put(name, block);
        return block;
    }

    /** Registers a block with no item (it is placed by some other item, like a crop by its seed). */
    private static <B extends Block> Supplier<B> registerNoItem(String name, Supplier<B> factory) {
        Supplier<B> block = Services.REGISTRY.register(BuiltInRegistries.BLOCK, name, factory);
        ALL.add(block);
        return block;
    }

    private static <B extends Block> Supplier<B> cutout(Supplier<B> block) {
        CUTOUT.add(block);
        return block;
    }

    private static <B extends Block> Supplier<B> translucent(Supplier<B> block) {
        TRANSLUCENT.add(block);
        return block;
    }

    /** A building block with stairs and slab, plus a wall when {@code withWall} is true. */
    private static StoneSet stoneSet(String name, boolean withWall, BlockBehaviour.Properties properties) {
        Supplier<Block> base = register(name, () -> new Block(properties));
        Supplier<Block> stairs = register(name + "_stairs", () -> new StairBlock(base.get().defaultBlockState(), properties));
        Supplier<Block> slab = register(name + "_slab", () -> new SlabBlock(properties));
        Supplier<Block> wall = withWall ? register(name + "_wall", () -> new WallBlock(properties.forceSolidOn())) : null;
        StoneSet set = new StoneSet(name, base, stairs, slab, wall);
        STONE_SETS.add(set);
        return set;
    }

    /** A full wood family: logs, planks and every planks shape, plus leaves and a sapling. */
    private static WoodSet woodSet(String name, String leavesName, MapColor barkColour, MapColor leafColour,
                                   TreeGrower grower, BlockSetType setType, WoodType woodType) {
        Properties logProps = Properties.ofFullCopy(Blocks.OAK_LOG).mapColor(barkColour);
        Properties plankProps = Properties.ofFullCopy(Blocks.OAK_PLANKS).mapColor(barkColour);

        Supplier<Block> log = register(name + "_log", () -> new RotatedPillarBlock(logProps));
        Supplier<Block> wood = register(name + "_wood", () -> new RotatedPillarBlock(logProps));
        Supplier<Block> strippedLog = register("stripped_" + name + "_log", () -> new RotatedPillarBlock(logProps));
        Supplier<Block> strippedWood = register("stripped_" + name + "_wood", () -> new RotatedPillarBlock(logProps));
        Supplier<Block> planks = register(name + "_planks", () -> new Block(plankProps));
        Supplier<Block> stairs = register(name + "_stairs", () -> new StairBlock(planks.get().defaultBlockState(), plankProps));
        Supplier<Block> slab = register(name + "_slab", () -> new SlabBlock(plankProps));
        Supplier<Block> fence = register(name + "_fence", () -> new FenceBlock(plankProps));
        Supplier<Block> fenceGate = register(name + "_fence_gate", () -> new FenceGateBlock(woodType, plankProps.forceSolidOn()));
        Supplier<Block> door = cutout(register(name + "_door", () -> new DoorBlock(setType,
                Properties.ofFullCopy(Blocks.OAK_DOOR).mapColor(barkColour))));
        Supplier<Block> trapdoor = cutout(register(name + "_trapdoor", () -> new TrapDoorBlock(setType,
                Properties.ofFullCopy(Blocks.OAK_TRAPDOOR).mapColor(barkColour))));
        Supplier<Block> button = register(name + "_button", () -> new ButtonBlock(setType, 30,
                Properties.ofFullCopy(Blocks.OAK_BUTTON)));
        Supplier<Block> pressurePlate = register(name + "_pressure_plate", () -> new PressurePlateBlock(setType,
                Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE).mapColor(barkColour)));
        Supplier<Block> leaves = cutout(register(leavesName, () -> new LeavesBlock(
                Properties.ofFullCopy(Blocks.OAK_LEAVES).mapColor(leafColour))));
        Supplier<Block> sapling = cutout(register(name + "_sapling", () -> new SaplingBlock(grower,
                Properties.ofFullCopy(Blocks.OAK_SAPLING).mapColor(leafColour))));

        WoodSet set = new WoodSet(name, log, wood, strippedLog, strippedWood, planks, stairs, slab, fence, fenceGate,
                door, trapdoor, button, pressurePlate, leaves, sapling);
        WOOD_SETS.add(set);
        return set;
    }

    // ---- Accessors ------------------------------------------------------------------------------

    /** Registry name -> block, for every block that should get a plain BlockItem. */
    public static Map<String, Supplier<? extends Block>> blocksWithItems() {
        return Collections.unmodifiableMap(WITH_ITEM);
    }

    /** Every block this mod adds, in registration order. */
    public static List<Supplier<? extends Block>> all() {
        return Collections.unmodifiableList(ALL);
    }

    /** Blocks with transparent pixels (leaves, plants, doors) that need the cutout render layer. */
    public static List<Supplier<? extends Block>> cutoutBlocks() {
        return Collections.unmodifiableList(CUTOUT);
    }

    /** Blocks with semi-transparent pixels (glass) that need the translucent render layer. */
    public static List<Supplier<? extends Block>> translucentBlocks() {
        return Collections.unmodifiableList(TRANSLUCENT);
    }

    public static List<StoneSet> stoneSets() {
        return Collections.unmodifiableList(STONE_SETS);
    }

    public static List<WoodSet> woodSets() {
        return Collections.unmodifiableList(WOOD_SETS);
    }

    /** Forces class loading so the static fields above register. */
    public static void init() {
    }

    // ---- Set records ----------------------------------------------------------------------------

    /** @param wall null when the set has no wall */
    public record StoneSet(String name, Supplier<Block> base, Supplier<Block> stairs, Supplier<Block> slab,
                           Supplier<Block> wall) {
    }

    public record WoodSet(String name, Supplier<Block> log, Supplier<Block> wood, Supplier<Block> strippedLog,
                          Supplier<Block> strippedWood, Supplier<Block> planks, Supplier<Block> stairs,
                          Supplier<Block> slab, Supplier<Block> fence, Supplier<Block> fenceGate,
                          Supplier<Block> door, Supplier<Block> trapdoor, Supplier<Block> button,
                          Supplier<Block> pressurePlate, Supplier<Block> leaves, Supplier<Block> sapling) {
    }
}
