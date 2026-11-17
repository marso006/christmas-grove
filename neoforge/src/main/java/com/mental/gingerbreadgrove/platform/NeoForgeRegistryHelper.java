package com.mental.gingerbreadgrove.platform;

import com.mental.gingerbreadgrove.Constants;
import com.mental.gingerbreadgrove.platform.services.IRegistryHelper;
import net.minecraft.core.Holder;
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
    public <T, R extends T> Supplier<R> register(Registry<T> registry, String name, Supplier<R> factory) {
        return deferredFor(registry, name).register(name, factory);
    }

    @Override
    public <T> Holder<T> registerForHolder(Registry<T> registry, String name, Supplier<T> factory) {
        // DeferredHolder is itself a Holder that resolves once the registry event has run.
        return deferredFor(registry, name).register(name, factory);
    }

    @SuppressWarnings("unchecked")
    private static <T> DeferredRegister<T> deferredFor(Registry<T> registry, String name) {
        if (modBus == null) {
            throw new IllegalStateException("Registered " + name + " before the NeoForge mod bus was set");
        }
        return (DeferredRegister<T>) REGISTERS.computeIfAbsent(registry.key(), key -> {
            DeferredRegister<T> created = DeferredRegister.create(registry.key(), Constants.MOD_ID);
            created.register(modBus);
            return created;
        });
    }
}
