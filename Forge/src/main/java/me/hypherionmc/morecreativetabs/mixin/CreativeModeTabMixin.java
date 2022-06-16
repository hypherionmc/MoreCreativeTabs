package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author HypherionSA
 * Mixin to change the Creative tab icon, remove items from disabled tabs
 */
@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {

    @Inject(at = @At("RETURN"), method = "getDisplayName", cancellable = true)
    public void getDisplayName(CallbackInfoReturnable<Component> cir) {
        if (CustomCreativeTabManager.showNames) {
            cir.setReturnValue(new TextComponent(((CreativeModeTab)(Object)this).getRecipeFolderName()));
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;fillItemCategory(Lnet/minecraft/world/item/CreativeModeTab;Lnet/minecraft/core/NonNullList;)V"), method = "fillItemList")
    public void fillItemCategory(Item instance, CreativeModeTab tab, NonNullList<ItemStack> stacks) {
       if (!CustomCreativeTabManager.custom_tabs.contains(tab)) {
           if (!CustomCreativeTabManager.hidden_stacks.contains(instance) && !CustomCreativeTabManager.disabled_tabs.contains(tab.getRecipeFolderName())) {
               instance.fillItemCategory(tab, stacks);
           }
       }
   }

}
