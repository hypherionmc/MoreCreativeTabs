package me.hypherionmc.morecreativetabs.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class ReloadTabsCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("reloadTabs").executes(context -> {
            if (CustomCreativeTabManager.tabEvents != null) {
                CustomCreativeTabManager.tabEvents.reloadTabs();
            }
            return 1;
        }));
    }

}
