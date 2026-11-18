package com.mental.christmasgrove.platform;

import com.mental.christmasgrove.Constants;
import com.mental.christmasgrove.platform.services.IRegistryHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Function;
import java.util.function.Supplier;

public class FabricRegistryHelper implements IRegistryHelper {

    @Override
    public <T, R extends T> Supplier<R> register(Registry<T> registry, String name, Supplier<R> factory) {
        R value = Registry.register(registry, Constants.id(name), factory.get());
        return () -> value;
    }

    @Override
    public <T, R extends T> Supplier<R> registerKeyed(Registry<T> registry, String name, Function<ResourceKey<T>, R> factory) {
        ResourceKey<T> key = ResourceKey.create(registry.key(), Constants.id(name));
        R value = Registry.register(registry, key, factory.apply(key));
        return () -> value;
    }

    @Override
    public <T> Holder<T> registerForHolder(Registry<T> registry, String name, Supplier<T> factory) {
        return Registry.registerForHolder(registry, Constants.id(name), factory.get());
    }
}
