package me.hypherionmc.morecreativetabs.client;

import me.hypherionmc.morecreativetabs.MoreCreativeTabs;
import me.hypherionmc.morecreativetabs.platform.services.IPlatformHelper;
import net.minecraft.world.item.CreativeModeTab;

/**
 * @author HypherionSA
 * Forge Class for Platform Specific code
 */
public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public void setNewTabs(CreativeModeTab[] tabs) {
        CreativeModeTab.TABS = tabs;
    }

    @Override
    public void reloadTabs() {
        MoreCreativeTabs.reloadTabs();
    }

}
