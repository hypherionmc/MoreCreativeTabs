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
 * Mixin to change the Creative tab icon, remove items from disabled tabs
 */
@Mixin(CreativeModeTab.class)
public abstract class CreativeModeTabMixin {

    @Shadow public abstract String getRecipeFolderName();

    @Inject(at = @At("RETURN"), method = "getDisplayName", cancellable = true)
    public void getDisplayName(CallbackInfoReturnable<Component> cir) {
        CreativeTabUtils.replacementTab(convertName(getRecipeFolderName())).ifPresent(tabData -> {
            cir.setReturnValue(Component.translatable("itemGroup." + CreativeTabUtils.prefix(tabData.getLeft().tab_name)));
        });

        if (CustomCreativeTabManager.showNames) {
            cir.setReturnValue(Component.literal(this.getRecipeFolderName()));
        }
    }

    @Inject(at = @At("RETURN"), method = "getIconItem", cancellable = true)
    public void injectIcon(CallbackInfoReturnable<ItemStack> cir) {
        CreativeTabUtils.replacementTab(convertName(getRecipeFolderName())).ifPresent(tabData -> {
            ItemStack stack = CreativeTabUtils.makeTabIcon(tabData.getLeft());
            if (!stack.isEmpty()) {
                cir.setReturnValue(stack);
            }
        });
    }

    @Inject(method = "fillItemList", at = @At("HEAD"), cancellable = true)
    public void injectItems(NonNullList<ItemStack> stacks, CallbackInfo ci) {
        CreativeModeTab tab = (CreativeModeTab) (Object)this;
        if (!CustomCreativeTabManager.custom_tabs.contains(tab)) {
            ci.cancel();
            CreativeTabUtils.replacementTab(convertName(tab.getRecipeFolderName())).ifPresentOrElse(tabData -> {
                if (tabData.getLeft().keepExisting) {
                    fillTabItems(tab, stacks);
                }

                stacks.addAll(tabData.getRight());
            }, () -> fillTabItems(tab, stacks));
        }
    }

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

    private String convertName(String tabName) {
        return tabName.replace(".", "_");
    }
}
