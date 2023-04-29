package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Class to add "Registry" names to tooltips to make it easier to find them
 */
@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getTooltipLines", at = @At("RETURN"), cancellable = true)
    private void injectToolTip(Player $$0, TooltipFlag $$1, CallbackInfoReturnable<List<Component>> cir) {
        if (CustomCreativeTabManager.showNames) {
            ItemStack stack = (ItemStack) (Object)this;
            List<Component> list = cir.getReturnValue();
            list.add(Component.literal("MCT Name: " + Registry.ITEM.getKey(stack.getItem())).withStyle(ChatFormatting.YELLOW));
            cir.setReturnValue(list);
        }
    }
}
