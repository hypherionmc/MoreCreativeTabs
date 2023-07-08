package me.hypherionmc.morecreativetabs;

import me.hypherionmc.morecreativetabs.client.ForgeResourceReloader;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager;
import me.hypherionmc.morecreativetabs.util.CreativeTabUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Collection;

/**
 * @author HypherionSA
 */
@Mod(ModConstants.MOD_ID)
public class MoreCreativeTabs {

    private static boolean hasRun = false;

    public MoreCreativeTabs() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "", (a, b) -> true));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerReloadListener);
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
            Collection<ResourceLocation> customTabs = manager.listResources("morecreativetabs",
                    path -> path.endsWith(".json") && !path.contains("disabled_tabs")
                            && !path.equals("ordered_tabs"));

            Collection<ResourceLocation> disabledTabs = manager.listResources("morecreativetabs", path -> path.contains("disabled_tabs.json"));
            Collection<ResourceLocation> orderedTabs = manager.listResources("morecreativetabs", path -> path.contains("ordered_tabs.json"));

            if (!disabledTabs.isEmpty()) {
                CustomCreativeTabManager.loadDisabledTabs(manager, disabledTabs);
            }

            if (!orderedTabs.isEmpty()) {
                CustomCreativeTabManager.loadOrderedTabs(manager, orderedTabs);
            }

            CustomCreativeTabManager.loadEntries(manager, customTabs, ((jsonHelper, stacks) -> CreativeTabUtils.defaultTabCreator(-1, jsonHelper, stacks)));
        });
    }

    public void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new ForgeResourceReloader());
    }
}
