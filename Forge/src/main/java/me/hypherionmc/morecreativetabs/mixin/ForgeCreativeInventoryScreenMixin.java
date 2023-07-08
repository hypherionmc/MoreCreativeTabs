package me.hypherionmc.morecreativetabs.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author HypherionSA
 * Mixin to help with rendering when tabs have been re-ordered, replaced or removed
 */
@Mixin(CreativeModeInventoryScreen.class)
@SuppressWarnings("unchecked")
public abstract class ForgeCreativeInventoryScreenMixin extends AbstractContainerScreen {

    @Shadow private static int selectedTab;

    @Shadow(remap = false) private static int tabPage;

    public ForgeCreativeInventoryScreenMixin(AbstractContainerMenu p_97741_, Inventory p_97742_, Component p_97743_) {
        super(p_97741_, p_97742_, p_97743_);
    }

    /**
     * Mixin here to return the correct Tab Column, Row and Alignment
     */
    @Inject(method = "renderTabButton", at = @At(value = "HEAD"), cancellable = true)
    private void injectRenderTabButton(PoseStack p_98582_, CreativeModeTab p_98583_, CallbackInfo ci) {
        ci.cancel();

        boolean flag = p_98583_.getId() == selectedTab;
        boolean flag1 = isTopRow(p_98583_);
        int i = getColumn(p_98583_);
        int j = i * 28;
        int k = 0;
        int l = this.leftPos + 28 * i;
        int i1 = this.topPos;
        if (flag) {
            k += 32;
        }

        if (isAlignedRight(p_98583_)) {
            l = this.leftPos + this.imageWidth - 28 * (6 - i);
        } else if (i > 0) {
            l += i;
        }

        if (flag1) {
            i1 -= 28;
        } else {
            k += 64;
            i1 += this.imageHeight - 4;
        }

        RenderSystem.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.
        this.blit(p_98582_, l, i1, j, k, 28, 32);
        this.itemRenderer.blitOffset = 100.0F;
        l += 6;
        i1 += 8 + (flag1 ? 1 : -1);
        ItemStack itemstack = p_98583_.getIconItem();
        this.itemRenderer.renderAndDecorateItem(itemstack, l, i1);
        this.itemRenderer.renderGuiItemDecorations(this.font, itemstack, l, i1);
        this.itemRenderer.blitOffset = 0.0F;
    }

    /**
     * Mixin here to return the correct Tab Column, Row and Alignment
     */
    @Inject(method = "checkTabClicked", at = @At(value = "HEAD"), cancellable = true)
    private void injectCheckTabClicked(CreativeModeTab p_98563_, double p_98564_, double p_98565_, CallbackInfoReturnable<Boolean> cir) {
        if (p_98563_.getTabPage() != tabPage && p_98563_ != CreativeModeTab.TAB_SEARCH && p_98563_ != CreativeModeTab.TAB_INVENTORY) {
            cir.setReturnValue(false);
            return;
        }

        int i = getColumn(p_98563_);
        int j = 28 * i;
        int k = 0;
        if (isAlignedRight(p_98563_)) {
            j = this.imageWidth - 28 * (6 - i) + 2;
        } else if (i > 0) {
            j += i;
        }

        if (isTopRow(p_98563_)) {
            k -= 32;
        } else {
            k += this.imageHeight;
        }

        cir.setReturnValue(p_98564_ >= (double)j && p_98564_ <= (double)(j + 28) && p_98565_ >= (double)k && p_98565_ <= (double)(k + 32));
    }

    /**
     * Mixin here to return the correct Tab Column, Row and Alignment
     */
    @Inject(method = "checkTabHovering", at = @At(value = "HEAD"), cancellable = true)
    private void injectCheckTabHovering(PoseStack p_98585_, CreativeModeTab p_98586_, int p_98587_, int p_98588_, CallbackInfoReturnable<Boolean> cir) {
        int i = getColumn(p_98586_);
        int j = 28 * i;
        int k = 0;
        if (isAlignedRight(p_98586_)) {
            j = this.imageWidth - 28 * (6 - i) + 2;
        } else if (i > 0) {
            j += i;
        }

        if (isTopRow(p_98586_)) {
            k -= 32;
        } else {
            k += this.imageHeight;
        }

        if (this.isHovering(j + 3, k + 3, 23, 27, (double)p_98587_, (double)p_98588_)) {
            this.renderTooltip(p_98585_, p_98586_.getDisplayName(), p_98587_, p_98588_);
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }
    }

    // Extracted from CreativeModeTab
    private boolean isAlignedRight(CreativeModeTab tab) {
        if (tabPage == 0) {
            return tab.getId() == 4 || tab.getId() == 5 || tab.getId() == 11;
        }
        return tab == CreativeModeTab.TAB_SEARCH || tab == CreativeModeTab.TAB_INVENTORY;
    }

    public int getColumn(CreativeModeTab tab) {
        int id = tab.getId();
        if (tabPage != 0) {
            if (tab == CreativeModeTab.TAB_SEARCH)
                id = 5;

            if (tab == CreativeModeTab.TAB_INVENTORY)
                id = 11;
        }

        if (id > 11) return ((id - 12) % 10) % 5;
        return id % 6;
    }

    public boolean isTopRow(CreativeModeTab tab) {
        int id = tab.getId();
        if (tabPage != 0) {
            if (tab == CreativeModeTab.TAB_SEARCH)
                id = 5;

            if (tab == CreativeModeTab.TAB_INVENTORY)
                id = 11;
        }

        if (id > 11) return ((id - 12) % 10) < 5;
        return id < 6;
    }
}
