package me.hypherionmc.morecreativetabs.client;

import me.hypherionmc.morecreativetabs.Logger;
import me.hypherionmc.morecreativetabs.client.commands.ReloadTabsCommand;
import me.hypherionmc.morecreativetabs.client.data.jsonhelpers.TabJsonHelper;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager;
import me.hypherionmc.morecreativetabs.client.tabs.TabCreator;
import me.hypherionmc.morecreativetabs.client.tabs.TabEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MoreCreativeTabsClient implements ClientModInitializer {

    private boolean hasRun = false;

    @Override
    public void onInitializeClient() {
        CustomCreativeTabManager.setTabEvents(new TabEvents() {
            @Override
            public void setNewTabs(CreativeModeTab[] tabs) {
                CreativeModeTab.TABS = tabs;
            }

            @Override
            public void reloadTabs() {
                MoreCreativeTabsClient.reloadTabs();
            }

        });

        ReloadTabsCommand.register(ClientCommandManager.DISPATCHER);

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation("morecreativetabs", "tabs");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                if (!hasRun) {
                    reloadTabs();
                    hasRun = true;
                }
            }
        });
    }

    public static void reloadTabs() {
        Logger.info("Checking for custom creative tabs");
        CustomCreativeTabManager.clearTabs();
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        Collection<ResourceLocation> customTabs = manager.listResources("morecreativetabs", path -> path.contains(".json") && !path.contains("disabled_tabs"));
        Collection<ResourceLocation> disabledTabs = manager.listResources("morecreativetabs", path -> path.contains("disabled_tabs.json"));

        if (!disabledTabs.isEmpty()) {
            CustomCreativeTabManager.loadDisabledTabs(manager, disabledTabs.stream().findFirst().get());
        }

        CustomCreativeTabManager.loadEntries(manager, customTabs, new TabCreator() {
            @Override
            public CreativeModeTab createTab(TabJsonHelper jsonHelper, List<ItemStack> stacks) {
                ((ItemGroupExtensions) CreativeModeTab.TAB_BUILDING_BLOCKS).fabric_expandArray();
                return new CreativeModeTab(CreativeModeTab.TABS.length - 1, String.format("%s.%s", "morecreativetabs", jsonHelper.tab_name)) {
                    @Override
                    public ItemStack makeIcon() {
                        AtomicReference<ItemStack> icon = new AtomicReference<>(ItemStack.EMPTY);
                        TabJsonHelper.TabIcon tabIcon = new TabJsonHelper.TabIcon();

                        if (jsonHelper.tab_stack != null) {
                            tabIcon = jsonHelper.tab_stack;
                        } else if (jsonHelper.tab_icon != null) {
                            tabIcon = new TabJsonHelper.TabIcon();
                            tabIcon.name = jsonHelper.tab_icon;
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
                    public void fillItemList(NonNullList<ItemStack> stackss) {
                        stackss.clear();
                        stackss.addAll(stacks);
                    }

                    @Override
                    public String getBackgroundSuffix() {
                        return jsonHelper.tab_background == null ? super.getBackgroundSuffix() : jsonHelper.tab_background;
                    }
                };
            }
        });
    }

}
