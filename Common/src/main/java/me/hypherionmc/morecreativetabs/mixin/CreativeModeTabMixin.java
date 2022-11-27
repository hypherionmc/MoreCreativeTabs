package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager;
import me.hypherionmc.morecreativetabs.util.CreativeTabUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author HypherionSA
 * Mixin to handle modifications to existing tabs, such as Vanilla or Modded
 */
@Mixin(CreativeModeTab.class)
public abstract class CreativeModeTabMixin {

    @Shadow public abstract String getRecipeFolderName();

    /**
     * Changes the Title of the Tab. When a Vanilla Tab is replaced, this function is used to update the title
     * When `showTabNames` are enabled, this function is used to show the "registry" name of the tab
     * @param cir - The modified display name if needed
     */
    @Inject(at = @At("RETURN"), method = "getDisplayName", cancellable = true)
    public void getDisplayName(CallbackInfoReturnable<Component> cir) {
        CreativeTabUtils.replacementTab(convertName(getRecipeFolderName())).ifPresent(tabData -> {
            cir.setReturnValue(Component.translatable("itemGroup." + CreativeTabUtils.prefix(tabData.getLeft().tab_name)));
        });

        if (CustomCreativeTabManager.showNames) {
            cir.setReturnValue(Component.literal(this.getRecipeFolderName()));
        }
    }

    /**
     * Change the ICON of an exiting tab, when it's replaced with a custom one
     * @param cir - The modified icon to be shown
     */
    @Inject(at = @At("RETURN"), method = "getIconItem", cancellable = true)
    public void injectIcon(CallbackInfoReturnable<ItemStack> cir) {
        CreativeTabUtils.replacementTab(convertName(getRecipeFolderName())).ifPresent(tabData -> {
            ItemStack stack = CreativeTabUtils.makeTabIcon(tabData.getLeft());
            if (!stack.isEmpty()) {
                cir.setReturnValue(stack);
            }
        });
    }

    /**
     * This filters out Disabled items/tabs, and also replaces the items in Existing Tabs, with that of a custom tab
     * @param stacks - The list of items that will be shown in the tab
     * @param ci - N/A
     */
    @Inject(method = "fillItemList", at = @At("HEAD"), cancellable = true)
    public void injectItems(NonNullList<ItemStack> stacks, CallbackInfo ci) {
        CreativeModeTab tab = (CreativeModeTab) (Object)this;
        if (!CustomCreativeTabManager.custom_tabs.contains(tab) && tab != CreativeModeTab.TAB_SEARCH) {
            ci.cancel();
            CreativeTabUtils.replacementTab(convertName(tab.getRecipeFolderName())).ifPresentOrElse(tabData -> {
                if (tabData.getLeft().keepExisting) {
                    fillTabItems(tab, stacks);
                }

                stacks.addAll(tabData.getRight());
            }, () -> fillTabItems(tab, stacks));
        }
    }

    /**
     * This is a copy of `fillItemList` from CreativeModeTab, modified to filter out disabled items
     * @param tab - The Creative Tab that is being processed
     * @param existing - The items that will be added to the tab, if needed
     */
    private void fillTabItems(CreativeModeTab tab, NonNullList<ItemStack> existing) {
        for (Item $$1 : Registry.ITEM) {
            if (!CustomCreativeTabManager.custom_tabs.contains(tab)) {
                if (!CustomCreativeTabManager.disabled_tabs.contains(tab.getRecipeFolderName()) && !CustomCreativeTabManager.hidden_stacks.contains($$1)) {
                    $$1.fillItemCategory(tab, existing);
                }
            } else {
                $$1.fillItemCategory(tab, existing);
            }
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
