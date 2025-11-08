package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {
    
    private final BoxPvPCore plugin;
    
    public FlyCommand(BoxPvPCore plugin) {
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
        
        if (!rank.canUseFly()) {
            player.sendMessage(prefix + "§cYou need at least DARK rank to use this command!");
            return true;
        }
        
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.sendMessage(prefix + "§cFly mode disabled!");
        } else {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.sendMessage(prefix + "§aFly mode enabled!");
        }
        
        return true;
    }
}
