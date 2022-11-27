package me.hypherionmc.morecreativetabs.client.tabs;

import com.google.gson.Gson;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.hypherionmc.morecreativetabs.ModConstants;
import me.hypherionmc.morecreativetabs.client.data.jsonhelpers.CustomCreativeTab;
import me.hypherionmc.morecreativetabs.client.data.jsonhelpers.DisabledTabsJsonHelper;
import me.hypherionmc.morecreativetabs.mixin.CreativeModeTabAccessor;
import me.hypherionmc.morecreativetabs.platform.PlatformServices;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static me.hypherionmc.morecreativetabs.util.CreativeTabUtils.fileToTab;
import static me.hypherionmc.morecreativetabs.util.CreativeTabUtils.getItemStack;

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

    /* Tabs that replace existing, Non-Custom tabs */
    public static HashMap<String, Pair<CustomCreativeTab, List<ItemStack>>> replaced_tabs = new HashMap<>();

    /**
     * Load and process the resource/data pack
     * @param entries - The found entries
     * @param creator - The "helper" class that creates the custom tab
     */
    public static void loadEntries(Map<ResourceLocation, Resource> entries, TabCreator creator) {
        entries.forEach((location, resource) -> {
            ModConstants.logger.info("Processing " + location.toString());

            try (InputStream stream = resource.open()) {
                CustomCreativeTab json = new Gson().fromJson(new InputStreamReader(stream), CustomCreativeTab.class);
                ArrayList<ItemStack> tabItems = new ArrayList<>();

                /* Check if the tab is enabled and should be loaded */
                if (json.tab_enabled) {

                    /* Loop over all the Item Stack entries */
                    json.tab_items.forEach(item -> {
                        if (item.name.equalsIgnoreCase("existing")) {
                            json.keepExisting = true;
                        } else {
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
                                        ModConstants.logger.error("Failed to Process NBT for Item " + item.name, e);
                                    }
                                }

                                /* Store the item for adding to the creative tab */
                                tabItems.add(stack);
                            }
                        }
                    });

                    /* Check if tab replaces an existing tab */
                    if (json.replace) {
                        replaced_tabs.put(fileToTab(location.getPath()), Pair.of(json, tabItems));
                    } else {
                        /* Create the actual tab and store it */
                        custom_tabs.add(creator.createTab(json, tabItems));
                    }
                }
            } catch (Exception e) {
                ModConstants.logger.error("Failed to process creative tab", e);
            }
        });

        reOrderTabs();
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
               ModConstants.logger.error("Failed to process disabled tabs for " + location, e);
           }
       });
    }

    /**
     * This function is used to filter out disabled tabs
     */
    private static void reOrderTabs() {
        List<CreativeModeTab> filteredTabs = new ArrayList<>();
        AtomicInteger id = new AtomicInteger(0);

        for (CreativeModeTab tab : CreativeModeTab.TABS) {
            if (!disabled_tabs.contains(tab.getRecipeFolderName())) {
                ((CreativeModeTabAccessor) tab).setId(id.getAndIncrement());
                filteredTabs.add(tab);
            }

            // Don't disable Survival Inventory or Custom Tabs
            if (tab == CreativeModeTab.TAB_INVENTORY || custom_tabs.contains(tab)) {
                if (!filteredTabs.contains(tab)) {
                    ((CreativeModeTabAccessor) tab).setId(id.getAndIncrement());
                    filteredTabs.add(tab);
                }
            }
        }

        PlatformServices.helper.setNewTabs(filteredTabs.toArray(new CreativeModeTab[0]));
    }

    /**
     * Clear all cached data for reloading
     */
    public static void clearTabs() {
        hidden_stacks.clear();
        disabled_tabs.clear();
        PlatformServices.helper.setNewTabs(tabs_before);
        custom_tabs.clear();
        replaced_tabs.clear();
    }
}
