package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager;
import me.hypherionmc.morecreativetabs.util.CreativeTabUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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
        CreativeTabUtils.replacementTab(this.getRecipeFolderName().replace(".", "_")).ifPresent(tabData -> {
            cir.setReturnValue(Component.translatable("itemGroup." + CreativeTabUtils.prefix(tabData.getLeft().tab_name)));
        });

        if (CustomCreativeTabManager.showNames) {
            cir.setReturnValue(Component.literal(this.getRecipeFolderName()));
        }
    }

    @Inject(at = @At("RETURN"), method = "getIconItem", cancellable = true)
    public void injectIcon(CallbackInfoReturnable<ItemStack> cir) {
        CreativeTabUtils.replacementTab(this.getRecipeFolderName().replace(".", "_")).ifPresent(tabData -> {
            ItemStack stack = CreativeTabUtils.makeTabIcon(tabData.getLeft());
            if (!stack.isEmpty()) {
                cir.setReturnValue(stack);
            }
        });
    }

    @Redirect(method = "fillItemList", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;fillItemCategory(Lnet/minecraft/world/item/CreativeModeTab;Lnet/minecraft/core/NonNullList;)V"))
    public void injectItems(Item instance, CreativeModeTab tab, NonNullList<ItemStack> stacks) {
        if (!CustomCreativeTabManager.custom_tabs.contains(tab)) {
            CreativeTabUtils.replacementTab(tab.getRecipeFolderName().replace(".", "_")).ifPresentOrElse(tabData -> {
                if (tabData.getLeft().keepExisting) {
                    instance.fillItemCategory(tab, stacks);
                }

                CreativeTabUtils.getReplacementItem(tab.getRecipeFolderName().replace(".", "_"), instance).ifPresent(item -> {
                    stacks.add(new ItemStack(item));
                    instance.fillItemCategory(tab, stacks);
                });
            }, () -> {
                if (!CustomCreativeTabManager.disabled_tabs.contains(tab.getRecipeFolderName()) && !CustomCreativeTabManager.hidden_stacks.contains(instance)) {
                    instance.fillItemCategory(tab, stacks);
                }
            });
        }
    }
}
