package com.asians.asiansplugin;

import com.asians.asiansplugin.listener.EspecialBow;
import com.asians.asiansplugin.listener.TheFloorIsLava;
import org.bukkit.plugin.java.JavaPlugin;

public final class AsiansPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        new TheFloorIsLava(this);
        new EspecialBow((this));
    }

    @Override
    public void onDisable() {
    }
}
