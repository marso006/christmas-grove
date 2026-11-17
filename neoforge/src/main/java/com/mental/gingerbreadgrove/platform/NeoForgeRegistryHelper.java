package com.mental.gingerbreadgrove.platform;

import com.mental.gingerbreadgrove.Constants;
import com.mental.gingerbreadgrove.platform.services.IRegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NeoForgeRegistryHelper implements IRegistryHelper {

    private static IEventBus modBus;
    private static final Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> REGISTERS = new HashMap<>();

    public static void setModBus(IEventBus bus) {
        modBus = bus;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, R extends T> Supplier<R> register(Registry<T> registry, String name, Supplier<R> factory) {
        if (modBus == null) {
            throw new IllegalStateException("Registered " + name + " before the NeoForge mod bus was set");
        }
        DeferredRegister<T> deferred = (DeferredRegister<T>) REGISTERS.computeIfAbsent(registry.key(), key -> {
            DeferredRegister<T> created = DeferredRegister.create(registry.key(), Constants.MOD_ID);
            created.register(modBus);
            return created;
        });
        return deferred.register(name, factory);
    }
}
