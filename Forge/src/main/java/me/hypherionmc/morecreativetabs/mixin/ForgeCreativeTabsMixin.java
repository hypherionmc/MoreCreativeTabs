package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(CreativeModeTabs.class)
public class ForgeCreativeTabsMixin {

    @Inject(method = "allTabs", at = @At("RETURN"), cancellable = true)
    private static void injectAllTabs(CallbackInfoReturnable<List<CreativeModeTab>> cir) {
        cir.setReturnValue(CustomCreativeTabRegistry.current_tabs);
    }

    @Inject(method = "tabs", at = @At("RETURN"), cancellable = true)
    private static void injectTabs(CallbackInfoReturnable<List<CreativeModeTab>> cir) {
        cir.setReturnValue(CustomCreativeTabRegistry.current_tabs.stream().filter(CreativeModeTab::shouldDisplay).toList());
    }

    @Inject(method = "getDefaultTab", at = @At("RETURN"), cancellable = true)
    private static void injectDefaultTab(CallbackInfoReturnable<CreativeModeTab> cir) {
        cir.setReturnValue(CustomCreativeTabRegistry.current_tabs.stream().filter(CreativeModeTab::shouldDisplay).findFirst().orElse(CreativeModeTabs.INVENTORY));
    }

    @Inject(method = "defaultTabs", at = @At("RETURN"), cancellable = true, remap = false)
    private static void injectDefaultTabs(CallbackInfoReturnable<List<CreativeModeTab>> cir) {
        cir.setReturnValue(new ArrayList<>());
    }

}
