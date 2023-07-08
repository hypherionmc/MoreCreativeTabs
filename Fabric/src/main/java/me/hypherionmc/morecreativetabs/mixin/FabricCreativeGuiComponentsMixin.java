package me.hypherionmc.morecreativetabs.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.impl.item.group.CreativeGuiExtensions;
import net.fabricmc.fabric.impl.item.group.FabricCreativeGuiComponents;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author HypherionSA
 * Mixin to move the fabric Next/Prev Buttons to the 1.19.3 position, to prevent
 * interference with custom/reordered tabs
 */
@Mixin(FabricCreativeGuiComponents.ItemGroupButtonWidget.class)
public class FabricCreativeGuiComponentsMixin {

    private int modifiedX, modifiedY;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectConstructor(int x, int y, FabricCreativeGuiComponents.Type type, CreativeGuiExtensions extensions, CallbackInfo ci) {
        this.modifiedX = x + 54;
        this.modifiedY = y + 15;
        FabricCreativeGuiComponents.COMMON_GROUPS.clear();
        FabricCreativeGuiComponents.COMMON_GROUPS.add(CreativeModeTab.TAB_SEARCH);
        FabricCreativeGuiComponents.COMMON_GROUPS.add(CreativeModeTab.TAB_INVENTORY);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void injectRender(PoseStack matrixStack, int mouseX, int mouseY, float float_1, CallbackInfo ci) {
        FabricCreativeGuiComponents.ItemGroupButtonWidget buttonWidget = ((FabricCreativeGuiComponents.ItemGroupButtonWidget) (Object) this);
        buttonWidget.x = this.modifiedX;
        buttonWidget.y = this.modifiedY;
    }

}
