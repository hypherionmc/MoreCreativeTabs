package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import me.hypherionmc.morecreativetabs.mixin.accessors.CreativeModeTabsAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HypherionSA
 */
@Mixin(CreativeModeTabs.class)
public class ForgeCreativeTabsMixin {

    /**
     * Inject our list of creative tabs, instead of using the standard ones
     */
    @Inject(method = "allTabs", at = @At("RETURN"), cancellable = true)
    private static void injectAllTabs(CallbackInfoReturnable<List<CreativeModeTab>> cir) {
        cir.setReturnValue(CustomCreativeTabRegistry.current_tabs);
    }

    /**
     * Inject our list of creative tabs, instead of using the standard ones
     */
    @Inject(method = "tabs", at = @At("RETURN"), cancellable = true)
    private static void injectTabs(CallbackInfoReturnable<List<CreativeModeTab>> cir) {
        cir.setReturnValue(CustomCreativeTabRegistry.current_tabs.stream().filter(CreativeModeTab::shouldDisplay).toList());
    }

    /**
     * Inject our list of creative tabs, instead of using the standard ones
     */
    @Inject(method = "getDefaultTab", at = @At("RETURN"), cancellable = true)
    private static void injectDefaultTab(CallbackInfoReturnable<CreativeModeTab> cir) {
        CreativeModeTab inventory = BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabsAccessor.getInventoryTab());
        cir.setReturnValue(CustomCreativeTabRegistry.current_tabs.stream().filter(CreativeModeTab::shouldDisplay).findFirst().orElse(inventory));
    }

    /**
     * Remove the Hotbar, Search and Inventory tabs from ALWAYS being shown
     */
    /*@Inject(method = "defaultTabs", at = @At("RETURN"), cancellable = true, remap = false)
    private static void injectDefaultTabs(CallbackInfoReturnable<List<CreativeModeTab>> cir) {
        cir.setReturnValue(new ArrayList<>());
    }*/

}
