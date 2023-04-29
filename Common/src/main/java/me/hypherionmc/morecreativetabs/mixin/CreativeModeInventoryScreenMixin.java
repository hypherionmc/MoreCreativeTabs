package me.hypherionmc.morecreativetabs.mixin;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Honestly, this class shouldn't be needed.
 * Some tabs like the Saved Hotbars tab, have their `isAlignedRight` value hard-coded to TRUE.
 * This causes rendering issues when re-ordering tabs, when tabs before them are disabled.
 * Overwriting the value from CreativeModeTabMixin does not work, so that's why this is here
 */
@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin {

    @Redirect(method = "renderTabButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;isAlignedRight()Z"))
    private boolean injectRenderTabButton(CreativeModeTab instance) {
        if (instance == CreativeModeTab.TAB_HOTBAR && instance.getId() != 4) {
            return false;
        }
        return instance.isAlignedRight();
    }

    @Redirect(method = "checkTabClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;isAlignedRight()Z"))
    private boolean injectCheckTabClicked(CreativeModeTab instance) {
        if (instance == CreativeModeTab.TAB_HOTBAR && instance.getId() != 4) {
            return false;
        }
        return instance.isAlignedRight();
    }

    @Redirect(method = "checkTabHovering", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;isAlignedRight()Z"))
    private boolean injectCheckTabHovering(CreativeModeTab instance) {
        if (instance == CreativeModeTab.TAB_HOTBAR && instance.getId() != 4) {
            return false;
        }
        return instance.isAlignedRight();
    }
}
