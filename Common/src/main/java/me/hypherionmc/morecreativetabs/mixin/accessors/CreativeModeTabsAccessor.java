package me.hypherionmc.morecreativetabs.mixin.accessors;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author HypherionSA
 * Helper class to access private values from CreativeModeTabs
 */
@Mixin(CreativeModeTabs.class)
public interface CreativeModeTabsAccessor {

    @Accessor("INVENTORY")
    public static ResourceKey<CreativeModeTab> getInventoryTab() {
        throw new AssertionError();
    }

    @Accessor("HOTBAR")
    public static ResourceKey<CreativeModeTab> getHotbarTab() {
        throw new AssertionError();
    }

    @Accessor("SEARCH")
    public static ResourceKey<CreativeModeTab> getSearchTab() {
        throw new AssertionError();
    }

    @Accessor("OP_BLOCKS")
    public static ResourceKey<CreativeModeTab> getOpBlockTab() {
        throw new AssertionError();
    }

    /*@Accessor("TABS")
    public static ResourceKey<CreativeModeTab> getOldTabs() {
        throw new AssertionError();
    }*/

}
