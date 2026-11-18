package com.mental.christmasgrove.platform.services;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.server.level.ServerLevel;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IPlatformHelper {

    /** @return a creative tab builder that the current loader positions correctly among modded tabs. */
    CreativeModeTab.Builder creativeTabBuilder();

    /**
     * Makes axes strip each log/wood key into its value. Called once after registration; the
     * suppliers may only be resolved after blocks are registered.
     */
    void registerStrippables(Map<Supplier<? extends Block>, Supplier<? extends Block>> strippables);

    /**
     * A block entity type for the given blocks. Since 1.21.2 vanilla's constructor is private; NeoForge
     * opens it, Fabric offers a builder.
     */
    <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> factory, Block... blocks);

    /** Runs the callback at the end of every server level tick (each dimension separately). */
    void onServerLevelTick(Consumer<ServerLevel> callback);

    /** @return the name of the current loader, e.g. "Fabric" or "NeoForge". */
    String getPlatformName();

    /** @return true if a mod with the given id is loaded. */
    boolean isModLoaded(String modId);

    /** @return true when running from a development environment (Gradle run tasks). */
    boolean isDevelopmentEnvironment();

    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }
}
