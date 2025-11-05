package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {
    
    private final BoxPvPCore plugin;
    
    public SpawnCommand(BoxPvPCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        Location spawn = getSpawnLocation();
        
        player.teleport(spawn);
        
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        String message = plugin.getConfig().getString("messages.spawn-teleport", "§aTeleported to spawn!");
        player.sendMessage(prefix + message);
        
        return true;
    }
    
    private Location getSpawnLocation() {
        String worldName = plugin.getConfig().getString("spawn.world", "world");
        double x = plugin.getConfig().getDouble("spawn.x", 0.0);
        double y = plugin.getConfig().getDouble("spawn.y", 64.0);
        double z = plugin.getConfig().getDouble("spawn.z", 0.0);
        float yaw = (float) plugin.getConfig().getDouble("spawn.yaw", 0.0);
        float pitch = (float) plugin.getConfig().getDouble("spawn.pitch", 0.0);
        
        return new Location(plugin.getServer().getWorld(worldName), x, y, z, yaw, pitch);
    }
}
