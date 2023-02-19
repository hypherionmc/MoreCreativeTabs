package me.hypherionmc.morecreativetabs.mixin.accessor;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.CreativeModeTabRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(CreativeModeTabRegistry.class)
public interface ForgeCreativeModeTabRegistryAccessor {

    @Accessor(value = "SORTED_TABS_VIEW", remap = false)
    public static List<CreativeModeTab> getInternalTabs() {
        throw new AssertionError();
    }

}
