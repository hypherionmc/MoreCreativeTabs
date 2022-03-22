package me.hypherionmc.morecreativetabs;

import me.hypherionmc.morecreativetabs.common.commands.ShowTabNamesCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class MoreCreativeTabs implements ModInitializer {

    @Override
    public void onInitialize() {
        Logger.info("Registering Commands");
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            ShowTabNamesCommand.register(dispatcher);
        });
    }
}
