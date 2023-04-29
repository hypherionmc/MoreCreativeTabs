package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabManager;
import me.hypherionmc.morecreativetabs.util.CreativeTabUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * @author HypherionSA
 * Mixin to handle the removal of items from Search, and to show the correct tab name in search
 */
@Mixin(Item.class)
public class ItemMixin {

    @Shadow @Final @Nullable protected CreativeModeTab category;

    /**
     * Map items to their correct custom tab, or, remove them if they are disabled and not
     * added anywhere else
     */
    @Inject(method = "getItemCategory", at = @At("RETURN"), cancellable = true)
    private void injectCategory(CallbackInfoReturnable<CreativeModeTab> cir) {
        Item itm = ((Item) (Object)this);
        CreativeModeTab oldTab = this.category;

        if (CustomCreativeTabManager.remapped_items.containsKey(itm)) {
            String tab = CustomCreativeTabManager.remapped_items.get(itm);
            Arrays.stream(CreativeModeTab.TABS)
                    .filter(t -> t.getRecipeFolderName().equals(tab))
                    .findAny().ifPresent(cir::setReturnValue);
            return;
        }

        if (CustomCreativeTabManager.hidden_stacks.contains(itm)) {
            cir.setReturnValue(null);
            return;
        }

        if (oldTab == null)
            return;

        if (CustomCreativeTabManager.disabled_tabs.contains(oldTab.getRecipeFolderName())) {
            cir.setReturnValue(null);
            return;
        }

        CreativeTabUtils.replacementTab(convertName(oldTab.getRecipeFolderName())).ifPresent(t -> {
            if (t.getKey().keepExisting)
                return;

            if (t.getValue().stream().noneMatch(itemStack -> itemStack.getItem() == itm)) {
                cir.setReturnValue(null);
            }
        });
    }

    /**
     * Just a helper method to convert the tab "registry" names from dotted, to underscore.
     * For example: `itemGroup.building` -> `itemGroup_building`
     * @param tabName - The unmodified tab name
     * @return - The modified tab name
     */
    private String convertName(String tabName) {
        return tabName.replace(".", "_");
    }
}
