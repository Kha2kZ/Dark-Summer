package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.PlayerData;
import com.boxpvp.core.data.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class NickCommand implements CommandExecutor {
    
    private final BoxPvPCore plugin;
    private static final Pattern RGB_PATTERN = Pattern.compile("&#[0-9a-fA-F]{6}");
    
    public NickCommand(BoxPvPCore plugin) {
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
        Rank rank = plugin.getRankManager().getRank(player);
        
        if (!rank.canUseNick()) {
            player.sendMessage(prefix + "§cYou need at least MVP rank to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(prefix + "§cUsage: /nick <nickname>");
            player.sendMessage(prefix + "§7Use /nick reset to remove your nickname");
            return true;
        }
        
        String nickname = String.join(" ", args);
        
        if (nickname.equalsIgnoreCase("reset")) {
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
            data.setNickname(null);
            plugin.getPlayerDataManager().savePlayerData(player.getUniqueId());
            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getName());
            player.sendMessage(prefix + "§aYour nickname has been reset!");
            return true;
        }
        
        if (nickname.length() > 16) {
            player.sendMessage(prefix + "§cNickname must be 16 characters or less!");
            return true;
        }
        
        if (RGB_PATTERN.matcher(nickname).find() && !rank.canUseRGBNick()) {
            player.sendMessage(prefix + "§cYou need at least RICHKID rank to use RGB colors!");
            return true;
        }
        
        String colored = translateHexColorCodes(nickname);
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.setNickname(colored);
        plugin.getPlayerDataManager().savePlayerData(player.getUniqueId());
        
        player.setDisplayName(colored);
        player.setPlayerListName(colored);
        
        player.sendMessage(prefix + "§aYour nickname has been set to: " + colored);
        
        return true;
    }
    
    private String translateHexColorCodes(String message) {
        Pattern pattern = Pattern.compile("&#([0-9a-fA-F]{6})");
        java.util.regex.Matcher matcher = pattern.matcher(message);
        
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String hex = matcher.group(1);
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of("#" + hex).toString());
        }
        matcher.appendTail(buffer);
        
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
}
