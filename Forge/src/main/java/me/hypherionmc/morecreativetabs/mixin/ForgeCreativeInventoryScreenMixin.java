package me.hypherionmc.morecreativetabs.mixin;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.client.gui.CreativeTabsScreenPage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

/**
 * @author HypherionSA
 * Modifications made to the Inventory Screen to allow some customizations not normally allowed
 */
@Mixin(CreativeModeInventoryScreen.class)
public class ForgeCreativeInventoryScreenMixin {

    @Shadow(remap = false) private CreativeTabsScreenPage currentPage;

    @Shadow @Final private List<CreativeTabsScreenPage> pages;

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

        if (pages.indexOf(currentPage) == 0) {
            if (isTop) {
                return column == 5 || column == 6;
            }

            return column == 5 || column == 6;
        }

        return false;
    }

}
