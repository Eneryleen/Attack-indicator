package org.eneryleen.attackIndicator.indicator.legacy;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.eneryleen.attackIndicator.AttackIndicator;
import org.eneryleen.attackIndicator.ConfigManager;
import org.eneryleen.attackIndicator.indicator.IndicatorSpawner;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class LegacyIndicatorManager implements IndicatorSpawner {

    private final AttackIndicator plugin;
    private final DecimalFormat damageFormat;
    private final Random random;
    private final Set<ArmorStand> activeIndicators;

    public LegacyIndicatorManager(AttackIndicator plugin) {
        this.plugin = plugin;
        this.damageFormat = new DecimalFormat("0.#");
        this.random = new Random();
        this.activeIndicators = new HashSet<>();
    }

    @Override
    public void spawnIndicator(LivingEntity entity, double damage) {
        ConfigManager config = plugin.getConfigManager();

        Location location = entity.getLocation().clone();
        double entityHeight = getEntityHeight(entity);
        location.add(0, entityHeight + config.getVerticalOffset(), 0);

        if (config.isRandomOffsetEnabled()) {
            double offsetX = (random.nextDouble() - 0.5) * config.getRandomOffsetX();
            double offsetZ = (random.nextDouble() - 0.5) * config.getRandomOffsetZ();
            location.add(offsetX, 0, offsetZ);
        }

        String damageText = damageFormat.format(damage);
        String formattedText = config.getIndicatorFormat().replace("{damage}", damageText);

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            String displayText = TextFormatter.format(formattedText);

            ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);

            armorStand.setCustomName(displayText);
            armorStand.setCustomNameVisible(true);
            armorStand.setVisible(false);
            armorStand.setGravity(false);

            try {
                armorStand.setMarker(true);
            } catch (NoSuchMethodError ignored) {
            }

            try {
                armorStand.setInvulnerable(true);
            } catch (NoSuchMethodError ignored) {
            }

            activeIndicators.add(armorStand);

            animateIndicator(armorStand, location, config.getUpwardSpeed(), config.getDisplayDuration());
        });
    }

    private void animateIndicator(ArmorStand armorStand, Location startLocation, double speed, int duration) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!armorStand.isValid() || ticks >= duration) {
                    armorStand.remove();
                    activeIndicators.remove(armorStand);
                    cancel();
                    return;
                }

                Location newLocation = armorStand.getLocation().add(0, speed, 0);
                armorStand.teleport(newLocation);

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private double getEntityHeight(LivingEntity entity) {
        try {
            return entity.getHeight();
        } catch (NoSuchMethodError e) {
            return 1.5;
        }
    }

    @Override
    public void cleanup() {
        for (ArmorStand armorStand : new HashSet<>(activeIndicators)) {
            if (armorStand.isValid()) {
                armorStand.remove();
            }
        }
        activeIndicators.clear();
    }
}
