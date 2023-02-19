package me.hypherionmc.morecreativetabs.client;

import me.hypherionmc.morecreativetabs.platform.services.IFabricHelper;
import net.fabricmc.fabric.mixin.itemgroup.ItemGroupsAccessor;
import net.minecraft.world.item.CreativeModeTab;

import java.util.List;

public class FabricPlatformHelper implements IFabricHelper {

    @Override
    public void updateCreativeTabs(List<CreativeModeTab> tabs) {
        List<CreativeModeTab> validated = ItemGroupsAccessor.invokeCollect(tabs.toArray(new CreativeModeTab[0]));
        ItemGroupsAccessor.setGroups(validated);
    }
}
