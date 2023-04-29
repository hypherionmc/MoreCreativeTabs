package me.hypherionmc.morecreativetabs;

import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager;
import me.hypherionmc.morecreativetabs.util.CreativeTabUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

/**
 * @author HypherionSA
 */
@Mod(ModConstants.MOD_ID)
public class MoreCreativeTabs {

    private static boolean hasRun = false;

    public MoreCreativeTabs() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "", (a, b) -> true));
    }


    public static void reloadResources() {
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
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ResourceManager manager = Minecraft.getInstance().getResourceManager();
            Map<ResourceLocation, Resource> customTabs = manager.listResources("morecreativetabs",
                    path -> path.getPath().endsWith(".json") && !path.getPath().contains("disabled_tabs")
                            && !path.getPath().equals("ordered_tabs"));

            Map<ResourceLocation, Resource> disabledTabs = manager.listResources("morecreativetabs", path -> path.getPath().contains("disabled_tabs.json"));
            Map<ResourceLocation, Resource> orderedTabs = manager.listResources("morecreativetabs", path -> path.getPath().contains("ordered_tabs.json"));

            if (!disabledTabs.isEmpty()) {
                CustomCreativeTabManager.loadDisabledTabs(disabledTabs);
            }

            if (!orderedTabs.isEmpty()) {
                CustomCreativeTabManager.loadOrderedTabs(orderedTabs);
            }

            CustomCreativeTabManager.loadEntries(customTabs, ((jsonHelper, stacks) -> CreativeTabUtils.defaultTabCreator(-1, jsonHelper, stacks)));
        });
    }
}
