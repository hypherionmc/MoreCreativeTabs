package me.hypherionmc.morecreativetabs.client;

import me.hypherionmc.morecreativetabs.platform.services.IPlatformHelper;
import net.minecraft.world.item.CreativeModeTab;

/**
 * @author HypherionSA
 * Fabric Class for Platform Specific code
 */
public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public void setNewTabs(CreativeModeTab[] tabs) {
        CreativeModeTab.TABS = tabs;
    }

    @Override
    public void reloadTabs() {
        MoreCreativeTabsClient.reloadTabs();
    }

}
