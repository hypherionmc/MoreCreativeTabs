package me.hypherionmc.morecreativetabs.mixin.accessors;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeModeTab.class)
public interface CreativeModeTabAccessor {

    @Accessor("column")
    public void setColumn(int id);

    @Accessor("displayName")
    public Component getInternalDisplayName();

}
