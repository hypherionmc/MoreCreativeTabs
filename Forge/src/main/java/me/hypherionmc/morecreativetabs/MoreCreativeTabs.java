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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Map;

/**
 * @author HypherionSA
 */
@Mod(ModConstants.MOD_ID)
public class MoreCreativeTabs {

    public MoreCreativeTabs() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupComplete);
    }

    // Run after all mods have completed their setup
    private void setupComplete(FMLLoadCompleteEvent event) {
        CustomCreativeTabManager.tabs_before = CreativeModeTab.TABS;
        reloadTabs();
    }

    /**
     * Called to reload all creative tabs
     */
    public static void reloadTabs() {
        ModConstants.logger.info("Checking for custom creative tabs");
        CustomCreativeTabManager.clearTabs();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ResourceManager manager = Minecraft.getInstance().getResourceManager();
            Map<ResourceLocation, Resource> customTabs = manager.listResources("morecreativetabs", path -> path.getPath().endsWith(".json") && !path.getPath().contains("disabled_tabs"));
            Map<ResourceLocation, Resource> disabledTabs = manager.listResources("morecreativetabs", path -> path.getPath().contains("disabled_tabs.json"));

            if (!disabledTabs.isEmpty()) {
                CustomCreativeTabManager.loadDisabledTabs(disabledTabs);
            }

            CustomCreativeTabManager.loadEntries(customTabs, ((jsonHelper, stacks) -> CreativeTabUtils.defaultTabCreator(-1, jsonHelper, stacks)));
        });
    }
}
