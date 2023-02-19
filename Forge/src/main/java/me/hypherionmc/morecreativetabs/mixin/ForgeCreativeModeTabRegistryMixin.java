package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.CreativeModeTabRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CreativeModeTabRegistry.class)
public class ForgeCreativeModeTabRegistryMixin {

    @Inject(method = "getSortedCreativeModeTabs", at = @At("RETURN"), cancellable = true, remap = false)
    private static void injectTabs(CallbackInfoReturnable<List<CreativeModeTab>> cir) {
        cir.setReturnValue(CustomCreativeTabRegistry.current_tabs);
    }

}
