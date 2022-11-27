package me.hypherionmc.morecreativetabs.mixin;

import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Helper class to allow changing the ID of a creative tab.
 * This is used to reorder tabs when some are disabled
 */
@Mixin(CreativeModeTab.class)
public interface CreativeModeTabAccessor {

    @Accessor
    public void setId(int id);

}
