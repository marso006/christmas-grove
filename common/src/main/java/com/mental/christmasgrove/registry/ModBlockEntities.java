package com.mental.christmasgrove.registry;

import com.mental.christmasgrove.block.entity.AdventCalendarBlockEntity;
import com.mental.christmasgrove.block.entity.CookieJarBlockEntity;
import com.mental.christmasgrove.block.entity.GiftBoxBlockEntity;
import com.mental.christmasgrove.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public final class ModBlockEntities {

    public static final Supplier<BlockEntityType<GiftBoxBlockEntity>> GIFT_BOX = Services.REGISTRY.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, "gift_box", () -> Services.PLATFORM.createBlockEntityType(GiftBoxBlockEntity::new,
                    ModBlocks.RED_GIFT_BOX.get(), ModBlocks.GREEN_GIFT_BOX.get(), ModBlocks.BLUE_GIFT_BOX.get()));

    public static final Supplier<BlockEntityType<CookieJarBlockEntity>> COOKIE_JAR = Services.REGISTRY.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, "cookie_jar",
            () -> Services.PLATFORM.createBlockEntityType(CookieJarBlockEntity::new, ModBlocks.COOKIE_JAR.get()));

    public static final Supplier<BlockEntityType<AdventCalendarBlockEntity>> ADVENT_CALENDAR = Services.REGISTRY.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, "advent_calendar",
            () -> Services.PLATFORM.createBlockEntityType(AdventCalendarBlockEntity::new, ModBlocks.ADVENT_CALENDAR.get()));

    private ModBlockEntities() {
    }

    /** Forces class loading so the static fields above register. Call after ModBlocks. */
    public static void init() {
    }
}
