package org.eneryleen.attackIndicator;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class AttackIndicator extends JavaPlugin {

    private static AttackIndicator instance;
    private ConfigManager configManager;
    private LangManager langManager;
    private IndicatorManager indicatorManager;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        configManager.loadConfig();

        langManager = new LangManager(this);
        langManager.loadLanguage(configManager.getLanguage());

        indicatorManager = new IndicatorManager(this);

        getServer().getPluginManager().registerEvents(new DamageListener(this), this);

        ReloadCommand reloadCommand = new ReloadCommand(this);
        getCommand("attackindicator").setExecutor(reloadCommand);

        int pluginId = 27487;
        new Metrics(this, pluginId);

        UpdateChecker updateChecker = new UpdateChecker(this);
        updateChecker.checkForUpdates();

        getLogger().info("AttackIndicator v" + getDescription().getVersion() + " enabled!");
        getLogger().info("Using Display Entities for damage indicators");
    }

    @Override
    public void onDisable() {
        if (indicatorManager != null) {
            indicatorManager.cleanup();
        }

        getLogger().info("AttackIndicator disabled!");
    }

    public static AttackIndicator getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LangManager getLangManager() {
        return langManager;
    }

    public IndicatorManager getIndicatorManager() {
        return indicatorManager;
    }
}
