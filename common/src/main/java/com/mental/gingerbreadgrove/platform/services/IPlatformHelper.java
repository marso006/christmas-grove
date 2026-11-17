package com.mental.gingerbreadgrove.platform.services;

import net.minecraft.world.item.CreativeModeTab;

public interface IPlatformHelper {

    /** @return a creative tab builder that the current loader positions correctly among modded tabs. */
    CreativeModeTab.Builder creativeTabBuilder();

    /** @return the name of the current loader, e.g. "Fabric" or "NeoForge". */
    String getPlatformName();

    /** @return true if a mod with the given id is loaded. */
    boolean isModLoaded(String modId);

    /** @return true when running from a development environment (Gradle run tasks). */
    boolean isDevelopmentEnvironment();

    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }
}
