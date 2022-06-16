package me.hypherionmc.morecreativetabs.client.tabs;

import com.google.gson.Gson;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.hypherionmc.morecreativetabs.ModConstants;
import me.hypherionmc.morecreativetabs.client.data.jsonhelpers.DisabledTabsJsonHelper;
import me.hypherionmc.morecreativetabs.client.data.jsonhelpers.TabJsonHelper;
import me.hypherionmc.morecreativetabs.platform.PlatformServices;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author HypherionSA
 * Class to manage all custom tabs and items
 */
public class CustomCreativeTabManager {

    /* Items to remove from their old tabs */
    public static Set<Item> hidden_stacks = new HashSet<>();

    /* List of Custom Defined tabs */
    public static Set<CreativeModeTab> custom_tabs = new HashSet<>();

    /* List of Disabled Tabs */
    public static Set<String> disabled_tabs = new HashSet<>();

    /* Should the Name or Registry name of the tab be showed */
    public static boolean showNames = false;

    /* A fixed backup of all creative tabs, before custom ones are added */
    public static CreativeModeTab[] tabs_before;

    /**
     * Load and process the resource/data pack
     * @param entries - The found entries
     * @param creator - The "helper" class that creates the custom tab
     */
    public static void loadEntries(Map<ResourceLocation, Resource> entries, TabCreator creator) {
        entries.forEach((location, resource) -> {
            ModConstants.logger.info("Processing " + location.toString());

            try (InputStream stream = resource.open()) {
                TabJsonHelper json = new Gson().fromJson(new InputStreamReader(stream), TabJsonHelper.class);
                ArrayList<ItemStack> tabItems = new ArrayList<>();

                /* Check if the tab is enabled and should be loaded */
                if (json.tab_enabled) {

                    /* Loop over all the Item Stack entries */
                    json.tab_items.forEach(item -> {
                        ItemStack stack = getItemStack(item.name);
                        if (stack != ItemStack.EMPTY) {
                            if (item.hide_old_tab) {
                                hidden_stacks.add(stack.getItem());
                            }

                            /* Parse the Item NBT and apply it to the stack */
                            if (item.nbt != null && !item.nbt.isEmpty()) {
                                try {
                                    CompoundTag tag = TagParser.parseTag(item.nbt);
                                    stack.setTag(tag);

                                    /* Give the item a "Custom Name" if defined in NBT */
                                    if (tag.contains("customName")) {
                                        stack.setHoverName(Component.literal(tag.getString("customName")));
                                    }
                                } catch (CommandSyntaxException e) {
                                    e.printStackTrace();
                                }
                            }

                            /* Store the item for adding to the creative tab */
                            tabItems.add(stack);
                        }
                    });

                    /* Create the actual tab and store it */
                    custom_tabs.add(creator.createTab(json, tabItems));
                }
            } catch (Exception e) {
                ModConstants.logger.error("Failed to process creative tab");
                e.printStackTrace();
            }
        });
    }

    /**
     * The default Creative Tab creator. Actually just used on Forge, but whatever
     * @param json - Tab JSON entry
     * @param tabItems - The items to add to the tab
     * @return - An initialized Creative tab with all the items it should contain
     */
    public static CreativeModeTab defaultTabCreator(TabJsonHelper json, List<ItemStack> tabItems) {
        return new CreativeModeTab(-1, "morecreativetabs." + json.tab_name) {

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
                itemStacks.addAll(tabItems);
            }
        };
    }

    /**
     * Load disabled tabs for later processing
     */
    public static void loadDisabledTabs(Map<ResourceLocation, Resource> resourceMap) {
       resourceMap.forEach((location, resource) -> {
           ModConstants.logger.info("Processing " + location.toString());
           try (InputStream stream = resource.open()) {
               DisabledTabsJsonHelper json = new Gson().fromJson(new InputStreamReader(stream), DisabledTabsJsonHelper.class);
               disabled_tabs.addAll(json.disabled_tabs);
           } catch (Exception e) {
               ModConstants.logger.error("Failed to process disabled tabs for " + location);
               e.printStackTrace();
           }
       });
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
     * Clear all cached data for reloading
     */
    public static void clearTabs() {
        hidden_stacks.clear();
        disabled_tabs.clear();
        PlatformServices.helper.setNewTabs(tabs_before);
        custom_tabs.clear();
    }
}
