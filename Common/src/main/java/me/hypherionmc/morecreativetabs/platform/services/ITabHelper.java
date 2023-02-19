package me.hypherionmc.morecreativetabs.platform.services;

import net.minecraft.world.item.CreativeModeTab;

import java.util.List;

/**
 * @author HypherionSA
 * A platform specific helper to modify tabs.
 * Mostly just used on Fabric
 */
public interface ITabHelper {

    void updateCreativeTabs(List<CreativeModeTab> tabs);

}
