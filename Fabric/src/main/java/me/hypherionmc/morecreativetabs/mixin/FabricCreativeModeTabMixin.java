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

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Mixin(CreativeModeTab.class)
public abstract class FabricCreativeModeTabMixin {

    @Shadow private Collection<ItemStack> displayItems;

    @Shadow private Set<ItemStack> displayItemsSearchTab;

    @Shadow public abstract Collection<ItemStack> getSearchTabDisplayItems();

    @Shadow public abstract Collection<ItemStack> getDisplayItems();

    @Shadow @Nullable private Consumer<List<ItemStack>> searchTreeBuilder;

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
