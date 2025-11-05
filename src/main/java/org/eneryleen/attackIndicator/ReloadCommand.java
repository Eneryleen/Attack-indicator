package org.eneryleen.attackIndicator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReloadCommand implements CommandExecutor, TabCompleter {

    private final AttackIndicator plugin;

    public ReloadCommand(AttackIndicator plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        LangManager lang = plugin.getLangManager();

        if (args.length == 0) {
            sender.sendMessage(lang.getFormatted("command.usage"));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("attackindicator.reload")) {
                sender.sendMessage(lang.getFormatted("command.no-permission"));
                return true;
            }

            try {
                plugin.getConfigManager().loadConfig();
                plugin.getLangManager().loadLanguage(plugin.getConfigManager().getLanguage());
                sender.sendMessage(lang.getFormatted("command.reload-success"));
            } catch (Exception e) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("error", e.getMessage());
                sender.sendMessage(lang.getFormatted("command.reload-error", placeholders));
                plugin.getLogger().severe("Failed to reload configuration: " + e.getMessage());
                e.printStackTrace();
            }

            return true;
        }

        sender.sendMessage(lang.getFormatted("command.unknown-subcommand"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("attackindicator.reload")) {
                completions.add("reload");
            }
        }

        return completions;
    }
}
