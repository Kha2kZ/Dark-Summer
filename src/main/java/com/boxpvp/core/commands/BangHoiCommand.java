
package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BangHoiCommand implements CommandExecutor {
    
    private final BoxPvPCore plugin;
    
    public BangHoiCommand(BoxPvPCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cChỉ người chơi mới có thể sử dụng lệnh này!");
            return true;
        }
        
        Player player = (Player) sender;
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        
        if (args.length == 0) {
            player.sendMessage(prefix + "§cSử dụng: /banghoi <create|disband|list|chat>");
            return true;
        }
        
        // TODO: Implement bang hội system
        player.sendMessage(prefix + "§eHệ thống bang hội đang được phát triển...");
        
        return true;
    }
}
