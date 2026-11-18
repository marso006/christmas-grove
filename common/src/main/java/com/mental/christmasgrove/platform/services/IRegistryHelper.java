package com.mental.christmasgrove.platform.services;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Function;

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

    /**
     * Like {@link #register}, but the factory receives the entry's registry key. Since 1.21.2, blocks,
     * items and entity types must know their own id when they are constructed.
     */
    <T, R extends T> Supplier<R> registerKeyed(Registry<T> registry, String name, Function<ResourceKey<T>, R> factory);

    /** Like {@link #register} but returns a {@link Holder}, for APIs that take holders (e.g. armor materials). */
    <T> Holder<T> registerForHolder(Registry<T> registry, String name, Supplier<T> factory);
}
