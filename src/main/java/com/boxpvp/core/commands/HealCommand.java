package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.Rank;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand implements CommandExecutor {
    
    private final BoxPvPCore plugin;
    
    public HealCommand(BoxPvPCore plugin) {
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
        
        if (!rank.canUseHeal()) {
            player.sendMessage(prefix + "§cYou need at least MVP+ rank to use this command!");
            return true;
        }
        
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        player.setHealth(maxHealth);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setFireTicks(0);
        
        player.sendMessage(prefix + "§aYou have been healed!");
        
        return true;
    }
}
