package me.hypherionmc.morecreativetabs.mixin.accessor;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(CreativeModeTabs.class)
public interface ForgeCreativeModeTabsAccessor {

    @Accessor(value = "DEFAULT_TABS", remap = false)
    public static List<CreativeModeTab> getDefaultTabsInternal() {
        throw new AssertionError();
    }

}
