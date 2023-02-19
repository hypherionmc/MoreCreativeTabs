package me.hypherionmc.morecreativetabs.mixin.accessors;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/**
 * @author HypherionSA
 * Helper class to access private values from CreativeModeTabs
 */
@Mixin(CreativeModeTabs.class)
public interface CreativeModeTabsAccessor {

    @Accessor("INVENTORY")
    public static CreativeModeTab getInventoryTab() {
        throw new AssertionError();
    }

    @Accessor("HOTBAR")
    public static CreativeModeTab getHotbarTab() {
        throw new AssertionError();
    }

    @Accessor("SEARCH")
    public static CreativeModeTab getSearchTab() {
        throw new AssertionError();
    }

    @Accessor("OP_BLOCKS")
    public static CreativeModeTab getOpBlockTab() {
        throw new AssertionError();
    }

    @Accessor("TABS")
    public static List<CreativeModeTab> getOldTabs() {
        throw new AssertionError();
    }

}
