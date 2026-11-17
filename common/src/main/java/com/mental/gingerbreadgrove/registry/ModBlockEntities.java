package com.mental.gingerbreadgrove.registry;

import com.mental.gingerbreadgrove.block.entity.AdventCalendarBlockEntity;
import com.mental.gingerbreadgrove.block.entity.CookieJarBlockEntity;
import com.mental.gingerbreadgrove.block.entity.GiftBoxBlockEntity;
import com.mental.gingerbreadgrove.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public final class ModBlockEntities {

    public static final Supplier<BlockEntityType<GiftBoxBlockEntity>> GIFT_BOX = Services.REGISTRY.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, "gift_box", () -> BlockEntityType.Builder.of(GiftBoxBlockEntity::new,
                    ModBlocks.RED_GIFT_BOX.get(), ModBlocks.GREEN_GIFT_BOX.get(), ModBlocks.BLUE_GIFT_BOX.get()).build(null));

    public static final Supplier<BlockEntityType<CookieJarBlockEntity>> COOKIE_JAR = Services.REGISTRY.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, "cookie_jar",
            () -> BlockEntityType.Builder.of(CookieJarBlockEntity::new, ModBlocks.COOKIE_JAR.get()).build(null));

    public static final Supplier<BlockEntityType<AdventCalendarBlockEntity>> ADVENT_CALENDAR = Services.REGISTRY.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, "advent_calendar",
            () -> BlockEntityType.Builder.of(AdventCalendarBlockEntity::new, ModBlocks.ADVENT_CALENDAR.get()).build(null));

    private ModBlockEntities() {
    }

    /** Forces class loading so the static fields above register. Call after ModBlocks. */
    public static void init() {
    }
}
