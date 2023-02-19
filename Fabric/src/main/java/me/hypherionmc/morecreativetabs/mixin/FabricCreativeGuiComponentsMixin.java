package me.hypherionmc.morecreativetabs.mixin;

import net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(FabricCreativeGuiComponents.class)
public interface FabricCreativeGuiComponentsMixin {

    @Accessor(value = "COMMON_GROUPS", remap = false)
    @Final
    @Mutable
    static void setCommonGroups(Set<CreativeModeTab> tabs) {
        throw new AssertionError();
    }

}
