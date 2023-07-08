package me.hypherionmc.morecreativetabs.client.tabs;

import com.google.gson.Gson;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.hypherionmc.morecreativetabs.ModConstants;
import me.hypherionmc.morecreativetabs.client.data.CustomCreativeTab;
import me.hypherionmc.morecreativetabs.client.data.DisabledTabsJsonHelper;
import me.hypherionmc.morecreativetabs.client.data.OrderedTabs;
import me.hypherionmc.morecreativetabs.mixin.accessors.CreativeModeTabAccessor;
import me.hypherionmc.morecreativetabs.mixin.accessors.CreativeModeTabsAccessor;
import me.hypherionmc.morecreativetabs.platform.PlatformServices;
import net.minecraft.core.registries.BuiltInRegistries;
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

import static me.hypherionmc.morecreativetabs.utils.CreativeTabUtils.*;

/**
 * @author HypherionSA
 * Class to manage all custom tabs and items
 */
public class CustomCreativeTabRegistry {

    public static final Gson GSON = new Gson();

    /* Items to remove from their old tabs */
    public static Set<Item> hidden_stacks = new HashSet<>();

    /* List of Custom Defined tabs */
    public static Set<CreativeModeTab> custom_tabs = new HashSet<>();

    /* List of Disabled Tabs */
    public static Set<String> disabled_tabs = new HashSet<>();

    /* List of Reordered Tabs */
    public static Set<String> reordered_tabs = new LinkedHashSet<>();

    /* Should the Name or Registry name of the tab be showed */
    public static boolean showNames = false;

    /* A fixed backup of all creative tabs, before custom ones are added */
    public static List<CreativeModeTab> tabs_before;

    /* Tabs that replace existing, Non-Custom tabs */
    public static HashMap<String, Pair<CustomCreativeTab, List<ItemStack>>> replaced_tabs = new HashMap<>();

    /* Holds the items to display in Custom Tabs */
    public static HashMap<CreativeModeTab, List<ItemStack>> tab_items = new HashMap<>();

    public static List<CreativeModeTab> current_tabs = new LinkedList<>();

    /**
     * Load and process the resource/data pack
     * @param entries - The found entries
     */
    public static void processEntries(Map<ResourceLocation, Resource> entries) {
        entries.forEach((location, resource) -> {
            ModConstants.logger.info("Processing {}", location.toString());

            try (InputStream stream = resource.open()) {
                CustomCreativeTab json = GSON.fromJson(new InputStreamReader(stream), CustomCreativeTab.class);
                ArrayList<ItemStack> tabItems = new ArrayList<>();

                /* Check if the tab is enabled and should be loaded */
                if (json.tab_enabled) {

                    /* Loop over all the Item Stack entries */
                    json.tab_items.forEach(item -> {
                        if (item.name.equalsIgnoreCase("existing"))
                            json.keepExisting = true;

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
                                    ModConstants.logger.error("Failed to Process NBT for Item {}", item.name, e);
                                }
                            }

                            /* Store the item for adding to the creative tab */
                            tabItems.add(stack);
                        }
                    });

                    /* Check if tab replaces an existing tab */
                    if (json.replace) {
                        replaced_tabs.put(fileToTab(location.getPath()).toLowerCase(), Pair.of(json, tabItems));
                    } else {
                        /* Create the actual tab and store it */
                        CreativeModeTab.Builder builder = new CreativeModeTab.Builder(null, -1);
                        builder.title(Component.translatable(prefix(json.tab_name)));
                        builder.icon(() -> makeTabIcon(json));

                        if (json.tab_background != null) {
                            builder.backgroundSuffix(json.tab_background);
                        }

                        CreativeModeTab tab = builder.build();
                        custom_tabs.add(tab);
                        tab_items.put(tab, tabItems);
                    }

                }

            } catch (Exception e) {
                ModConstants.logger.error("Failed to process creative tab", e);
            }
        });

        reorderTabs();
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
     * Load ordered tabs for later processing
     */
    public static void loadOrderedTabs(Map<ResourceLocation, Resource> resourceMap) {
        resourceMap.forEach((location, resource) -> {
            ModConstants.logger.info("Processing " + location.toString());
            try (InputStream stream = resource.open()) {
                OrderedTabs tabs = new Gson().fromJson(new InputStreamReader(stream), OrderedTabs.class);
                reordered_tabs.addAll(tabs.tabs);
            } catch (Exception e) {
                ModConstants.logger.error("Failed to process ordered tabs for " + location, e);
            }
        });
    }

    /**
     * This function is used to filter out disabled tabs and apply order to the tabs
     */
    private static void reorderTabs() {
        List<CreativeModeTab> oldTabs = new ArrayList<>();
        oldTabs.addAll(tabs_before);
        oldTabs.addAll(custom_tabs);

        Set<CreativeModeTab> filteredTabs = new LinkedHashSet<>();
        boolean addExisting = false;

        if (!reordered_tabs.isEmpty()) {
            for (String orderedTab : reordered_tabs) {
                if (!orderedTab.equalsIgnoreCase("existing")) {
                    oldTabs.stream()
                            .filter(tab -> getTabKey(((CreativeModeTabAccessor)tab).getInternalDisplayName()).equals(orderedTab))
                            .findFirst().ifPresent(pTab -> processTab(pTab, filteredTabs));
                } else {
                    addExisting = true;
                }
            }
        } else {
            addExisting = true;
        }


        if (addExisting) {
            for (CreativeModeTab tab : oldTabs) {
                processTab(tab, filteredTabs);
            }
        }

        // Don't disable the Survival Inventory
        filteredTabs.add(BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabsAccessor.getInventoryTab()));

        // Don't disable Custom Tabs
        filteredTabs.addAll(custom_tabs);

        current_tabs = filteredTabs.stream().toList();

        PlatformServices.TAB_HELPER.updateCreativeTabs(current_tabs);
    }

    // Just used to remove duplicate code
    private static void processTab(CreativeModeTab tab, Set<CreativeModeTab> filteredTabs) {
        if (!disabled_tabs.contains(getTabKey(((CreativeModeTabAccessor)tab).getInternalDisplayName()))) {
            filteredTabs.add(tab);
        }
    }

    /**
     * Clear all cached data for reloading
     */
    public static void clearTabs() {
        hidden_stacks.clear();
        disabled_tabs.clear();
        reordered_tabs.clear();
        current_tabs = tabs_before;
        PlatformServices.TAB_HELPER.updateCreativeTabs(current_tabs);
        custom_tabs.clear();
        replaced_tabs.clear();
    }
}
