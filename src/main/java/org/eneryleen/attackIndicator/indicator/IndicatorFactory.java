package org.eneryleen.attackIndicator.indicator;

import org.bukkit.Bukkit;
import org.eneryleen.attackIndicator.AttackIndicator;
import org.eneryleen.attackIndicator.indicator.legacy.LegacyIndicatorManager;

public class IndicatorFactory {

    public static IndicatorSpawner createIndicatorManager(AttackIndicator plugin) {
        if (supportsDisplayEntities()) {
            plugin.getLogger().info("Using Display Entities (1.19.4+) with Adventure API");
            return createModernIndicatorManager(plugin);
        } else {
            String textSystem = supportsAdventure() ? "Adventure API (MiniMessage)" : "Legacy ChatColor";
            plugin.getLogger().info("Using Armor Stands with " + textSystem);
            return new LegacyIndicatorManager(plugin);
        }
    }

    private static IndicatorSpawner createModernIndicatorManager(AttackIndicator plugin) {
        try {
            Class<?> modernClass = Class.forName("org.eneryleen.attackIndicator.indicator.modern.ModernIndicatorManager");
            return (IndicatorSpawner) modernClass.getConstructor(AttackIndicator.class).newInstance(plugin);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load ModernIndicatorManager, falling back to legacy mode");
            return new LegacyIndicatorManager(plugin);
        }
    }

    private static boolean supportsDisplayEntities() {
        try {
            Class.forName("org.bukkit.entity.Display");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean supportsAdventure() {
        try {
            Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
