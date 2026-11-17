package com.mental.gingerbreadgrove.platform;

import com.mental.gingerbreadgrove.Constants;
import com.mental.gingerbreadgrove.platform.services.IRegistryHelper;
import net.minecraft.core.Registry;

import java.util.function.Supplier;

public class FabricRegistryHelper implements IRegistryHelper {

    @Override
    public <T, R extends T> Supplier<R> register(Registry<T> registry, String name, Supplier<R> factory) {
        R value = Registry.register(registry, Constants.id(name), factory.get());
        return () -> value;
    }
}
