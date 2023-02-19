package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.MoreCreativeTabs;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableResourceManager.class)
public class ForgeReloadableResourceManagerMixin {

    @Inject(method = "createReload", at = @At("RETURN"))
    private void injectReload(Executor p_143930_, Executor p_143931_, CompletableFuture<Unit> p_143932_, List<PackResources> p_143933_, CallbackInfoReturnable<ReloadInstance> cir) {
        MoreCreativeTabs.reloadResources();
    }

}
