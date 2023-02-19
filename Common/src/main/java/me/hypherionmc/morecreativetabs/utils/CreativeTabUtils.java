package me.hypherionmc.morecreativetabs.utils;

import me.hypherionmc.morecreativetabs.client.data.CustomCreativeTab;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class CreativeTabUtils {

    /**
     * Helper Method to create a Tab Icon from the JSON file. Used to remove duplicated code between Forge/Fabric
     * @param json - The JSON class of the loaded Tab JSON
     * @return - Returns an ItemStack or Empty ItemStack
     */
    public static ItemStack makeTabIcon(CustomCreativeTab json) {
        AtomicReference<ItemStack> icon = new AtomicReference<>(ItemStack.EMPTY);
        CustomCreativeTab.TabIcon tabIcon = new CustomCreativeTab.TabIcon();

        if (json.tab_stack != null) {
            tabIcon = json.tab_stack;
        }

        /* Resolve the Icon from the Item Registry */
        CustomCreativeTab.TabIcon finalTabIcon = tabIcon;
        ItemStack stack = getItemStack(tabIcon.name);

        if (!stack.isEmpty()) {
            if (finalTabIcon.nbt != null) {
                CompoundTag tag = new CompoundTag();
                try {
                    tag = TagParser.parseTag(finalTabIcon.nbt);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /* Apply the Stack NBT if needed and apply the icon */
                stack.setTag(tag);
                icon.set(stack);
            } else {
                icon.set(stack);
            }
        }
        return icon.get();
    }

    /**
     * Get the item stack from the Minecraft Registry
     * @param jsonItem - The Item JSON object
     * @return - The item if found, or return an empty item
     */
    public static ItemStack getItemStack(String jsonItem) {
        Optional<Item> itemOptional = BuiltInRegistries.ITEM.getOptional(new ResourceLocation(jsonItem));
        return itemOptional.map(ItemStack::new).orElse(ItemStack.EMPTY);
    }

    /**
     * Prefix Custom Tab names with morecreativetabs to avoid Lang Key conflicts
     * @param tabName - The name of the tab from the JSON
     * @return - Returns `morecreativetabs.tabname`
     */
    public static String prefix(String tabName) {
        return String.format("%s.%s", "morecreativetabs", tabName);
    }

    public static String getTabKey(Component component) {
        if (component.getContents() instanceof TranslatableContents contents) {
            return contents.getKey();
        }
        return component.getString();
    }

    /**
     * Convert Resource Location into String for processing replacement tabs
     * @param input - The PATH section of a ResourceLocation
     * @return - The tab name in format `itemGroup.name` or `name`
     */
    public static String fileToTab(String input) {
        input = input.replace("morecreativetabs/", "");
        input = input.replace("morecreativetabs", "");
        input = input.replace(".json", "");

        return input;
    }

    /**
     * Helper method to detect if a Custom Tab is a replacement of an existing tab
     * @param tabName - The "recipeFolderName" of the tab to find
     * @return - An optional containing the tab data
     */
    public static Optional<Pair<CustomCreativeTab, List<ItemStack>>> replacementTab(String tabName) {
        if (CustomCreativeTabRegistry.replaced_tabs.containsKey(tabName)) {
            return Optional.of(CustomCreativeTabRegistry.replaced_tabs.get(tabName));
        }
        if (CustomCreativeTabRegistry.replaced_tabs.containsKey(tabName.toLowerCase())) {
            return Optional.of(CustomCreativeTabRegistry.replaced_tabs.get(tabName.toLowerCase()));
        }
        return Optional.empty();
    }

}
