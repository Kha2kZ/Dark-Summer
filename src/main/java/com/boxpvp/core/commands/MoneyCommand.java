
package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {
    
    private final BoxPvPCore plugin;
    
    public MoneyCommand(BoxPvPCore plugin) {
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
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        player.sendMessage("§8§m                                ");
        player.sendMessage("  §6§lYOUR BALANCE");
        player.sendMessage("");
        player.sendMessage("  §e💰 Money: §f" + String.format("%.2f", data.getMoney()) + " coins");
        player.sendMessage("  §b💎 Gems: §f" + data.getGems() + " gems");
        player.sendMessage("§8§m                                ");
        
        return true;
    }
}
