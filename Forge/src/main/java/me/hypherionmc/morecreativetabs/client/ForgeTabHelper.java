package me.hypherionmc.morecreativetabs.client;

import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import me.hypherionmc.morecreativetabs.mixin.accessors.CreativeModeTabAccessor;
import me.hypherionmc.morecreativetabs.platform.services.ITabHelper;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;

import java.util.List;
import java.util.Set;

/**
 * @author HypherionSA
 * UNUSED ON FORGE
 */
public class ForgeTabHelper implements ITabHelper {

    @Override
    public void updateCreativeTabs(List<CreativeModeTab> tabs) {
        //Minecraft.getInstance().createSearchTrees();
        Set<ItemStack> set = ItemStackLinkedSet.createTypeAndTagSet();
        CreativeModeTabAccessor accessor = ((CreativeModeTabAccessor) CreativeModeTabs.searchTab());
        for (CreativeModeTab t : CustomCreativeTabRegistry.current_tabs) {
            if (t.getType() != CreativeModeTab.Type.SEARCH) {
                set.addAll(t.getSearchTabDisplayItems());
            }
        }
        accessor.getDisplayItemSearchTab().clear();
        accessor.getDisplayItems().clear();
        accessor.getDisplayItems().addAll(set);
        accessor.getDisplayItemSearchTab().addAll(set);
    }
}
