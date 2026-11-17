package com.mental.gingerbreadgrove.registry;

import com.mental.gingerbreadgrove.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class ModBlocks {

    private static final List<Supplier<? extends Block>> ALL = new ArrayList<>();

    // ---- Ground: surface and cliffs -------------------------------------------------------------

    /** Top layer of the biome, the equivalent of snow on grass. */
    public static final Supplier<Block> FROSTING = register("frosting", () -> new Block(
            BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.4F).sound(SoundType.SNOW)));

    /** Gingerbread with a frosting top: the "grass block" of the biome. */
    public static final Supplier<Block> FROSTED_GINGERBREAD = register("frosted_gingerbread", () -> new Block(
            BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.7F).sound(SoundType.MUD_BRICKS)));

    /** Baked dough under the surface and in cliffs: the "dirt" of the biome. */
    public static final Supplier<Block> GINGERBREAD = register("gingerbread", () -> new Block(
            BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(0.7F).sound(SoundType.MUD_BRICKS)));

    /** Deep stone layer: the "stone" of the biome. */
    public static final Supplier<Block> PEPPERMINT_STONE = register("peppermint_stone", () -> new Block(
            BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.CALCITE)));

    private ModBlocks() {
    }

    /** Registers a block and a matching block item, and remembers it for datagen and the creative tab. */
    private static <B extends Block> Supplier<B> register(String name, Supplier<B> factory) {
        Supplier<B> block = Services.REGISTRY.register(BuiltInRegistries.BLOCK, name, factory);
        ModItems.registerRaw(name, () -> new BlockItem(block.get(), new Item.Properties()));
        ALL.add(block);
        return block;
    }

    /** Every block this mod adds, in registration order. */
    public static List<Supplier<? extends Block>> all() {
        return Collections.unmodifiableList(ALL);
    }

    /** Forces class loading so the static fields above register. */
    public static void init() {
    }
}
