package me.hypherionmc.morecreativetabs.mixin;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.client.gui.CreativeTabsScreenPage;
import net.minecraftforge.common.util.ConcatenatedListView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(CreativeTabsScreenPage.class)
public class ForgeCreativeTabsScreenPageMixin {

    @Shadow(remap = false) @Final @Mutable private ConcatenatedListView<CreativeModeTab> visibleTabs;

    @Mutable
    @Shadow(remap = false) @Final private List<CreativeModeTab> topTabs;

    @Mutable
    @Shadow(remap = false) @Final private List<CreativeModeTab> bottomTabs;

    @Mutable
    @Shadow(remap = false) @Final private List<CreativeModeTab> tabs;

    @Inject(method = "<init>", at = @At(value = "RETURN"), remap = false)
    private void injectVisibleTabs(List<CreativeModeTab> tabs, CallbackInfo ci) {
        this.tabs = tabs;
        this.topTabs = new ArrayList<>();
        this.bottomTabs = new ArrayList<>();
        this.visibleTabs = ConcatenatedListView.of(tabs);

        int maxLength = 14;
        int topLength = maxLength / 2;
        int length = Math.min(14, tabs.size());

        for (int i = 0; i < length; i++)
        {
            CreativeModeTab tab = tabs.get(i);
            (i < topLength ? this.topTabs : this.bottomTabs).add(tab);
        }
    }

    @Inject(method = "isTop", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectIsTop(CreativeModeTab tab, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.topTabs.contains(tab));
    }

    @Inject(method = "getColumn", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectGetColumn(CreativeModeTab tab, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(this.topTabs.contains(tab) ? this.topTabs.indexOf(tab) : this.bottomTabs.indexOf(tab));
    }

}
