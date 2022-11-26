package me.hypherionmc.morecreativetabs.client;

import me.hypherionmc.morecreativetabs.client.data.jsonhelpers.CustomCreativeTab;
import me.hypherionmc.morecreativetabs.client.tabs.TabCreator;
import me.hypherionmc.morecreativetabs.util.CreativeTabUtils;
import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * @author HypherionSA
 * Fabric Custom Tab Creator
 */
public class FabricTabCreator implements TabCreator {

    @Override
    public CreativeModeTab createTab(CustomCreativeTab json, List<ItemStack> stacks) {
        ((ItemGroupExtensions) CreativeModeTab.TAB_BUILDING_BLOCKS).fabric_expandArray();
        return CreativeTabUtils.defaultTabCreator(CreativeModeTab.TABS.length  -1, json, stacks);
    }
}
