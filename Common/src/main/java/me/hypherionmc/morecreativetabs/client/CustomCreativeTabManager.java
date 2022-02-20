package me.hypherionmc.morecreativetabs.client;

import com.google.gson.Gson;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.hypherionmc.morecreativetabs.Logger;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class CustomCreativeTabManager {

    public static Set<Item> hidden_stacks = new HashSet<>();
    public static Set<CreativeModeTab> custom_tabs = new HashSet<>();
    public static Set<String> disabled_tabs = new HashSet<>();

    public static void loadEntries(ResourceManager manager, Collection<ResourceLocation> entries, TabCreator creator) {
        for (ResourceLocation location : entries) {
            Logger.info("Processing " + location.toString());

            try (InputStream stream = manager.getResource(location).getInputStream()) {
                TabJsonHelper json = new Gson().fromJson(new InputStreamReader(stream), TabJsonHelper.class);

                if (json.tab_enabled) {
                    ArrayList<ItemStack> tabItems = new ArrayList<>();

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
                Registry.ITEM.getOptional(new ResourceLocation(json.tab_icon)).ifPresent(item -> {
                    icon.set(new ItemStack(item));
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

}
