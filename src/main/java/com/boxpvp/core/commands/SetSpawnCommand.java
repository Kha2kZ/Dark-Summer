package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {
    
    private final BoxPvPCore plugin;
    
    public SetSpawnCommand(BoxPvPCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("boxpvp.admin")) {
            String prefix = plugin.getConfig().getString("messages.prefix", "");
            String message = plugin.getConfig().getString("messages.no-permission", "§cYou don't have permission!");
            player.sendMessage(prefix + message);
            return true;
        }
        
        Location loc = player.getLocation();
        
        plugin.getConfig().set("spawn.world", loc.getWorld().getName());
        plugin.getConfig().set("spawn.x", loc.getX());
        plugin.getConfig().set("spawn.y", loc.getY());
        plugin.getConfig().set("spawn.z", loc.getZ());
        plugin.getConfig().set("spawn.yaw", loc.getYaw());
        plugin.getConfig().set("spawn.pitch", loc.getPitch());
        plugin.saveConfig();
        
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        String message = plugin.getConfig().getString("messages.spawn-set", "§aSpawn point has been set!");
        player.sendMessage(prefix + message);
        
        return true;
    }
}
