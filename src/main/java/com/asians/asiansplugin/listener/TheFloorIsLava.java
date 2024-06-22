package com.asians.asiansplugin.listener;

import com.asians.asiansplugin.AsiansPlugin;
import com.asians.asiansplugin.runnable.TheFloorIsLavaRunnable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class TheFloorIsLava implements CommandExecutor, TabCompleter {

    private final AsiansPlugin plugin;
    private long interval;
    private final LinkedList<TheFloorIsLavaRunnable> tasksStates;

    public TheFloorIsLava(AsiansPlugin plugin) {
        this.plugin = plugin;
        interval = 10;
        tasksStates = new LinkedList<>();
        this.plugin.getCommand("lava").setExecutor(this);
        this.plugin.getCommand("lava").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 1) {
            if("stop".equals(strings[0])) {
                tasksStates.getLast().cancel();
                tasksStates.removeLast();
                plugin.getServer().broadcastMessage(ChatColor.GRAY + "Lava shut down!");
                return true;
            }
        } else if(strings.length == 2) {
            if("interval".equals(strings[0])) {
                try {
                    interval = Long.parseLong(strings[1]);
                    TheFloorIsLavaRunnable lastRunnable = tasksStates.getLast();
                    int lavaLevel = lastRunnable.getCurrentLavaLevel();
                    int radius = lastRunnable.getRadius();
                    Location location = lastRunnable.getLocation();
                    lastRunnable.cancel();
                    tasksStates.removeLast();
                    TheFloorIsLavaRunnable newTheFloorIsLavaRunnable = new TheFloorIsLavaRunnable(plugin, location, radius, lavaLevel);
                    newTheFloorIsLavaRunnable.runTaskTimer(plugin, 0, 20 * interval);
                    tasksStates.add(newTheFloorIsLavaRunnable);
                    plugin.getServer().broadcastMessage(ChatColor.YELLOW + "Lava interval changed to " + ChatColor.DARK_BLUE + interval + "s");
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            } else if(commandSender instanceof Player) {
                Player player = (Player) commandSender;
                Location location = player.getLocation();
                if("start".equals(strings[0])) {
                    try {
                        int radius = Integer.parseInt(strings[1]);
                        if(radius <= 0) {
                            return false;
                        }
                        WorldBorder worldBorder = location.getWorld().getWorldBorder();
                        Location blockLocation = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
                        worldBorder.setCenter(blockLocation);
                        worldBorder.setSize(radius);
                        TheFloorIsLavaRunnable theFloorIsLavaRunnable = new TheFloorIsLavaRunnable(plugin, location, radius, location.getWorld().getMinHeight());
                        theFloorIsLavaRunnable.runTaskTimer(plugin, 0, 20 * interval);
                        tasksStates.add(theFloorIsLavaRunnable);
                        plugin.getServer().broadcastMessage(ChatColor.GOLD + player.getName() + ": " + ChatColor.DARK_RED + "THE FLOOR IS LAVA!!!");
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> firstArgs = Arrays.asList("interval", "start", "stop");
        List<String> intervalArgs = Arrays.asList(String.valueOf(interval));
        List<String> startArgs = Arrays.asList("20");
        if(strings.length == 1) {
            return getCompletions(firstArgs, strings[0].toLowerCase());
        } else if(strings.length == 2) {
            if(firstArgs.get(0).equals(strings[0])) {
                return getCompletions(intervalArgs, strings[1]);
            } else if(firstArgs.get(1).equals(strings[0])) {
                return getCompletions(startArgs, strings[1]);
            }
        }
        return Collections.emptyList();
    }

    private static List<String> getCompletions(List<String> args, String stringTyped) {
        List<String> completions = null;
        for (String arg : args) {
            if (arg.startsWith(stringTyped)) {
                if (completions == null) {
                    completions = new ArrayList<>();
                }
                completions.add(arg);
            }
        }
        if (completions != null) {
            Collections.sort(completions);
        }
        return completions;
    }

}
