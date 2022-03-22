package me.hypherionmc.morecreativetabs.client.tabs;

import me.hypherionmc.morecreativetabs.client.data.jsonhelpers.TabJsonHelper;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface TabCreator {

    public CreativeModeTab createTab(TabJsonHelper jsonHelper, List<ItemStack> stacks);

}
