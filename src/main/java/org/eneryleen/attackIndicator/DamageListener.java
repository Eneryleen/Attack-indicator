package org.eneryleen.attackIndicator;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    private final AttackIndicator plugin;

    public DamageListener(AttackIndicator plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof LivingEntity)) {
            return;
        }

        LivingEntity victim = (LivingEntity) entity;
        ConfigManager config = plugin.getConfigManager();

        if (entity instanceof Player && !config.isShowOnPlayers()) {
            return;
        }

        if (config.isWorldDisabled(victim.getWorld().getName())) {
            return;
        }

        if (!config.shouldShowForEntity(victim.getType())) {
            return;
        }

        ConfigManager.DisplayMode mode = config.getDisplayMode();
        Player attacker = null;

        switch (mode) {
            case PLAYER_ONLY:
                if (!(event instanceof EntityDamageByEntityEvent)) {
                    return;
                }
                EntityDamageByEntityEvent playerDamageEvent = (EntityDamageByEntityEvent) event;
                Entity damager = getDamager(playerDamageEvent.getDamager());
                if (!(damager instanceof Player)) {
                    return;
                }
                attacker = (Player) damager;
                break;

            case NO_SELF:
                if (event instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent selfDamageEvent = (EntityDamageByEntityEvent) event;
                    Entity actualDamager = getDamager(selfDamageEvent.getDamager());
                    if (actualDamager != null && actualDamager.equals(victim)) {
                        return;
                    }
                    if (actualDamager instanceof Player) {
                        attacker = (Player) actualDamager;
                    }
                }
                break;

            case ALL:
                if (event instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent allDamageEvent = (EntityDamageByEntityEvent) event;
                    Entity allDamager = getDamager(allDamageEvent.getDamager());
                    if (allDamager instanceof Player) {
                        attacker = (Player) allDamager;
                    }
                }
                break;
        }

        if (attacker != null && !plugin.getToggleManager().isEnabled(attacker.getUniqueId())) {
            return;
        }

        double damage = event.getFinalDamage();
        if (damage <= 0) {
            return;
        }

        plugin.getIndicatorManager().spawnIndicator(victim, damage);
    }

    private Entity getDamager(Entity damager) {
        if (damager instanceof org.bukkit.entity.Projectile) {
            org.bukkit.entity.Projectile projectile = (org.bukkit.entity.Projectile) damager;
            if (projectile.getShooter() instanceof Entity) {
                return (Entity) projectile.getShooter();
            }
            return null;
        }
        return damager;
    }
}
