package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {
    
    private final BoxPvPCore plugin;
    
    public StatsCommand(BoxPvPCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;
        
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cYou must specify a player name!");
                return true;
            }
            target = (Player) sender;
        } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                String prefix = plugin.getConfig().getString("messages.prefix", "");
                String message = plugin.getConfig().getString("messages.player-not-found", "§cPlayer not found!");
                sender.sendMessage(prefix + message);
                return true;
            }
        }
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);
        
        sender.sendMessage("§8§m----------------------------");
        sender.sendMessage("§6§l" + target.getName() + "'s Statistics");
        sender.sendMessage("");
        sender.sendMessage("§eKills: §f" + data.getKills());
        sender.sendMessage("§eDeaths: §f" + data.getDeaths());
        sender.sendMessage("§eK/D Ratio: §f" + String.format("%.2f", data.getKDRatio()));
        sender.sendMessage("§eBalance: §a$" + String.format("%.2f", data.getBalance()));
        sender.sendMessage("§8§m----------------------------");
        
        return true;
    }
}
