package me.hypherionmc.morecreativetabs.client;

import me.hypherionmc.morecreativetabs.ModConstants;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CreativeModeTab;

import java.util.Collection;

/**
 * Helper class to load our resource packs
 */
public class FabricResourceLoader implements SimpleSynchronousResourceReloadListener {

    private boolean hasRun = false;

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation("morecreativetabs", "tabs");
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        if (!hasRun) {
            CustomCreativeTabManager.tabs_before = CreativeModeTab.TABS;
            reloadTabs();
            hasRun = true;
        } else {
            reloadTabs();
        }
    }

    /**
     * Called to reload all creative tabs
     */
    public static void reloadTabs() {
        ModConstants.logger.info("Checking for custom creative tabs");
        CustomCreativeTabManager.clearTabs();
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        Collection<ResourceLocation> customTabs = manager.listResources("morecreativetabs",
                path -> path.endsWith(".json") && !path.contains("disabled_tabs")
                        && !path.contains("ordered_tabs"));

        Collection<ResourceLocation> disabledTabs = manager.listResources("morecreativetabs", path -> path.contains("disabled_tabs.json"));
        Collection<ResourceLocation> orderedTabs = manager.listResources("morecreativetabs", path -> path.contains("ordered_tabs.json"));

        if (!disabledTabs.isEmpty()) {
            CustomCreativeTabManager.loadDisabledTabs(manager, disabledTabs);
        }

        if (!orderedTabs.isEmpty()) {
            CustomCreativeTabManager.loadOrderedTabs(manager, orderedTabs);
        }

        CustomCreativeTabManager.loadEntries(manager, customTabs, new FabricTabCreator());
    }
}
