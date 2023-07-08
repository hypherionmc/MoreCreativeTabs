package me.hypherionmc.morecreativetabs.client;

import me.hypherionmc.morecreativetabs.ModConstants;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import me.hypherionmc.morecreativetabs.mixin.accessors.CreativeModeTabsAccessor;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author HypherionSA
 * Helper class to load our resource packs
 */
public class FabricResourceLoader implements SimpleSynchronousResourceReloadListener {

    private boolean hasRun = false;

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation("morecreativetabs", "tabs");
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        if (!hasRun) {
            BuiltInRegistries.CREATIVE_MODE_TAB
            CustomCreativeTabRegistry.tabs_before = CreativeModeTabsAccessor.getOldTabs();
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
        CustomCreativeTabRegistry.clearTabs();
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        Map<ResourceLocation, Resource> customTabs = manager.listResources("morecreativetabs",
                path -> path.getPath().endsWith(".json") && !path.getPath().contains("disabled_tabs")
                        && !path.getPath().contains("ordered_tabs"));

        Map<ResourceLocation, Resource> disabledTabs = manager.listResources("morecreativetabs", path -> path.getPath().contains("disabled_tabs.json"));
        Map<ResourceLocation, Resource> orderedTabs = manager.listResources("morecreativetabs", path -> path.getPath().contains("ordered_tabs.json"));

        if (!disabledTabs.isEmpty()) {
            CustomCreativeTabRegistry.loadDisabledTabs(disabledTabs);
        }

        if (!orderedTabs.isEmpty()) {
            CustomCreativeTabRegistry.loadOrderedTabs(orderedTabs);
        }

        CustomCreativeTabRegistry.processEntries(customTabs);
    }
}
