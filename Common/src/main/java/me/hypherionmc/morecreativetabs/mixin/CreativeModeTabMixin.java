package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.client.data.jsonhelpers.TabJsonHelper;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager;
import me.hypherionmc.morecreativetabs.util.CreativeTabUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

/**
 * @author HypherionSA
 * Mixin to change the Creative tab icon, remove items from disabled tabs
 */
@Mixin(CreativeModeTab.class)
public abstract class CreativeModeTabMixin {

    @Shadow public abstract String getRecipeFolderName();

    @Inject(at = @At("RETURN"), method = "getDisplayName", cancellable = true)
    public void getDisplayName(CallbackInfoReturnable<Component> cir) {
        if (CustomCreativeTabManager.replaced_tabs.containsKey(this.getRecipeFolderName())) {
            Pair<TabJsonHelper, List<ItemStack>> tabItems = CustomCreativeTabManager.replaced_tabs.get(this.getRecipeFolderName());
            cir.setReturnValue(Component.translatable("itemGroup." + CreativeTabUtils.prefix(tabItems.getLeft().tab_name)));
        }

        if (CustomCreativeTabManager.showNames) {
            cir.setReturnValue(Component.literal(this.getRecipeFolderName()));
        }
    }

    @Inject(at = @At("RETURN"), method = "getIconItem", cancellable = true)
    public void injectIcon(CallbackInfoReturnable<ItemStack> cir) {
        if (CustomCreativeTabManager.replaced_tabs.containsKey(this.getRecipeFolderName())) {
            Pair<TabJsonHelper, List<ItemStack>> tabItems = CustomCreativeTabManager.replaced_tabs.get(this.getRecipeFolderName());

            ItemStack stack = CreativeTabUtils.makeTabIcon(tabItems.getLeft());
            if (!stack.isEmpty()) {
                cir.setReturnValue(stack);
            }
        }
    }

    @Redirect(method = "fillItemList", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;fillItemCategory(Lnet/minecraft/world/item/CreativeModeTab;Lnet/minecraft/core/NonNullList;)V"))
    public void injectItems(Item instance, CreativeModeTab tab, NonNullList<ItemStack> stacks) {
        if (!CustomCreativeTabManager.custom_tabs.contains(tab)) {
            if (CustomCreativeTabManager.replaced_tabs.containsKey(tab.getRecipeFolderName())) {
                stacks.clear();
                stacks.addAll(CustomCreativeTabManager.replaced_tabs.get(tab.getRecipeFolderName()).getRight());
            } else {
                if (CustomCreativeTabManager.disabled_tabs.contains(tab.getRecipeFolderName()) || CustomCreativeTabManager.hidden_stacks.contains(instance)) {
                    stacks.clear();
                }
            }
        }
        instance.fillItemCategory(tab, stacks);
    }

}
