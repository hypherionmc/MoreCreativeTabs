package me.hypherionmc.morecreativetabs.client.tabs;

import com.google.gson.Gson;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.hypherionmc.morecreativetabs.ModConstants;
import me.hypherionmc.morecreativetabs.client.data.jsonhelpers.DisabledTabsJsonHelper;
import me.hypherionmc.morecreativetabs.client.data.jsonhelpers.TabJsonHelper;
import me.hypherionmc.morecreativetabs.platform.PlatformServices;
import me.hypherionmc.morecreativetabs.util.CreativeTabUtils;
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
import org.apache.commons.lang3.tuple.Pair;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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

    public static HashMap<String, Pair<TabJsonHelper, List<ItemStack>>> replaced_tabs = new HashMap<>();

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

                    /* Check if tab replaces an existing tab */
                    if (json.replaces != null && !json.replaces.isEmpty()) {
                        replaced_tabs.put(json.replaces, Pair.of(json, tabItems));
                    } else {
                        /* Create the actual tab and store it */
                        custom_tabs.add(creator.createTab(json, tabItems));
                    }
                }
            } catch (Exception e) {
                ModConstants.logger.error("Failed to process creative tab");
                e.printStackTrace();
            }
        });
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
