package me.hypherionmc.morecreativetabs.client;

import com.google.common.collect.ImmutableList;
import me.hypherionmc.morecreativetabs.ModConstants;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.minecraft.world.item.CreativeModeTabs.*;

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
            List<ResourceKey<CreativeModeTab>> VANILLA_TABS = ImmutableList.of(BUILDING_BLOCKS, COLORED_BLOCKS, NATURAL_BLOCKS, FUNCTIONAL_BLOCKS, REDSTONE_BLOCKS, HOTBAR, SEARCH, TOOLS_AND_UTILITIES, COMBAT, FOOD_AND_DRINKS, INGREDIENTS, SPAWN_EGGS, INVENTORY);
            List<CreativeModeTab> beforeTabs = new ArrayList<>();
            VANILLA_TABS.forEach(t -> beforeTabs.add(BuiltInRegistries.CREATIVE_MODE_TAB.get(t)));
            CreativeModeTab tab = BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabs.OP_BLOCKS);

            BuiltInRegistries.CREATIVE_MODE_TAB.stream().toList().forEach(t -> {
                if (t != tab && !beforeTabs.contains(t))
                    beforeTabs.add(t);
            });

            CustomCreativeTabRegistry.tabs_before = beforeTabs;
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
