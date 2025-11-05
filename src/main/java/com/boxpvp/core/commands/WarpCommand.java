package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class WarpCommand implements CommandExecutor {
    
    private final BoxPvPCore plugin;
    
    public WarpCommand(BoxPvPCore plugin) {
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
        
        if (args.length == 0) {
            Set<String> warpNames = plugin.getWarpManager().getWarpNames();
            
            if (warpNames.isEmpty()) {
                player.sendMessage(prefix + "§cNo warps available!");
                return true;
            }
            
            player.sendMessage("§8§m----------------------------");
            player.sendMessage("§6§lAvailable Warps");
            player.sendMessage("");
            for (String warpName : warpNames) {
                player.sendMessage("  §e/warp " + warpName);
            }
            player.sendMessage("§8§m----------------------------");
            return true;
        }
        
        String warpName = args[0].toLowerCase();
        
        if (!plugin.getWarpManager().warpExists(warpName)) {
            player.sendMessage(prefix + "§cWarp '§e" + warpName + "§c' does not exist!");
            player.sendMessage(prefix + "§7Use §e/warp §7to see available warps.");
            return true;
        }
        
        Location warpLocation = plugin.getWarpManager().getWarp(warpName);
        player.teleport(warpLocation);
        
        player.sendMessage(prefix + "§aTeleported to §e" + warpName + "§a!");
        
        return true;
    }
}
