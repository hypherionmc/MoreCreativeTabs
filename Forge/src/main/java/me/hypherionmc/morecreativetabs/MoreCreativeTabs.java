package me.hypherionmc.morecreativetabs;

import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Collection;

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
        Logger.info("Checking for custom creative tabs");
        CustomCreativeTabManager.clearTabs();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ResourceManager manager = Minecraft.getInstance().getResourceManager();
            Collection<ResourceLocation> customTabs = manager.listResources("morecreativetabs", path -> path.endsWith(".json") && !path.contains("disabled_tabs"));
            Collection<ResourceLocation> disabledTabs = manager.listResources("morecreativetabs", path -> path.contains("disabled_tabs.json"));

            if (!disabledTabs.isEmpty()) {
                CustomCreativeTabManager.loadDisabledTabs(manager, disabledTabs.stream().findFirst().get());
            }

            CustomCreativeTabManager.loadEntries(manager, customTabs, CustomCreativeTabManager::defaultTabCreator);
        });
    }
}
