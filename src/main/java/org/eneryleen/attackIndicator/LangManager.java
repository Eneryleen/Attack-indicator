package org.eneryleen.attackIndicator;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LangManager {

    private final AttackIndicator plugin;
    private final MiniMessage miniMessage;
    private final Map<String, String> messages;
    private String language;

    public LangManager(AttackIndicator plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
        this.messages = new HashMap<>();
    }

    public void loadLanguage(String lang) {
        this.language = lang;
        messages.clear();

        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        File langFile = new File(langFolder, lang + ".yml");

        if (!langFile.exists()) {
            plugin.saveResource("lang/" + lang + ".yml", false);
        }

        FileConfiguration config;
        try {
            config = YamlConfiguration.loadConfiguration(langFile);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load language file: " + lang + ".yml, using English");
            InputStream defaultStream = plugin.getResource("lang/en.yml");
            if (defaultStream != null) {
                config = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            } else {
                return;
            }
        }

        loadMessages(config, "");
    }

    private void loadMessages(ConfigurationSection config, String path) {
        for (String key : config.getKeys(false)) {
            String fullPath = path.isEmpty() ? key : path + "." + key;
            if (config.isConfigurationSection(key)) {
                loadMessages(config.getConfigurationSection(key), fullPath);
            } else {
                messages.put(fullPath, config.getString(key, ""));
            }
        }
    }

    public String getMessage(String key) {
        return messages.getOrDefault(key, key);
    }

    public String getMessage(String key, Map<String, String> placeholders) {
        String message = getMessage(key);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }

    public Component getComponent(String key) {
        String message = getMessage(key);
        try {
            return miniMessage.deserialize(message);
        } catch (Exception e) {
            return Component.text(message, NamedTextColor.WHITE);
        }
    }

    public Component getComponent(String key, Map<String, String> placeholders) {
        String message = getMessage(key, placeholders);
        try {
            return miniMessage.deserialize(message);
        } catch (Exception e) {
            return Component.text(message, NamedTextColor.WHITE);
        }
    }

    public String getLanguage() {
        return language;
    }
}
