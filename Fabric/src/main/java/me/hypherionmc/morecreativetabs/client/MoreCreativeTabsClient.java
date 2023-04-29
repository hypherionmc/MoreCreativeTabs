package me.hypherionmc.morecreativetabs.client;

import com.mojang.brigadier.arguments.BoolArgumentType;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager;
import me.hypherionmc.morecreativetabs.platform.PlatformServices;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.packs.PackType;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;

public class MoreCreativeTabsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        /* Register Client Commands */
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("mct").then(ClientCommandManager.literal("showTabNames")
                        .then(ClientCommandManager.argument("enabled", bool()).executes(context -> {
                            boolean enabled = BoolArgumentType.getBool(context, "enabled");
                            CustomCreativeTabManager.showNames = enabled;
                            context.getSource().sendFeedback(enabled ? new TextComponent("Showing tab registry names") : new TextComponent("Showing tab names"));
                            return 1;
                        }))).then(ClientCommandManager.literal("reloadTabs").executes(ctx -> {
                    PlatformServices.helper.reloadTabs();
                    ctx.getSource().sendFeedback(new TextComponent("Reloaded Custom Tabs"));
                    return 1;
                }))
        );

        /* Load initial entries and cache old tabs */
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new FabricResourceLoader());
    }

}
