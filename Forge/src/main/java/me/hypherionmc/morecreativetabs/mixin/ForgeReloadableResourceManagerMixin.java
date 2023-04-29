package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.MoreCreativeTabs;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(SimpleReloadableResourceManager.class)
public class ForgeReloadableResourceManagerMixin {

    @Inject(method = "createReload", at = @At("RETURN"))
    void injectReload(Executor par1, Executor par2, CompletableFuture<Unit> par3, List<PackResources> par4, CallbackInfoReturnable<ReloadInstance> cir) {
        MoreCreativeTabs.reloadResources();
    }

}