package me.hypherionmc.morecreativetabs.client.data.jsonhelpers;

import java.util.ArrayList;

/**
 * @author HypherionSA
 * Gson Helper class for loading Custom Tabs
 */
public class TabJsonHelper {

    public boolean tab_enabled;
    public String tab_name;
    @Deprecated(forRemoval = true)
    public String tab_icon;
    public TabIcon tab_stack;
    public String tab_background;
    public String replaces;
    public ArrayList<TabItem> tab_items;

    public static class TabItem {
        public String name;
        public boolean hide_old_tab;
        public String nbt;
    }

    public static class TabIcon {
        public String name;
        public String nbt;
    }
}
