package me.hypherionmc.morecreativetabs.util;

import me.hypherionmc.morecreativetabs.client.data.jsonhelpers.CustomCreativeTab;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class CreativeTabUtils {

    /**
     * Helper Method to create a Tab Icon from the JSON file. Used to remove duplicated code between Forge/Fabric
     * @param json - The JSON class of the loaded Tab JSON
     * @return - Returns an ItemStack or Empty ItemStack
     */
    public static ItemStack makeTabIcon(CustomCreativeTab json) {
        AtomicReference<ItemStack> icon = new AtomicReference<>(ItemStack.EMPTY);
        CustomCreativeTab.TabIcon tabIcon = new CustomCreativeTab.TabIcon();

        if (json.tab_stack != null) {
            tabIcon = json.tab_stack;
        } else if (json.tab_icon != null) {
            // WARNING: This is just to add support for the old format... To be removed
            tabIcon = new CustomCreativeTab.TabIcon();
            tabIcon.name = json.tab_icon;
        }

        /* Resolve the Icon from the Item Registry */
        CustomCreativeTab.TabIcon finalTabIcon = tabIcon;
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
     * Get the item stack from the Minecraft Registry
     * @param jsonItem - The Item JSON object
     * @return - The item if found, or return an empty item
     */
    public static ItemStack getItemStack(String jsonItem) {
        Optional<Item> itemOptional = Registry.ITEM.getOptional(new ResourceLocation(jsonItem));
        return itemOptional.map(ItemStack::new).orElse(ItemStack.EMPTY);
    }

    /**
     * Prefix Custom Tab names with morecreativetabs to avoid Lang Key conflicts
     * @param tabName - The name of the tab from the JSON
     * @return - Returns `morecreativetabs.tabname`
     */
    public static String prefix(String tabName) {
        return String.format("%s.%s", "morecreativetabs", tabName);
    }

    /**
     * The default Creative Tab creator.
     * @param json - Tab JSON entry
     * @param tabItems - The items to add to the tab
     * @return - An initialized Creative tab with all the items it should contain
     */
    public static CreativeModeTab defaultTabCreator(int index, CustomCreativeTab json, List<ItemStack> tabItems) {
        return new CreativeModeTab(index, prefix(json.tab_name)) {

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
                return CreativeTabUtils.makeTabIcon(json);
            }

            /**
             * Fill the Creative Tab with the items it should contain
             * @param itemStacks - Unused
             */
            @Override
            public void fillItemList(NonNullList<ItemStack> itemStacks) {
                itemStacks.clear();
                itemStacks.addAll(tabItems);
            }
        };
    }

    /**
     * Convert Resource Location into String for processing replacement tabs
     * @param input - The PATH section of a ResourceLocation
     * @return - The tab name in format `itemGroup.name` or `name`
     */
    public static String fileToTab(String input) {
        input = input.replace("morecreativetabs/", "");
        input = input.replace("morecreativetabs", "");
        input = input.replace(".json", "");

        return input;
    }

    /**
     * Helper method to detect if a Custom Tab is a replacement of an existing tab
     * @param tabName - The "recipeFolderName" of the tab to find
     * @return - An optional containing the tab data
     */
    public static Optional<Pair<CustomCreativeTab, List<ItemStack>>> replacementTab(String tabName) {
        if (CustomCreativeTabManager.replaced_tabs.containsKey(tabName)) {
            return Optional.of(CustomCreativeTabManager.replaced_tabs.get(tabName));
        }
        if (CustomCreativeTabManager.replaced_tabs.containsKey(tabName.toLowerCase())) {
            return Optional.of(CustomCreativeTabManager.replaced_tabs.get(tabName));
        }
        return Optional.empty();
    }
}
