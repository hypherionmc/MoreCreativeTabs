package me.hypherionmc.morecreativetabs.platform.services;

import net.minecraft.world.item.CreativeModeTab;

/**
 * @author HypherionSA
 * Service for Platform Specific code
 */
public interface IPlatformHelper {

    /**
     * Called to update the creative tabs in the game
     * @param tabs - The new list of tabs
     */
    public void setNewTabs(CreativeModeTab[] tabs);

    /**
     * Called when the creative tabs need to be reloaded
     */
    public void reloadTabs();

}
