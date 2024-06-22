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
    private int radius;
    private Location location;
    private World world;
    private int currentLavaLevel;
    private int highestBlockLevel;

    public TheFloorIsLavaRunnable(AsiansPlugin plugin, Location location, int radius, int currentLavaLevel) {
        this.plugin = plugin;
        this.radius = radius;
        this.location = location;
        world = location.getWorld();
        this.currentLavaLevel = currentLavaLevel;
        highestBlockLevel = location.getWorld().getMinHeight();
    }

    @Override
    public void run() {
        plugin.getServer().broadcastMessage(ChatColor.YELLOW + "Lava is going up from Y: " + ChatColor.BLUE + currentLavaLevel + ChatColor.YELLOW + "\t X: " + ChatColor.AQUA + (int) location.getX() + ChatColor.YELLOW + " Z: " + ChatColor.AQUA + (int) location.getZ());
        setRegionMaterial(Material.LAVA);
        if(currentLavaLevel > highestBlockLevel + 1) {
            plugin.getServer().broadcastMessage(ChatColor.BLUE + "Lava shut down!");
            this.cancel();
        }
        currentLavaLevel++;
    }

    private void setRegionMaterial(Material material) {
        int minX = (int) Math.floor(location.getBlockX() - radius / 2);
        int minZ = (int) Math.floor(location.getBlockZ() - radius / 2);

        int maxX = (int) Math.floor(location.getBlockX() + radius / 2);
        int maxZ = (int) Math.floor(location.getBlockZ() + radius / 2);

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Block block = world.getBlockAt(x, currentLavaLevel, z);
                int surfaceBlockLevel = getSurfaceBlockLevel(world, x, z);
                if(currentLavaLevel <= surfaceBlockLevel + 1 && block.getType() == Material.AIR) {
                    block.setType(material);
                }
            }
        }
    }

    private int getSurfaceBlockLevel(World world, int x, int z) {
        int maxY = world.getMaxHeight();

        for (int y = maxY - 1; y >= world.getMinHeight(); y--) {
            Block block = world.getBlockAt(x, y, z);
            if (block.getType() != Material.AIR && block.getType() != Material.LAVA) {
                if(y > highestBlockLevel) {
                    highestBlockLevel = y;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
