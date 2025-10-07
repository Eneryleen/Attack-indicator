package org.eneryleen.attackIndicator;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand implements CommandExecutor, TabCompleter {

    private final AttackIndicator plugin;

    public ReloadCommand(AttackIndicator plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("Usage: /attackindicator <reload>", NamedTextColor.YELLOW));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("attackindicator.reload")) {
                sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
                return true;
            }

            try {
                plugin.getConfigManager().loadConfig();
                sender.sendMessage(Component.text("AttackIndicator configuration reloaded successfully!", NamedTextColor.GREEN));
            } catch (Exception e) {
                sender.sendMessage(Component.text("Failed to reload configuration: " + e.getMessage(), NamedTextColor.RED));
                plugin.getLogger().severe("Failed to reload configuration: " + e.getMessage());
                e.printStackTrace();
            }

            return true;
        }

        sender.sendMessage(Component.text("Unknown subcommand. Usage: /attackindicator <reload>", NamedTextColor.YELLOW));
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
