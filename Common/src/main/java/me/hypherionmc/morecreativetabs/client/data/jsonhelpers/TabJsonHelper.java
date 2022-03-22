package me.hypherionmc.morecreativetabs.client.data.jsonhelpers;

import java.util.ArrayList;

public class TabJsonHelper {

    public boolean tab_enabled;
    public String tab_name;
    public String tab_icon;
    public String tab_background;
    public ArrayList<TabItem> tab_items;

    public static class TabItem {
        public String name;
        public boolean hide_old_tab;
        public String nbt;
    }
}
