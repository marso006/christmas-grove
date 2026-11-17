package com.mental.gingerbreadgrove.platform.services;

import net.minecraft.core.Registry;

import java.util.function.Supplier;

/**
 * Registers game objects in a loader-independent way.
 * <p>
 * Fabric registers immediately; NeoForge queues the entry in a {@code DeferredRegister} and creates
 * it during the registry event. Either way, only call {@link Supplier#get()} on the result once
 * registration has finished, never from static initialisers of other registry classes.
 */
public interface IRegistryHelper {

    <T, R extends T> Supplier<R> register(Registry<T> registry, String name, Supplier<R> factory);
}
