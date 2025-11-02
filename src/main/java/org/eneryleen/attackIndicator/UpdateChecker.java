package org.eneryleen.attackIndicator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class UpdateChecker {

    private static final String MODRINTH_API_URL = "https://api.modrinth.com/v2/project/%s/version";
    private static final String PROJECT_ID = "attack-indicator";
    private static final String USER_AGENT = "AttackIndicator/2.1 (github.com/eneryleen/attack-indicator)";

    private final JavaPlugin plugin;
    private final Logger logger;
    private final String currentVersion;

    public UpdateChecker(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.currentVersion = plugin.getDescription().getVersion();
    }

    public void checkForUpdates() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String apiUrl = String.format(MODRINTH_API_URL, PROJECT_ID);
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", USER_AGENT);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    logger.warning("Failed to check for updates: HTTP " + responseCode);
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JsonArray versions = JsonParser.parseString(response.toString()).getAsJsonArray();
                if (versions.size() == 0) {
                    return;
                }

                JsonElement latestVersionElement = versions.get(0).getAsJsonObject().get("version_number");
                String latestVersion = latestVersionElement.getAsString();

                if (!currentVersion.equals(latestVersion)) {
                    logger.warning("╔══════════════════════════════════════════════════════╗");
                    logger.warning("║                                                      ║");
                    logger.warning("║   A new version of AttackIndicator is available!     ║");
                    logger.warning("║                                                      ║");
                    logger.warning("║   Current version: " + String.format("%-27s", currentVersion) + "       ║");
                    logger.warning("║   Latest version:  " + String.format("%-27s", latestVersion) + "       ║");
                    logger.warning("║                                                      ║");
                    logger.warning("║   https://modrinth.com/plugin/attack-indicator       ║");
                    logger.warning("║                                                      ║");
                    logger.warning("╚══════════════════════════════════════════════════════╝");
                } else {
                    logger.info("AttackIndicator is up to date!");
                }

            } catch (Exception e) {
                logger.warning("Failed to check for updates: " + e.getMessage());
            }
        });
    }
}
