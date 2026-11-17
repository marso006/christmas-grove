package com.mental.gingerbreadgrove;

import com.mental.gingerbreadgrove.platform.NeoForgeRegistryHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class GingerbreadGroveNeoForge {

    public GingerbreadGroveNeoForge(IEventBus modBus) {
        // DeferredRegisters created during common init attach themselves to this bus.
        NeoForgeRegistryHelper.setModBus(modBus);
        GingerbreadGrove.init();
    }
}
