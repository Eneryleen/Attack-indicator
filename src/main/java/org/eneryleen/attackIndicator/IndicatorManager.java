package org.eneryleen.attackIndicator;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class IndicatorManager {

    private final AttackIndicator plugin;
    private final MiniMessage miniMessage;
    private final DecimalFormat damageFormat;
    private final Random random;
    private final Set<TextDisplay> activeIndicators;

    public IndicatorManager(AttackIndicator plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
        this.damageFormat = new DecimalFormat("0.#");
        this.random = new Random();
        this.activeIndicators = new HashSet<>();
    }

    public void spawnIndicator(LivingEntity entity, double damage) {
        ConfigManager config = plugin.getConfigManager();

        Location location = entity.getLocation().clone();
        location.add(0, entity.getHeight() + config.getVerticalOffset(), 0);

        if (config.isRandomOffsetEnabled()) {
            double offsetX = (random.nextDouble() - 0.5) * config.getRandomOffsetX();
            double offsetZ = (random.nextDouble() - 0.5) * config.getRandomOffsetZ();
            location.add(offsetX, 0, offsetZ);
        }

        String damageText = damageFormat.format(damage);
        String formattedText = config.getIndicatorFormat().replace("{damage}", damageText);
        Component textComponent = miniMessage.deserialize(formattedText);

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            TextDisplay display = location.getWorld().spawn(location, TextDisplay.class, textDisplay -> {
                textDisplay.text(textComponent);
                textDisplay.setBillboard(Display.Billboard.CENTER);
                textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
                textDisplay.setSeeThrough(true);

                float scale = config.getIndicatorScale();
                Transformation transformation = textDisplay.getTransformation();
                transformation.getScale().set(new Vector3f(scale, scale, scale));
                textDisplay.setTransformation(transformation);
            });

            activeIndicators.add(display);

            animateIndicator(display, location, config.getUpwardSpeed(), config.getDisplayDuration());
        });
    }

    private void animateIndicator(TextDisplay display, Location startLocation, double speed, int duration) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!display.isValid() || ticks >= duration) {
                    display.remove();
                    activeIndicators.remove(display);
                    cancel();
                    return;
                }

                Location newLocation = display.getLocation().add(0, speed, 0);
                display.teleport(newLocation);

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void cleanup() {
        for (TextDisplay display : new HashSet<>(activeIndicators)) {
            if (display.isValid()) {
                display.remove();
            }
        }
        activeIndicators.clear();
    }
}
