package me.hypherionmc.morecreativetabs.mixin;

import net.fabricmc.fabric.impl.itemgroup.FabricItemGroup;
import net.fabricmc.fabric.mixin.itemgroup.ItemGroupAccessor;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Mixin(value = CreativeModeTabs.class, priority = 0)
public class CreativeModeTabsMixin {

    @Inject(method = "checkTabs", at = @At("HEAD"), cancellable = true)
    private static void injectCheckTabs(CreativeModeTab[] groups, CallbackInfoReturnable<List<CreativeModeTab>> cir) {
        int tabID = 0;

        for (CreativeModeTab itemGroup : groups) {
            int TAB_LIMIT = 14;

            final FabricItemGroup fabricItemGroup = (FabricItemGroup) itemGroup;
            final ItemGroupAccessor itemGroupAccessor = (ItemGroupAccessor) itemGroup;
            fabricItemGroup.setPage((tabID / TAB_LIMIT));
            int pageIndex = tabID % TAB_LIMIT;
            CreativeModeTab.Row row = pageIndex < (TAB_LIMIT / 2) ? CreativeModeTab.Row.TOP : CreativeModeTab.Row.BOTTOM;
            itemGroupAccessor.setRow(row);
            itemGroupAccessor.setColumn(row == CreativeModeTab.Row.TOP ? pageIndex % TAB_LIMIT : (pageIndex - TAB_LIMIT / 2) % (TAB_LIMIT));

            tabID++;
        }

        // Overlapping group detection logic, with support for pages.
        record ItemGroupPosition(CreativeModeTab.Row row, int column, int page) { }
        var map = new HashMap<ItemGroupPosition, String>();

        for (CreativeModeTab itemGroup : groups) {
            final FabricItemGroup fabricItemGroup = (FabricItemGroup) itemGroup;
            final String displayName = itemGroup.getDisplayName().getString();
            final var position = new ItemGroupPosition(itemGroup.row(), itemGroup.column(), fabricItemGroup.getPage());
            final String existingName = map.put(position, displayName);

            if (existingName != null) {
                throw new IllegalArgumentException("Duplicate position: (%s) for item groups %s vs %s".formatted(position, displayName, existingName));
            }
        }

        FabricCreativeGuiComponentsMixin.setCommonGroups(new HashSet<>());

        cir.setReturnValue(List.of(groups));
    }

}
