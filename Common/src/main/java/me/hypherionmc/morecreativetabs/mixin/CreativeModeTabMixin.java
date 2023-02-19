package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import me.hypherionmc.morecreativetabs.mixin.accessors.CreativeModeTabsAccessor;
import me.hypherionmc.morecreativetabs.utils.CreativeTabUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.hypherionmc.morecreativetabs.utils.CreativeTabUtils.getTabKey;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {

    @Shadow @Final private Component displayName;

    @Shadow private Collection<ItemStack> displayItems;

    @Inject(method = "getDisplayItems", at = @At("RETURN"), cancellable = true)
    public void injectDisplayItems(CallbackInfoReturnable<Collection<ItemStack>> cir) {
        CreativeModeTab tab = ((CreativeModeTab) (Object)this);

        Collection<ItemStack> oldStacks = this.displayItems;
        Collection<ItemStack> filteredStacks = new ArrayList<>();

        if (oldStacks != null && !oldStacks.isEmpty()) {
            oldStacks.forEach(i -> {
                if (!CustomCreativeTabRegistry.hidden_stacks.contains(i.getItem())) {
                    filteredStacks.add(i);
                }
            });

            if (!filteredStacks.isEmpty()) {
                cir.setReturnValue(filteredStacks);
            }
        }

        if (CustomCreativeTabRegistry.tab_items.containsKey(tab)) {
            cir.setReturnValue(CustomCreativeTabRegistry.tab_items.get(tab));
        }

        CreativeTabUtils.replacementTab(convertName(getTabKey(this.displayName))).ifPresent(tabData -> {
            cir.setReturnValue(tabData.getRight());
        });

    }

    @Inject(method = "shouldDisplay", at = @At("HEAD"), cancellable = true)
    private void injectShouldDisplay(CallbackInfoReturnable<Boolean> cir) {
        CreativeModeTab tab = ((CreativeModeTab) (Object)this);

        if (CustomCreativeTabRegistry.tab_items.containsKey(tab)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void injectDisplayName(CallbackInfoReturnable<Component> cir) {
        Component value = this.displayName;

        CreativeTabUtils.replacementTab(convertName(getTabKey(value))).ifPresent(tabData -> {
            if (!CustomCreativeTabRegistry.showNames) {
                cir.setReturnValue(Component.translatable(CreativeTabUtils.prefix(tabData.getLeft().tab_name)));
            }
        });

        if (!CustomCreativeTabRegistry.showNames)
            return;

        cir.setReturnValue(Component.literal(getTabKey(value)));
    }

    @Inject(method = "getIconItem", at = @At("RETURN"), cancellable = true)
    private void injectIcon(CallbackInfoReturnable<ItemStack> cir) {
        CreativeTabUtils.replacementTab(convertName(getTabKey(this.displayName))).ifPresent(tabData -> {
            ItemStack stack = CreativeTabUtils.makeTabIcon(tabData.getLeft());
            if (!stack.isEmpty()) {
                cir.setReturnValue(stack);
            }
        });
    }

    @Inject(method = "isAlignedRight", at = @At("RETURN"), cancellable = true)
    private void injectAlignedRight(CallbackInfoReturnable<Boolean> cir) {
        CreativeModeTab tab = ((CreativeModeTab) (Object)this);

        if (tab == CreativeModeTabsAccessor.getHotbarTab() && (tab.column() != 5 || tab.row() != CreativeModeTab.Row.TOP)) {
            cir.setReturnValue(false);
        }

        if (tab == CreativeModeTabsAccessor.getSearchTab() && (tab.column() != 6 || tab.row() != CreativeModeTab.Row.TOP)) {
            cir.setReturnValue(false);
        }

        if (tab == CreativeModeTabsAccessor.getOpBlockTab() && (tab.column() != 5 || tab.row() != CreativeModeTab.Row.BOTTOM)) {
            cir.setReturnValue(false);
        }

        if (tab == CreativeModeTabsAccessor.getInventoryTab() && (tab.column() != 6 || tab.row() != CreativeModeTab.Row.BOTTOM)) {
            cir.setReturnValue(false);
        }
    }

    /**
     * Just a helper method to convert the tab "registry" names from dotted, to underscore.
     * For example: `itemGroup.building` -> `itemGroup_building`
     * @param tabName - The unmodified tab name
     * @return - The modified tab name
     */
    private String convertName(String tabName) {
        return tabName.replace(".", "_");
    }

}
