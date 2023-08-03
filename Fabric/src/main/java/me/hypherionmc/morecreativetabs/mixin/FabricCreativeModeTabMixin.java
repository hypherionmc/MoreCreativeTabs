package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import me.hypherionmc.morecreativetabs.platform.PlatformServices;
import net.fabricmc.fabric.impl.itemgroup.FabricItemGroup;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Set;

@Mixin(CreativeModeTab.class)
public abstract class FabricCreativeModeTabMixin {


    @Shadow private Collection<ItemStack> displayItems;

    @Shadow public abstract Collection<ItemStack> getDisplayItems();

    @Shadow private Set<ItemStack> displayItemsSearchTab;

    @Shadow public abstract Collection<ItemStack> getSearchTabDisplayItems();

    /**
     * Override the isAlignedRight value for Hotbar, Search and Inventory based on if they are reordered
     * or disabled
     */
    @Inject(method = "isAlignedRight", at = @At("RETURN"), cancellable = true)
    private void injectAlignedRight(CallbackInfoReturnable<Boolean> cir) {
        CreativeModeTab tab = ((CreativeModeTab) (Object)this);
        FabricItemGroup group = ((FabricItemGroup) this);

        if (group.getPage() == 0) {
            if (tab.row() == CreativeModeTab.Row.TOP) {
                cir.setReturnValue(tab.column() == 5 || tab.column() == 6);
            }

            if (tab.row() == CreativeModeTab.Row.BOTTOM) {
                cir.setReturnValue(tab.column() == 5 || tab.column() == 6);
            }
            return;
        }
        cir.setReturnValue(false);
    }

    @Inject(method = "buildContents", at = @At("HEAD"), cancellable = true)
    private void injectBuildContents(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CallbackInfo ci) {
        final CreativeModeTab self = (CreativeModeTab) (Object) this;

        if (self == CreativeModeTabs.searchTab()) {
            PlatformServices.TAB_HELPER.updateCreativeTabs(CustomCreativeTabRegistry.current_tabs);
            ci.cancel();
            return;
        }

        if (CustomCreativeTabRegistry.custom_tabs.contains(self)) {
            ci.cancel();
            displayItems.clear();
            displayItems.addAll(this.getDisplayItems());

            displayItemsSearchTab.clear();
            displayItemsSearchTab.addAll(this.getSearchTabDisplayItems());
        }
    }
}
