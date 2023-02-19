package me.hypherionmc.morecreativetabs.platform;

import me.hypherionmc.morecreativetabs.ModConstants;
import me.hypherionmc.morecreativetabs.platform.services.IFabricHelper;

import java.util.ServiceLoader;

/**
 * @author HypherionSA
 * Helper Class Loader Service
 */
public class PlatformServices {

    public static final IFabricHelper FABRIC_HELPER = load(IFabricHelper.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        ModConstants.logger.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }

}
