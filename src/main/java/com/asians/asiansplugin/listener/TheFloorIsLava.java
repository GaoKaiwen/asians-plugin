package com.asians.asiansplugin.listener;

import com.asians.asiansplugin.AsiansPlugin;
import com.asians.asiansplugin.runnable.TheFloorIsLavaRunnable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

public class TheFloorIsLava implements Listener, CommandExecutor, TabCompleter {

    private final AsiansPlugin plugin;
    private long interval;
    private Location lavaLocation;
    private final LinkedList<TheFloorIsLavaRunnable> tasksStates;

    public TheFloorIsLava(AsiansPlugin plugin) {
        this.plugin = plugin;
        interval = 10;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin.getCommand("lava").setExecutor(this);
        this.plugin.getCommand("lava").setTabCompleter(this);
        tasksStates = new LinkedList<>();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        lavaLocation = player.getLocation();
        TheFloorIsLavaRunnable theFloorIsLavaRunnable = new TheFloorIsLavaRunnable(plugin, lavaLocation, lavaLocation.getWorld().getMinHeight());
        theFloorIsLavaRunnable.runTaskTimer(plugin, 0, 20 * interval);
        tasksStates.add(theFloorIsLavaRunnable);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 2) {
            if("interval".equals(strings[0])) {
                try {
                    interval = Integer.parseInt(strings[1]);
                    TheFloorIsLavaRunnable lastRunnable = tasksStates.getLast();
                    int lavaLevel = lastRunnable.getCurrentLavaLevel();
                    lastRunnable.cancel();
                    tasksStates.removeLast();
                    TheFloorIsLavaRunnable newTheFloorIsLavaRunnable = new TheFloorIsLavaRunnable(plugin, lavaLocation, lavaLevel);
                    newTheFloorIsLavaRunnable.runTaskTimer(plugin, 0, 20 * interval);
                    tasksStates.add(newTheFloorIsLavaRunnable);
                    plugin.getServer().broadcastMessage(ChatColor.YELLOW + "Lava interval changed to " + ChatColor.DARK_BLUE + interval + "s");
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> firstArgs = Arrays.asList("interval");
        List<String> secondArgs = Arrays.asList(String.valueOf(interval));
        if(strings.length == 1) {
            return getCompletions(firstArgs, strings[0].toLowerCase());

        } else if(strings.length == 2 && firstArgs.get(0).equals(strings[0])) {
            return getCompletions(secondArgs, strings[1]);
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
