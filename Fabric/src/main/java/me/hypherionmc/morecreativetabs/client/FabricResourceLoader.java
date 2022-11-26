package me.hypherionmc.morecreativetabs.client;

import me.hypherionmc.morecreativetabs.ModConstants;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CreativeModeTab;

import java.util.Map;

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
        }
    }

    /**
     * Called to reload all creative tabs
     */
    public static void reloadTabs() {
        ModConstants.logger.info("Checking for custom creative tabs");
        CustomCreativeTabManager.clearTabs();
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        Map<ResourceLocation, Resource> customTabs = manager.listResources("morecreativetabs", path -> path.getPath().endsWith(".json") && !path.getPath().contains("disabled_tabs"));
        Map<ResourceLocation, Resource> disabledTabs = manager.listResources("morecreativetabs", path -> path.getPath().contains("disabled_tabs.json"));

        if (!disabledTabs.isEmpty()) {
            CustomCreativeTabManager.loadDisabledTabs(disabledTabs);
        }

        CustomCreativeTabManager.loadEntries(customTabs, new FabricTabCreator());
    }
}
