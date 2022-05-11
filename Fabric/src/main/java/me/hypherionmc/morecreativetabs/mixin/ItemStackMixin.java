package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void getDisplayName(CallbackInfoReturnable<Component> cir) {
        ItemStack stack = ((ItemStack)(Object)this);
        CreativeModeTab tab = stack.getItem().getItemCategory();
        if (CustomCreativeTabManager.customNames.containsKey(stack) && CustomCreativeTabManager.customNames.get(stack).getLeft().equals(tab.getRecipeFolderName())) {
            cir.setReturnValue(new TranslatableComponent(CustomCreativeTabManager.customNames.get(stack).getRight()));
        }
    }

    @Inject(method = "getHoverName", at = @At("RETURN"), cancellable = true)
    public void getHoverName(CallbackInfoReturnable<Component> cir) {
        ItemStack stack = ((ItemStack)(Object)this);
        CreativeModeTab tab = stack.getItem().getItemCategory();
        if (CustomCreativeTabManager.customNames.containsKey(stack) && CustomCreativeTabManager.customNames.get(stack).getLeft().equals(tab.getRecipeFolderName())) {
            cir.setReturnValue(new TranslatableComponent(CustomCreativeTabManager.customNames.get(stack).getRight()));
        }
    }

}
