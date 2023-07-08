package me.hypherionmc.morecreativetabs.client;

import me.hypherionmc.morecreativetabs.MoreCreativeTabs;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class ForgeResourceReloader implements ResourceManagerReloadListener {

    @Override
    public void onResourceManagerReload(ResourceManager p_10758_) {
        MoreCreativeTabs.reloadResources();
    }
}
