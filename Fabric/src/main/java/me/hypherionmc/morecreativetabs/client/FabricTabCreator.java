package me.hypherionmc.morecreativetabs.client;

import me.hypherionmc.morecreativetabs.client.data.jsonhelpers.TabJsonHelper;
import me.hypherionmc.morecreativetabs.client.tabs.TabCreator;
import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager.getItemStack;

/**
 * @author HypherionSA
 * Fabric Custom Tab Creator
 */
public class FabricTabCreator implements TabCreator {

    @Override
    public CreativeModeTab createTab(TabJsonHelper json, List<ItemStack> stacks) {
        ((ItemGroupExtensions) CreativeModeTab.TAB_BUILDING_BLOCKS).fabric_expandArray();
        return new CreativeModeTab(CreativeModeTab.TABS.length - 1, String.format("%s.%s", "morecreativetabs", json.tab_name)) {

            /**
             * Use a custom image for the creative tab background
             * @return - String containing the path to the image
             */
            @Override
            public String getBackgroundSuffix() {
                return json.tab_background == null ? super.getBackgroundSuffix() : json.tab_background;
            }

            /**
             * Override the Tab Icon to the custom one defined in the JSON
             * @return - The item stack to use as the item
             */
            @Override
            public ItemStack makeIcon() {
                AtomicReference<ItemStack> icon = new AtomicReference<>(ItemStack.EMPTY);
                TabJsonHelper.TabIcon tabIcon = new TabJsonHelper.TabIcon();

                if (json.tab_stack != null) {
                    tabIcon = json.tab_stack;
                } else if (json.tab_icon != null) {
                    // WARNING: This is just to add support for the old format... To be removed
                    tabIcon = new TabJsonHelper.TabIcon();
                    tabIcon.name = json.tab_icon;
                }

                /* Resolve the Icon from the Item Registry */
                TabJsonHelper.TabIcon finalTabIcon = tabIcon;
                ItemStack stack = getItemStack(tabIcon.name);

                if (!stack.isEmpty()) {
                    if (finalTabIcon.nbt != null) {
                        CompoundTag tag = new CompoundTag();
                        try {
                            tag = TagParser.parseTag(finalTabIcon.nbt);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        /* Apply the Stack NBT if needed and apply the icon */
                        stack.setTag(tag);
                        icon.set(stack);
                    } else {
                        icon.set(stack);
                    }
                }
                return icon.get();
            }

            /**
             * Fill the Creative Tab with the items it should contain
             * @param itemStacks - Unused
             */
            @Override
            public void fillItemList(NonNullList<ItemStack> itemStacks) {
                itemStacks.clear();
                itemStacks.addAll(stacks);
            }
        };
    }
}
