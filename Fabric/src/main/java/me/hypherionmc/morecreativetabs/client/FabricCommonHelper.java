package me.hypherionmc.morecreativetabs.client;

import me.hypherionmc.morecreativetabs.platform.services.IPlatformHelper;

public class FabricCommonHelper implements IPlatformHelper {

    @Override
    public boolean isFabric() {
        return true;
    }
}
