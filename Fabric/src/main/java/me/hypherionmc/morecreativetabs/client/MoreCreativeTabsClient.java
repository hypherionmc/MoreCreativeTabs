package me.hypherionmc.morecreativetabs.client;

import me.hypherionmc.morecreativetabs.Logger;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
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
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation("morecreativetabs", "tabs");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                if (!hasRun) {
                    Logger.info("Checking for custom creative tabs");
                    ResourceManager manager = Minecraft.getInstance().getResourceManager();
                    Collection<ResourceLocation> customTabs = manager.listResources("morecreativetabs", path -> path.contains(".json") && !path.contains("disabled_tabs"));
                    Collection<ResourceLocation> disabledTabs = manager.listResources("morecreativetabs", path -> path.contains("disabled_tabs.json"));

                    CustomCreativeTabManager.loadEntries(manager, customTabs, new TabCreator() {
                        @Override
                        public CreativeModeTab createTab(TabJsonHelper jsonHelper, List<ItemStack> stacks) {
                            ((ItemGroupExtensions) CreativeModeTab.TAB_BUILDING_BLOCKS).fabric_expandArray();
                            return new CreativeModeTab(CreativeModeTab.TABS.length - 1, String.format("%s.%s", "morecreativetabs", jsonHelper.tab_name)) {
                                @Override
                                public ItemStack makeIcon() {
                                    AtomicReference<ItemStack> icon = new AtomicReference<>(ItemStack.EMPTY);
                                    Registry.ITEM.getOptional(new ResourceLocation(jsonHelper.tab_icon)).ifPresent(item -> {
                                        icon.set(new ItemStack(item));
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
                    if (!disabledTabs.isEmpty()) {
                        CustomCreativeTabManager.loadDisabledTabs(manager, disabledTabs.stream().findFirst().get());
                    }
                    hasRun = true;
                }
            }
        });
    }
}
