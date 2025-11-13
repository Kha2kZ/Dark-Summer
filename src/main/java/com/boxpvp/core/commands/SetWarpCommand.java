package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWarpCommand implements CommandExecutor {

    private final BoxPvPCore plugin;

    public SetWarpCommand(BoxPvPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        String prefix = plugin.getConfig().getString("messages.prefix", "");

        if (!player.hasPermission("boxpvp.admin")) {
            String message = plugin.getConfig().getString("messages.no-permission", "§cYou don't have permission!");
            player.sendMessage(prefix + message);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(prefix + "§cUsage: /setwarp <arena|spawn|event|top>");
            return true;
        }

        String warpName = args[0].toLowerCase();

        plugin.getWarpManager().setWarp(warpName, player.getLocation());
        
        player.sendMessage(prefix + "§aWarp '§e" + warpName + "§a' has been set!");

        return true;
    }
}