package me.hypherionmc.morecreativetabs.client;

import me.hypherionmc.morecreativetabs.platform.services.ITabHelper;
import net.fabricmc.fabric.mixin.itemgroup.ItemGroupsAccessor;
import net.minecraft.world.item.CreativeModeTab;

import java.util.List;

/**
 * @author HypherionSA
 */
public class TabPlatformHelper implements ITabHelper {

    @Override
    public void updateCreativeTabs(List<CreativeModeTab> tabs) {
        List<CreativeModeTab> validated = ItemGroupsAccessor.invokeCollect(tabs.toArray(new CreativeModeTab[0]));
        ItemGroupsAccessor.setGroups(validated);
    }
}
