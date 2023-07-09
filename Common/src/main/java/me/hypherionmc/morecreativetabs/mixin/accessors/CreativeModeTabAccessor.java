package me.hypherionmc.morecreativetabs.mixin.accessors;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Collection;
import java.util.Set;

/**
 * @author HypherionSA
 * Helper Class to access some internal values and modify them as needed without
 * access wideners
 */
@Mixin(CreativeModeTab.class)
public interface CreativeModeTabAccessor {

    @Accessor("column")
    @Mutable
    public void setColumn(int id);

    @Accessor("row")
    @Mutable
    public void setRow(CreativeModeTab.Row row);

    @Accessor("displayName")
    public Component getInternalDisplayName();

    @Accessor("displayItems")
    public Collection<ItemStack> getDisplayItems();

    @Accessor("displayItemsSearchTab")
    public Set<ItemStack> getDisplayItemSearchTab();

}
