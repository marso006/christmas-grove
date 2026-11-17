package com.mental.christmasgrove;

import com.mental.christmasgrove.event.SantaSleighEvents;
import com.mental.christmasgrove.platform.Services;
import com.mental.christmasgrove.registry.ModArmorMaterials;
import com.mental.christmasgrove.registry.ModBlockEntities;
import com.mental.christmasgrove.registry.ModBlocks;
import com.mental.christmasgrove.registry.ModCreativeTabs;
import com.mental.christmasgrove.registry.ModEntities;
import com.mental.christmasgrove.registry.ModItems;
import com.mental.christmasgrove.registry.ModWorldgenTypes;
import net.minecraft.world.level.block.Block;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Loader-independent entry point. Each loader calls {@link #init()} from its own entrypoint,
 * before registries freeze.
 */
public final class ChristmasGrove {

    private ChristmasGrove() {
    }

    public static void init() {
        Constants.LOG.info("Initialising {} on {} ({})", Constants.MOD_NAME,
                Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());
        // Blocks before items: ModItems registers the block items of everything in ModBlocks.
        ModBlocks.init();
        ModBlockEntities.init();
        ModArmorMaterials.init();
        ModEntities.init(); // before items: spawn eggs refer to entity types
        ModItems.init();
        ModCreativeTabs.init();
        ModWorldgenTypes.init();

        Map<Supplier<? extends Block>, Supplier<? extends Block>> strippables = new LinkedHashMap<>();
        for (ModBlocks.WoodSet wood : ModBlocks.woodSets()) {
            strippables.put(wood.log(), wood.strippedLog());
            strippables.put(wood.wood(), wood.strippedWood());
        }
        Services.PLATFORM.registerStrippables(strippables);
        Services.PLATFORM.onServerLevelTick(SantaSleighEvents::tick); // Santa flies over now and then
    }
}
