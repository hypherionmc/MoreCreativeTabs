package me.hypherionmc.morecreativetabs.client;

import com.mojang.brigadier.arguments.BoolArgumentType;
import me.hypherionmc.morecreativetabs.Logger;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager;
import me.hypherionmc.morecreativetabs.platform.PlatformServices;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CreativeModeTab;

import java.util.Collection;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class MoreCreativeTabsClient implements ClientModInitializer {

    private boolean hasRun = false;

    @Override
    public void onInitializeClient() {
        /* Register Client Commands */
        Logger.info("Registering Commands");
        ClientCommandManager.DISPATCHER.register(literal("mct").then(literal("showTabNames")
                .then(argument("enabled", bool()).executes(context -> {
                    boolean enabled = BoolArgumentType.getBool(context, "enabled");
                    CustomCreativeTabManager.showNames = enabled;
                    context.getSource().sendFeedback(enabled ? new TextComponent("Showing tab registry names") : new TextComponent("Showing tab names"));
                    return 1;
                }))).then(literal("reloadTabs").executes(ctx -> {
                    PlatformServices.helper.reloadTabs();
                    ctx.getSource().sendFeedback(new TextComponent("Reloaded Custom Tabs"));
                    return 1;
                }))
        );

        /* Load initial entries and cache old tabs */
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
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
        });
    }

    /**
     * Called to reload all creative tabs
     */
    public static void reloadTabs() {
        Logger.info("Checking for custom creative tabs");
        CustomCreativeTabManager.clearTabs();
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        Collection<ResourceLocation> customTabs = manager.listResources("morecreativetabs", path -> path.endsWith(".json") && !path.contains("disabled_tabs"));
        Collection<ResourceLocation> disabledTabs = manager.listResources("morecreativetabs", path -> path.contains("disabled_tabs.json"));

        if (!disabledTabs.isEmpty()) {
            CustomCreativeTabManager.loadDisabledTabs(manager, disabledTabs.stream().findFirst().get());
        }

        CustomCreativeTabManager.loadEntries(manager, customTabs, new FabricTabCreator());
    }

}
