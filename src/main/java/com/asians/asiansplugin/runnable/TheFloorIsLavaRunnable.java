package com.asians.asiansplugin.runnable;

import com.asians.asiansplugin.AsiansPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public class TheFloorIsLavaRunnable extends BukkitRunnable {

    private final AsiansPlugin plugin;
    private int halfOfRegionSide = 20;
    private Location location;
    private World world;
    private int currentLavaLevel;
    private int highestLevel;

    public TheFloorIsLavaRunnable(AsiansPlugin plugin, Location location, int currentLavaLevel) {
        this.plugin = plugin;
        this.location = location;
        world = location.getWorld();
        this.currentLavaLevel = currentLavaLevel;
        highestLevel = currentLavaLevel + 2;
    }

    @Override
    public void run() {
        plugin.getServer().broadcastMessage(ChatColor.YELLOW + "Lava is going up from Y: " + ChatColor.BLUE + currentLavaLevel + ChatColor.YELLOW + "\t X: " + ChatColor.AQUA + location.getX() + ChatColor.YELLOW + " Z: " + ChatColor.AQUA + location.getZ());
        setRegionMaterial(Material.LAVA);
        if(currentLavaLevel++ >= highestLevel) {
            this.cancel();
        }
    }

    private void setRegionMaterial(Material material) {
        int minX = location.getBlockX() - halfOfRegionSide;
        int minZ = location.getBlockZ() - halfOfRegionSide;

        int maxX = location.getBlockX() + halfOfRegionSide;
        int maxZ = location.getBlockZ() + halfOfRegionSide;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Block block = world.getBlockAt(x, currentLavaLevel, z);
                if(!block.getType().isSolid() && currentLavaLevel <= getSurfaceBlockLevel(world, x, z)) {
                    block.setType(material);
                }
            }
        }
    }

    private int getSurfaceBlockLevel(World world, int x, int z) {
        int maxY = world.getMaxHeight();

        for (int y = maxY - 1; y >= world.getMinHeight(); y--) {
            Block block = world.getBlockAt(x, y, z);
            if (block.getType() != Material.AIR) {
                if(y > highestLevel) {
                    highestLevel = y;
                }
                return y;
            }
        }
        return world.getMinHeight();
    }

    public int getCurrentLavaLevel() {
        return currentLavaLevel;
    }

    public void setCurrentLavaLevel(int currentLavaLevel) {
        this.currentLavaLevel = currentLavaLevel;
    }
}
