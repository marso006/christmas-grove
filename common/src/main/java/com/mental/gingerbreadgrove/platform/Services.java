package com.mental.gingerbreadgrove.platform;

import com.mental.gingerbreadgrove.Constants;
import com.mental.gingerbreadgrove.platform.services.IEntityHelper;
import com.mental.gingerbreadgrove.platform.services.IPlatformHelper;
import com.mental.gingerbreadgrove.platform.services.IRegistryHelper;

import java.util.ServiceLoader;

/**
 * Finds the loader-specific implementation of each service through Java's {@link ServiceLoader}.
 * Each loader module lists its implementation in {@code META-INF/services/<interface name>}.
 */
public final class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IRegistryHelper REGISTRY = load(IRegistryHelper.class);
    public static final IEntityHelper ENTITIES = load(IEntityHelper.class);

    private Services() {
    }

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz, Services.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
