package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvseeCommand implements CommandExecutor {
    
    private final BoxPvPCore plugin;
    
    public InvseeCommand(BoxPvPCore plugin) {
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
        
        if (!rank.canUseInvsee()) {
            player.sendMessage(prefix + "§cYou need at least VIP rank to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(prefix + "§cUsage: /invsee <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        
        if (target == null || !target.isOnline()) {
            player.sendMessage(prefix + plugin.getConfig().getString("messages.player-not-found", "§cPlayer not found!"));
            return true;
        }
        
        player.openInventory(target.getInventory());
        player.sendMessage(prefix + "§aViewing inventory of " + target.getName());
        
        return true;
    }
}
