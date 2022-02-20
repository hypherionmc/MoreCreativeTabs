package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.client.CustomCreativeTabManager;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;fillItemCategory(Lnet/minecraft/world/item/CreativeModeTab;Lnet/minecraft/core/NonNullList;)V"), method = "fillItemList")
    public void fillItemCategory(Item instance, CreativeModeTab tab, NonNullList<ItemStack> stacks) {
       if (!CustomCreativeTabManager.custom_tabs.contains(tab)) {
           if (!CustomCreativeTabManager.hidden_stacks.contains(instance) && !CustomCreativeTabManager.disabled_tabs.contains(tab.getRecipeFolderName())) {
               instance.fillItemCategory(tab, stacks);
           }
       }
   }

}
