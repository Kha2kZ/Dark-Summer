package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommand implements CommandExecutor {
    
    private final BoxPvPCore plugin;
    
    public ArenaCommand(BoxPvPCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "join":
                handleJoin(player);
                break;
            case "leave":
                handleLeave(player);
                break;
            case "list":
                handleList(player);
                break;
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void sendHelp(Player player) {
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        player.sendMessage("§8§m----------------------------");
        player.sendMessage(prefix + "§6Arena Commands");
        player.sendMessage("§e/arena join §7- Join the arena queue");
        player.sendMessage("§e/arena leave §7- Leave the arena");
        player.sendMessage("§e/arena list §7- List active matches");
        player.sendMessage("§8§m----------------------------");
    }
    
    private void handleJoin(Player player) {
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        player.sendMessage(prefix + "§aYou have joined the arena queue!");
        player.sendMessage(prefix + "§7Waiting for opponent...");
    }
    
    private void handleLeave(Player player) {
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        player.sendMessage(prefix + "§cYou have left the arena!");
    }
    
    private void handleList(Player player) {
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        player.sendMessage("§8§m----------------------------");
        player.sendMessage(prefix + "§6Active Arena Matches");
        player.sendMessage("§7No active matches at the moment");
        player.sendMessage("§8§m----------------------------");
    }
}
