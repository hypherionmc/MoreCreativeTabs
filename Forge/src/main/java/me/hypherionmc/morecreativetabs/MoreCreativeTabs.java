package me.hypherionmc.morecreativetabs;

import me.hypherionmc.morecreativetabs.client.CustomCreativeTabManager;
import me.hypherionmc.morecreativetabs.client.TabCreator;
import me.hypherionmc.morecreativetabs.client.TabJsonHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Collection;
import java.util.List;

@Mod(ModConstants.MOD_ID)
public class MoreCreativeTabs {

    public MoreCreativeTabs() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupComplete);
    }

    // Run after all mods have completed their setup
    private void setupComplete(FMLLoadCompleteEvent event) {
        Logger.info("Checking for custom creative tabs");
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ResourceManager manager = Minecraft.getInstance().getResourceManager();
            Collection<ResourceLocation> customTabs = manager.listResources("morecreativetabs", path -> path.contains(".json") && !path.contains("disabled_tabs"));
            Collection<ResourceLocation> disabledTabs = manager.listResources("morecreativetabs", path -> path.contains("disabled_tabs.json"));

            CustomCreativeTabManager.loadEntries(manager, customTabs, new TabCreator() {
                @Override
                public CreativeModeTab createTab(TabJsonHelper jsonHelper, List<ItemStack> stacks) {
                    return CustomCreativeTabManager.defaultTabCreator(jsonHelper, stacks);
                }
            });
            if (!disabledTabs.isEmpty()) {
                CustomCreativeTabManager.loadDisabledTabs(manager, disabledTabs.stream().findFirst().get());
            }
        });
    }
}
