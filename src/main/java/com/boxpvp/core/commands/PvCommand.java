package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class PvCommand implements CommandExecutor, Listener {
    
    private final BoxPvPCore plugin;
    
    public PvCommand(BoxPvPCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
            player.sendMessage(prefix + "§cUsage: /pv <1-40>");
            return true;
        }
        
        int vaultNumber;
        try {
            vaultNumber = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(prefix + "§cInvalid vault number!");
            return true;
        }
        
        if (vaultNumber < 1 || vaultNumber > 40) {
            player.sendMessage(prefix + "§cVault number must be between 1 and 40!");
            return true;
        }
        
        Rank rank = plugin.getRankManager().getRank(player);
        
        if (vaultNumber > rank.getMaxVaults()) {
            player.sendMessage(prefix + "§cYour rank only allows access to " + rank.getMaxVaults() + " vaults!");
            player.sendMessage(prefix + "§7Upgrade your rank with /rank to unlock more vaults!");
            return true;
        }
        
        Inventory vault = plugin.getVirtualChestManager().getVault(player, vaultNumber);
        player.openInventory(vault);
        
        return true;
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getPlayer();
        String title = event.getView().getTitle();
        
        if (title.startsWith("§6§lVault #")) {
            try {
                String numberStr = title.substring(11);
                int vaultNumber = Integer.parseInt(numberStr);
                plugin.getVirtualChestManager().saveVault(player.getUniqueId(), vaultNumber);
            } catch (Exception e) {
            }
        }
    }
}
