package com.asians.asiansplugin.listener;

import com.asians.asiansplugin.AsiansPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EspecialBow implements Listener {

    AsiansPlugin plugin;
    private final Set<UUID> explosiveArrows = new HashSet<>();

    public EspecialBow(AsiansPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityShootBow(EntityShootBowEvent event) {
        String bowName = event.getBow().getItemMeta().getDisplayName();
        if("X".equals(bowName)) {
            explosiveArrows.add(event.getProjectile().getUniqueId());
            plugin.getLogger().info("Arrow shot with special bow added to explosiveArrows set: " + event.getProjectile().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        plugin.getLogger().info("EntityDamageByEntityEvent triggered by: " + damager.getType());
        if(explosiveArrows.contains(damager.getUniqueId())) {
            plugin.getLogger().info("Damager is in explosiveArrows set: " + damager.getUniqueId());
            if(event.getEntity() instanceof Player) {
                plugin.getLogger().info("Damaged entity is a player: " + event.getEntity().getName());
                Location hitLcation = damager.getLocation();
                World world = hitLcation.getWorld();

                world.createExplosion(hitLcation, 4);
            } else {
                plugin.getLogger().info("Damaged entity is not a player: " + event.getEntity().getType());
            }
            explosiveArrows.remove(damager.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileHit(ProjectileHitEvent event) {
        UUID uniqueId = event.getEntity().getUniqueId();
        Entity hitEntity = event.getHitEntity();
        if(explosiveArrows.contains(uniqueId)) {
            plugin.getLogger().info("ProjectileHitEvent triggered by explosive arrow: " + uniqueId);
            if(hitEntity == null) {
                plugin.getLogger().info("Projectile did not hit an entity: " + event.getHitBlock().getType());
                explosiveArrows.remove(uniqueId);
            }
        }
    }

}
