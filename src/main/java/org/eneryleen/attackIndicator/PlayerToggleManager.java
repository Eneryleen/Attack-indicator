package org.eneryleen.attackIndicator;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlayerToggleManager {

    private final AttackIndicator plugin;
    private final Set<UUID> disabledPlayers;
    private final File dataFile;

    public PlayerToggleManager(AttackIndicator plugin) {
        this.plugin = plugin;
        this.disabledPlayers = new HashSet<>();
        this.dataFile = new File(plugin.getDataFolder(), "disabled_players.yml");
        loadData();
    }

    public boolean isEnabled(UUID playerId) {
        return !disabledPlayers.contains(playerId);
    }

    public boolean toggle(UUID playerId) {
        if (disabledPlayers.contains(playerId)) {
            disabledPlayers.remove(playerId);
            saveData();
            return true;
        } else {
            disabledPlayers.add(playerId);
            saveData();
            return false;
        }
    }

    public void setEnabled(UUID playerId, boolean enabled) {
        if (enabled) {
            disabledPlayers.remove(playerId);
        } else {
            disabledPlayers.add(playerId);
        }
        saveData();
    }

    private void loadData() {
        if (!dataFile.exists()) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        for (String uuidString : config.getStringList("disabled")) {
            try {
                disabledPlayers.add(UUID.fromString(uuidString));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private void saveData() {
        FileConfiguration config = new YamlConfiguration();
        List<String> uuidList = new ArrayList<>();
        for (UUID uuid : disabledPlayers) {
            uuidList.add(uuid.toString());
        }
        config.set("disabled", uuidList);

        try {
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save disabled players data: " + e.getMessage());
        }
    }
}
