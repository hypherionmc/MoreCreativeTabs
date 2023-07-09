package me.hypherionmc.morecreativetabs.mixin;

import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents;
import net.fabricmc.fabric.impl.itemgroup.FabricItemGroup;
import net.fabricmc.fabric.mixin.itemgroup.ItemGroupAccessor;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.stream.Stream;

@Mixin(value = CreativeModeTabs.class, priority = 0)
public class FabricCreativeModeTabsMixin {

    @Inject(method = "validate", at = @At("HEAD"), cancellable = true)
    private static void injectCheckTabs(CallbackInfo ci) {
        ci.cancel();
        int TABS_PER_PAGE = 14;
        int count = 0;

        for (CreativeModeTab tab : CustomCreativeTabRegistry.current_tabs) {
            final FabricItemGroup fabricItemGroup = (FabricItemGroup) tab;

            final ItemGroupAccessor itemGroupAccessor = (ItemGroupAccessor) tab;
            fabricItemGroup.setPage((count / TABS_PER_PAGE));
            int pageIndex = count % TABS_PER_PAGE;
            CreativeModeTab.Row row = pageIndex < (TABS_PER_PAGE / 2) ? CreativeModeTab.Row.TOP : CreativeModeTab.Row.BOTTOM;
            itemGroupAccessor.setRow(row);
            itemGroupAccessor.setColumn(row == CreativeModeTab.Row.TOP ? pageIndex % TABS_PER_PAGE : (pageIndex - TABS_PER_PAGE / 2) % (TABS_PER_PAGE));

            count++;
        }

        // Overlapping group detection logic, with support for pages.
        record ItemGroupPosition(CreativeModeTab.Row row, int column, int page) { }
        var map = new HashMap<ItemGroupPosition, String>();

        for (CreativeModeTab tab : CustomCreativeTabRegistry.current_tabs) {
            final FabricItemGroup fabricItemGroup = (FabricItemGroup) tab;
            final String displayName = tab.getDisplayName().getString();
            final var position = new ItemGroupPosition(tab.row(), tab.column(), fabricItemGroup.getPage());
            final String existingName = map.put(position, displayName);

            if (existingName != null) {
                throw new IllegalArgumentException("Duplicate position: (%s) for item groups %s vs %s".formatted(position, displayName, existingName));
            }
        }

        FabricCreativeGuiComponents.COMMON_GROUPS.clear();
    }

    @Inject(method = "streamAllTabs", at = @At("RETURN"), cancellable = true)
    private static void injectTabs(CallbackInfoReturnable<Stream<CreativeModeTab>> cir) {
        cir.setReturnValue(CustomCreativeTabRegistry.current_tabs.stream());
    }
}
