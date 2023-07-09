package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import me.hypherionmc.morecreativetabs.utils.CreativeTabUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static me.hypherionmc.morecreativetabs.utils.CreativeTabUtils.getTabKey;

/**
 * @author HypherionSA
 * Mixin (or hacks if you will) to bend creative tabs to our will
 */
@Mixin(CreativeModeTab.class)
public abstract class CreativeModeTabMixin {

    @Shadow @Final private Component displayName;

    @Shadow private Collection<ItemStack> displayItems;

    /**
     * Modify the items inside a tab.
     * This filters out items removed from old tabs, displays the correct items for Custom Tabs and
     * replaced tabs
     */
    @Inject(method = "getDisplayItems", at = @At("RETURN"), cancellable = true)
    public void injectDisplayItems(CallbackInfoReturnable<Collection<ItemStack>> cir) {
        CreativeModeTab tab = ((CreativeModeTab) (Object)this);

        Collection<ItemStack> oldStacks = this.displayItems;
        Collection<ItemStack> filteredStacks = new ArrayList<>();

        if (oldStacks != null && !oldStacks.isEmpty()) {
            oldStacks.forEach(i -> {
                if (!CustomCreativeTabRegistry.hidden_stacks.contains(i.getItem())) {
                    filteredStacks.add(i);
                }
            });

            if (!filteredStacks.isEmpty()) {
                cir.setReturnValue(filteredStacks);
            }
        }

        if (CustomCreativeTabRegistry.tab_items.containsKey(tab)) {
            cir.setReturnValue(CustomCreativeTabRegistry.tab_items.get(tab));
        }

        CreativeTabUtils.replacementTab(convertName(getTabKey(this.displayName))).ifPresent(tabData -> {
            cir.setReturnValue(tabData.getRight());
        });

    }

    /**
     * If a tab has no items, it's hidden from the inventory. This overrides
     * the behaviour for custom tabs so that they are always shown if they have items
     */
    @Inject(method = "shouldDisplay", at = @At("HEAD"), cancellable = true)
    private void injectShouldDisplay(CallbackInfoReturnable<Boolean> cir) {
        CreativeModeTab tab = ((CreativeModeTab) (Object)this);

        if (CustomCreativeTabRegistry.tab_items.containsKey(tab)) {
            cir.setReturnValue(true);
        }
    }

    /**
     * Modify the Display Name of the tab based on if the tab is a replaced tab, or if showTabNames is enabled
     */
    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void injectDisplayName(CallbackInfoReturnable<Component> cir) {
        Component value = this.displayName;

        CreativeTabUtils.replacementTab(convertName(getTabKey(value))).ifPresent(tabData -> {
            if (!CustomCreativeTabRegistry.showNames) {
                cir.setReturnValue(Component.translatable(CreativeTabUtils.prefix(tabData.getLeft().tab_name)));
            }
        });

        if (!CustomCreativeTabRegistry.showNames)
            return;

        cir.setReturnValue(Component.literal(getTabKey(value)));
    }

    /**
     * Inject the ItemStack to use as an icon for replaced tabs
     */
    @Inject(method = "getIconItem", at = @At("RETURN"), cancellable = true)
    private void injectIcon(CallbackInfoReturnable<ItemStack> cir) {
        CreativeTabUtils.replacementTab(convertName(getTabKey(this.displayName))).ifPresent(tabData -> {
            ItemStack stack = CreativeTabUtils.makeTabIcon(tabData.getLeft());
            if (!stack.isEmpty()) {
                cir.setReturnValue(stack);
            }
        });
    }

    /**
     * Show the correct containing tab name, when in search
     */
    @Inject(method = "contains", at = @At("RETURN"), cancellable = true)
    private void injectContains(ItemStack $$0, CallbackInfoReturnable<Boolean> cir) {
        CreativeModeTab tab = ((CreativeModeTab) (Object) this);

        if (CustomCreativeTabRegistry.tab_items.containsKey(tab)) {
            List<ItemStack> itemStacks = CustomCreativeTabRegistry.tab_items.get(tab);
            cir.setReturnValue(itemStacks.contains($$0));
            return;
        }

        if (CustomCreativeTabRegistry.hidden_stacks.contains($$0.getItem())) {
            cir.setReturnValue(false);
            return;
        }

        Component value = this.displayName;

        CreativeTabUtils.replacementTab(convertName(getTabKey(value))).ifPresent(tabData -> {
            cir.setReturnValue(false);
        });
    }

    /**
     * Filter out items from Replaced Tabs and add correct search content for custom tabs
     */
    @Inject(method = "getSearchTabDisplayItems", at = @At("RETURN"), cancellable = true)
    private void injectSearchTabItems(CallbackInfoReturnable<Collection<ItemStack>> cir) {
        CreativeModeTab tab = ((CreativeModeTab) (Object) this);

        // Tab is a custom tab, so return the items of that tab
        if (CustomCreativeTabRegistry.tab_items.containsKey(tab)) {
            List<ItemStack> itemStacks = CustomCreativeTabRegistry.tab_items.get(tab);
            cir.setReturnValue(itemStacks);
            return;
        }

        Component value = this.displayName;

        // Tab is a replaced tab, so hide the items from it
        CreativeTabUtils.replacementTab(convertName(getTabKey(value))).ifPresent(tabData -> {
            cir.setReturnValue(new ArrayList<>());
        });

        // Filter out hidden items
        Collection<ItemStack> oldStacks = cir.getReturnValue();
        Collection<ItemStack> returnStacks = new ArrayList<>();
        oldStacks.forEach(s -> {
            if (!CustomCreativeTabRegistry.hidden_stacks.contains(s.getItem())) {
                returnStacks.add(s);
            }
        });

        cir.setReturnValue(returnStacks);
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
