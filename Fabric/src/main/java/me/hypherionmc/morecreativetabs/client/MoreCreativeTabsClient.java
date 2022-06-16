package me.hypherionmc.morecreativetabs.client;

import com.mojang.brigadier.arguments.BoolArgumentType;
import me.hypherionmc.morecreativetabs.ModConstants;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager;
import me.hypherionmc.morecreativetabs.platform.PlatformServices;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CreativeModeTab;

import java.util.Map;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class MoreCreativeTabsClient implements ClientModInitializer {

    private boolean hasRun = false;

    @Override
    public void onInitializeClient() {
        /* Register Client Commands */
        ClientCommandRegistrationCallback.EVENT.register((phase, listener) -> {
            ModConstants.logger.info("Registering Commands");
            ClientCommandManager.getActiveDispatcher().register(literal("mct").then(literal("showTabNames")
                            .then(argument("enabled", bool()).executes(context -> {
                                boolean enabled = BoolArgumentType.getBool(context, "enabled");
                                CustomCreativeTabManager.showNames = enabled;
                                context.getSource().sendFeedback(enabled ? Component.literal("Showing tab registry names") : Component.literal("Showing tab names"));
                                return 1;
                            }))).then(literal("reloadTabs").executes(ctx -> {
                        PlatformServices.helper.reloadTabs();
                        ctx.getSource().sendFeedback(Component.literal("Reloaded Custom Tabs"));
                        return 1;
                    }))
            );
        });

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
