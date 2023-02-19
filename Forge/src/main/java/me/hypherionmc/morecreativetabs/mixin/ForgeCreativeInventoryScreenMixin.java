package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.mixin.accessors.CreativeModeTabsAccessor;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.client.gui.CreativeTabsScreenPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author HypherionSA
 * Modifications made to the Inventory Screen to allow some customizations not normally allowed
 */
@Mixin(CreativeModeInventoryScreen.class)
public class ForgeCreativeInventoryScreenMixin {

    @Shadow(remap = false) private CreativeTabsScreenPage currentPage;

    /**
     * Modify the max number of tabs per "Page" to 14
     */
    @ModifyConstant(method = "init", constant = @Constant(intValue = 10, ordinal = 0))
    private int injectPageSize(int value) {
        return 14;
    }

    /**
     * Change the alignment of Hotbar, Search and Inventory tabs based on their location
     */
    @Redirect(method = "getTabX", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;isAlignedRight()Z"))
    private boolean injectTabX(CreativeModeTab instance) {
        int column = currentPage.getColumn(instance);
        boolean isTop = currentPage.isTop(instance);

        if (instance == CreativeModeTabsAccessor.getHotbarTab() && (column != 5 || !isTop)) {
            return false;
        }

        if (instance == CreativeModeTabsAccessor.getSearchTab() && (column != 6 || !isTop)) {
            return false;
        }

        if (instance == CreativeModeTabsAccessor.getOpBlockTab() && (column != 5 || isTop)) {
            return false;
        }

        if (instance == CreativeModeTabsAccessor.getInventoryTab() && (column != 6 || isTop)) {
            return false;
        }

        return instance.isAlignedRight();
    }

}
