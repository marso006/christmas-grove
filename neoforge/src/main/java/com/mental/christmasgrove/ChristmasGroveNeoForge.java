package com.mental.christmasgrove;

import com.mental.christmasgrove.platform.NeoForgeEntityHelper;
import com.mental.christmasgrove.platform.NeoForgeRegistryHelper;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(Constants.MOD_ID)
public class ChristmasGroveNeoForge {

    public ChristmasGroveNeoForge(IEventBus modBus) {
        // DeferredRegisters created during common init attach themselves to this bus.
        NeoForgeRegistryHelper.setModBus(modBus);
        modBus.addListener(NeoForgeEntityHelper::onAttributes);
        modBus.addListener(NeoForgeEntityHelper::onSpawnPlacements);
        modBus.addListener(EventPriority.LOWEST, ChristmasGroveNeoForge::bindRegistrationTags);
        ChristmasGrove.init();
    }

    /**
     * Item properties reference tags while items register (e.g. the Sugar Crystal tools' repair tag).
     * Vanilla binds such tags to empty before freezing; NeoForge 21.2 skips that for tags first seen
     * during mod registration and then refuses to freeze. Binding them here mirrors vanilla. The real
     * contents still come from data packs. On newer NeoForge versions this changes nothing.
     */
    private static void bindRegistrationTags(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.ITEM) && BuiltInRegistries.ITEM instanceof MappedRegistry<Item> items) {
            items.bindAllTagsToEmpty();
        }
    }
}
