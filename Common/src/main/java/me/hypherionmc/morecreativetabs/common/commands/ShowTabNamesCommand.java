package me.hypherionmc.morecreativetabs.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

// Allows you to see the names of the tabs, to be used for disabling built-in tabs
public class ShowTabNamesCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> showTabs =
                Commands.literal("showTabNames")
                        .requires((command) -> command.hasPermission(2))
                        .then(Commands.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                            boolean enabled = BoolArgumentType.getBool(context, "enabled");
                            CustomCreativeTabManager.showNames = enabled;
                            context.getSource().sendSuccess(enabled ? new TextComponent("Showing tab registry names") : new TextComponent("Showing tab names"), true);
                            return 1;
                        }));
        dispatcher.register(showTabs);
    }
}
