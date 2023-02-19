package me.hypherionmc.morecreativetabs.client.data;

import java.util.ArrayList;

/**
 * @author HypherionSA
 * Gson Helper class for loading Custom Tabs
 */
public class CustomCreativeTab {

    public boolean tab_enabled;
    public String tab_name;
    public TabIcon tab_stack;
    public String tab_background;
    public boolean replace = false;
    public boolean keepExisting = false;
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
