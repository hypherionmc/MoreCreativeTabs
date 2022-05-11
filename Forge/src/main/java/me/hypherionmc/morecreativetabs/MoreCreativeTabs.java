package me.hypherionmc.morecreativetabs;

import com.mojang.brigadier.CommandDispatcher;
import me.hypherionmc.morecreativetabs.client.data.jsonhelpers.TabJsonHelper;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager;
import me.hypherionmc.morecreativetabs.client.tabs.TabCreator;
import me.hypherionmc.morecreativetabs.client.tabs.TabEvents;
import me.hypherionmc.morecreativetabs.common.commands.ShowTabNamesCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Collection;
import java.util.List;

@Mod(ModConstants.MOD_ID)
public class MoreCreativeTabs {

    public MoreCreativeTabs() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            CustomCreativeTabManager.setTabEvents(new TabEvents() {
                @Override
                public void setNewTabs(CreativeModeTab[] tabs) {
                    CreativeModeTab.TABS = tabs;
                }

                @Override
                public void reloadTabs() {
                    MoreCreativeTabs.reloadTabs();
                }
            });
        });

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupComplete);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {
        Logger.info("Registering Commands");
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        ShowTabNamesCommand.register(dispatcher);
    }

    @SubscribeEvent
    public void onChatEvent(ClientChatEvent event) {
        if (event.getMessage().startsWith("reloadTabs")) {
            reloadTabs();
        }
    }

    // Run after all mods have completed their setup
    private void setupComplete(FMLLoadCompleteEvent event) {
        reloadTabs();
    }

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

            CustomCreativeTabManager.loadEntries(manager, customTabs, new TabCreator() {
                @Override
                public CreativeModeTab createTab(TabJsonHelper jsonHelper, List<ItemStack> stacks) {
                    return CustomCreativeTabManager.defaultTabCreator(jsonHelper, stacks);
                }
            });

        });
    }
}
