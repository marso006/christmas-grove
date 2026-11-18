package com.mental.christmasgrove.platform;

import com.mental.christmasgrove.platform.services.IPlatformHelper;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashMap;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.Map;
import java.util.function.Supplier;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public CreativeModeTab.Builder creativeTabBuilder() {
        return CreativeModeTab.builder();
    }

    @Override
    public void registerStrippables(Map<Supplier<? extends Block>, Supplier<? extends Block>> strippables) {
        // Blocks are not created yet (DeferredRegister), so resolve the map lazily on first use.
        Map<Block, Block> resolved = new HashMap<>();
        NeoForge.EVENT_BUS.addListener((BlockEvent.BlockToolModificationEvent event) -> {
            if (event.getItemAbility() != ItemAbilities.AXE_STRIP) {
                return;
            }
            if (resolved.isEmpty()) {
                strippables.forEach((log, stripped) -> resolved.put(log.get(), stripped.get()));
            }
            BlockState state = event.getState();
            Block stripped = resolved.get(state.getBlock());
            if (stripped != null) {
                event.setFinalState(stripped.defaultBlockState()
                        .setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)));
            }
        });
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> factory, Block... blocks) {
        return new BlockEntityType<>(factory::apply, Set.of(blocks));
    }

    @Override
    public void onServerLevelTick(Consumer<ServerLevel> callback) {
        NeoForge.EVENT_BUS.addListener((LevelTickEvent.Post event) -> {
            if (event.getLevel() instanceof ServerLevel level) {
                callback.accept(level);
            }
        });
    }

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.isProduction();
    }
}
