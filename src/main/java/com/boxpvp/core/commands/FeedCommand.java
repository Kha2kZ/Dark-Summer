package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCommand implements CommandExecutor {
    
    private final BoxPvPCore plugin;
    
    public FeedCommand(BoxPvPCore plugin) {
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
        
        if (!rank.canUseFeed()) {
            player.sendMessage(prefix + "§cYou need at least MVP rank to use this command!");
            return true;
        }
        
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.sendMessage(prefix + "§aYou have been fed!");
        
        return true;
    }
}
