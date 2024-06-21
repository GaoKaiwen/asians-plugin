package com.asians.asiansplugin;

import com.asians.asiansplugin.listener.TheFloorIsLava;
import org.bukkit.plugin.java.JavaPlugin;

public final class AsiansPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        new TheFloorIsLava(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
