package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Set;

@Mixin(CreativeModeTab.class)
public abstract class ForgeCreativeTabMixin {

    @Shadow private Collection<ItemStack> displayItems;

    @Shadow public abstract Collection<ItemStack> getDisplayItems();

    @Shadow private Set<ItemStack> displayItemsSearchTab;

    @Shadow public abstract Collection<ItemStack> getSearchTabDisplayItems();

    @Inject(method = "buildContents", at = @At("HEAD"), cancellable = true)
    private void injectBuildContents(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CallbackInfo ci) {
        final CreativeModeTab self = (CreativeModeTab) (Object) this;

        if (self == CreativeModeTabs.searchTab()) {
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
