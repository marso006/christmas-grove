package com.mental.gingerbreadgrove;

import com.mental.gingerbreadgrove.platform.Services;
import com.mental.gingerbreadgrove.registry.ModArmorMaterials;
import com.mental.gingerbreadgrove.registry.ModBlocks;
import com.mental.gingerbreadgrove.registry.ModCreativeTabs;
import com.mental.gingerbreadgrove.registry.ModItems;
import com.mental.gingerbreadgrove.registry.ModWorldgenTypes;
import net.minecraft.world.level.block.Block;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Loader-independent entry point. Each loader calls {@link #init()} from its own entrypoint,
 * before registries freeze.
 */
public final class GingerbreadGrove {

    private GingerbreadGrove() {
    }

    public static void init() {
        Constants.LOG.info("Initialising {} on {} ({})", Constants.MOD_NAME,
                Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());
        // Blocks before items: ModItems registers the block items of everything in ModBlocks.
        ModBlocks.init();
        ModArmorMaterials.init();
        ModItems.init();
        ModCreativeTabs.init();
        ModWorldgenTypes.init();

        Map<Supplier<? extends Block>, Supplier<? extends Block>> strippables = new LinkedHashMap<>();
        for (ModBlocks.WoodSet wood : ModBlocks.woodSets()) {
            strippables.put(wood.log(), wood.strippedLog());
            strippables.put(wood.wood(), wood.strippedWood());
        }
        Services.PLATFORM.registerStrippables(strippables);
    }
}
