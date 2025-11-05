package com.boxpvp.core.listeners;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    
    private final BoxPvPCore plugin;
    
    public PlayerListener(BoxPvPCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.setLastLogin(System.currentTimeMillis());
        
        if (!player.hasPlayedBefore()) {
            String prefix = plugin.getConfig().getString("messages.prefix", "");
            player.sendMessage(prefix + "§aWelcome to Box PvP! Type §e/help §afor commands.");
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayerDataManager().unloadPlayerData(player.getUniqueId());
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        
        PlayerData victimData = plugin.getPlayerDataManager().getPlayerData(victim);
        victimData.addDeath();
        
        double deathPenalty = plugin.getConfig().getDouble("economy.death-penalty", 25);
        if (victimData.hasBalance(deathPenalty)) {
            victimData.removeBalance(deathPenalty);
            victim.sendMessage(plugin.getConfig().getString("messages.prefix", "") + 
                "§cYou lost §e$" + deathPenalty + " §cfor dying!");
        }
        
        if (killer != null && killer != victim) {
            PlayerData killerData = plugin.getPlayerDataManager().getPlayerData(killer);
            killerData.addKill();
            
            double killReward = plugin.getConfig().getDouble("economy.kill-reward", 50);
            killerData.addBalance(killReward);
            
            killer.sendMessage(plugin.getConfig().getString("messages.prefix", "") + 
                "§a+1 Kill! You earned §e$" + killReward + "§a!");
        }
    }
}
