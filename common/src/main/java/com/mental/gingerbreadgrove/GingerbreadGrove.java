package com.mental.gingerbreadgrove;

import com.mental.gingerbreadgrove.platform.Services;
import com.mental.gingerbreadgrove.registry.ModBlocks;
import com.mental.gingerbreadgrove.registry.ModCreativeTabs;
import com.mental.gingerbreadgrove.registry.ModItems;

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
        // Order matters on Fabric (immediate registration): blocks before the items that wrap them.
        ModBlocks.init();
        ModItems.init();
        ModCreativeTabs.init();
    }
}
