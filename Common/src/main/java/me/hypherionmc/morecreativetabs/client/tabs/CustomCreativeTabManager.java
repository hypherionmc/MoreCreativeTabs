package me.hypherionmc.morecreativetabs.client.tabs;

import com.google.gson.Gson;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.hypherionmc.morecreativetabs.Logger;
import me.hypherionmc.morecreativetabs.client.data.jsonhelpers.DisabledTabsJsonHelper;
import me.hypherionmc.morecreativetabs.client.data.jsonhelpers.TabJsonHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class CustomCreativeTabManager {

    public static Set<Item> hidden_stacks = new HashSet<>();
    public static Set<CreativeModeTab> custom_tabs = new HashSet<>();
    public static Set<String> disabled_tabs = new HashSet<>();
    public static boolean showNames = false;

    public static HashMap<ItemStack, Pair<String, String>> customNames = new HashMap<>();

    public static TabEvents tabEvents = null;

    public static void loadEntries(ResourceManager manager, Collection<ResourceLocation> entries, TabCreator creator) {
        for (ResourceLocation location : entries) {
            Logger.info("Processing " + location.toString());

            try (InputStream stream = manager.getResource(location).getInputStream()) {
                TabJsonHelper json = new Gson().fromJson(new InputStreamReader(stream), TabJsonHelper.class);
                ArrayList<ItemStack> tabItems = new ArrayList<>();

                if (json.tab_enabled) {

                    json.tab_items.forEach(item -> {
                        ItemStack stack = getItemStack(item.name);
                        if (stack != ItemStack.EMPTY) {
                            if (item.hide_old_tab) {
                                hidden_stacks.add(stack.getItem());
                            }
                            if (item.nbt != null && !item.nbt.isEmpty()) {
                                try {
                                    CompoundTag tag = TagParser.parseTag(item.nbt);
                                    stack.setTag(tag);
                                } catch (CommandSyntaxException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (stack.getTag() != null && stack.getTag().contains("customName")) {
                                customNames.put(stack, Pair.of("morecreativetabs." + json.tab_name, stack.getTag().getString("customName")));
                            }
                            tabItems.add(stack);
                        }
                    });

                    custom_tabs.add(creator.createTab(json, tabItems));
                }

            } catch (Exception e) {
                Logger.error("Failed to process creative tab");
                e.printStackTrace();
            }
        }
    }

    public static CreativeModeTab defaultTabCreator(TabJsonHelper json, List<ItemStack> tabItems) {
        CreativeModeTab customTab = new CreativeModeTab(-1, "morecreativetabs." + json.tab_name) {

            @Override
            public String getBackgroundSuffix() {
                return json.tab_background == null ? super.getBackgroundSuffix() : json.tab_background;
            }

            @Override
            public ItemStack makeIcon() {
                AtomicReference<ItemStack> icon = new AtomicReference<>(ItemStack.EMPTY);
                TabJsonHelper.TabIcon tabIcon = new TabJsonHelper.TabIcon();

                if (json.tab_stack != null) {
                    tabIcon = json.tab_stack;
                } else if (json.tab_icon != null) {
                    tabIcon = new TabJsonHelper.TabIcon();
                    tabIcon.name = json.tab_icon;
                }

                TabJsonHelper.TabIcon finalTabIcon = tabIcon;
                Registry.ITEM.getOptional(new ResourceLocation(tabIcon.name)).ifPresent(item -> {
                    if (finalTabIcon.nbt != null) {
                        CompoundTag tag = new CompoundTag();
                        try {
                            tag = TagParser.parseTag(finalTabIcon.nbt);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ItemStack stack = new ItemStack(item);
                        stack.setTag(tag);
                        icon.set(stack);
                    } else {
                        icon.set(new ItemStack(item));
                    }
                });
                return icon.get();
            }

            @Override
            public void fillItemList(NonNullList<ItemStack> itemStacks) {
                itemStacks.clear();
                itemStacks.addAll(tabItems);
            }
        };
        return customTab;
    }

    public static void loadDisabledTabs(ResourceManager manager, ResourceLocation location) {
        Logger.info("Processing " + location.toString());

        try (InputStream stream = manager.getResource(location).getInputStream()) {
            DisabledTabsJsonHelper json = new Gson().fromJson(new InputStreamReader(stream), DisabledTabsJsonHelper.class);
            disabled_tabs.addAll(json.disabled_tabs);
        } catch (Exception e) {
            Logger.error("Failed to process disabled tabs");
            e.printStackTrace();
        }
    }

    private static ItemStack getItemStack(String jsonItem) {
        Optional<Item> itemOptional = Registry.ITEM.getOptional(new ResourceLocation(jsonItem));
        return itemOptional.map(ItemStack::new).orElse(ItemStack.EMPTY);
    }

    public static void setTabEvents(TabEvents tabEvents) {
        CustomCreativeTabManager.tabEvents = tabEvents;
    }

    public static void clearTabs() {
        hidden_stacks.clear();
        disabled_tabs.clear();
        customNames.clear();
        CreativeModeTab[] oldTabs = CreativeModeTab.TABS;
        List<CreativeModeTab> newTabs = new ArrayList<>();

        for (CreativeModeTab oldTab : oldTabs) {
            if (custom_tabs.stream().noneMatch(tab -> tab.getRecipeFolderName().equalsIgnoreCase(oldTab.getRecipeFolderName()))) {
                newTabs.add(oldTab);
            }
        }
        if (tabEvents != null) {
            tabEvents.setNewTabs(newTabs.toArray(new CreativeModeTab[0]));
        }
        custom_tabs.clear();
    }

}
