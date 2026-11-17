package com.mental.christmasgrove;

import com.mental.christmasgrove.platform.NeoForgeEntityHelper;
import com.mental.christmasgrove.platform.NeoForgeRegistryHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class ChristmasGroveNeoForge {

    public ChristmasGroveNeoForge(IEventBus modBus) {
        // DeferredRegisters created during common init attach themselves to this bus.
        NeoForgeRegistryHelper.setModBus(modBus);
        modBus.addListener(NeoForgeEntityHelper::onAttributes);
        modBus.addListener(NeoForgeEntityHelper::onSpawnPlacements);
        ChristmasGrove.init();
    }
}
