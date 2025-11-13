package com.boxpvp.core.listeners;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.PlayerData;
import com.boxpvp.core.data.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final BoxPvPCore plugin;

    public PlayerListener(BoxPvPCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayerDataManager().loadPlayerData(player.getUniqueId());

        // Set tab list name with rank prefix
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        Rank rank = data.getRank();
        player.setPlayerListName(rank.getColor() + "[" + rank.getDisplayName() + "] §f" + player.getName());

        String prefix = plugin.getConfig().getString("messages.prefix", "");
        event.setJoinMessage(prefix + "§e" + player.getName() + " §ahas joined the server!");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        Rank rank = data.getRank();

        event.setFormat(rank.getColor() + "[" + rank.getDisplayName() + "] §f" + player.getName() + " §8» §f" + event.getMessage());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayerDataManager().unloadPlayerData(player.getUniqueId());
        plugin.getVirtualChestManager().unloadVaults(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        PlayerData victimData = plugin.getPlayerDataManager().getPlayerData(victim.getUniqueId());
        Rank victimRank = victimData.getRank();
        victimData.addDeath();

        String prefix = plugin.getConfig().getString("messages.prefix", "");

        double deathMoneyPenalty = victimRank.getDeathMoneyPenalty();
        if (deathMoneyPenalty > 0) {
            deathMoneyPenalty = plugin.getConfig().getDouble("economy.death-penalty", 25);
        }
        if (!victimRank.isImmuneToDeathPenalty() && deathMoneyPenalty > 0) {
            if (victimData.hasBalance(deathMoneyPenalty)) {
                victimData.removeBalance(deathMoneyPenalty);
                victim.sendMessage(prefix + "§cYou lost §e$" + deathMoneyPenalty + " §cfor dying!");
            }
        }

        double deathGemsPenalty = victimRank.getDeathGemsPenalty();
        if (deathGemsPenalty > 0 && victimData.hasGems(deathGemsPenalty)) {
            victimData.removeGems(deathGemsPenalty);
            victim.sendMessage(prefix + "§cYou lost §b" + deathGemsPenalty + " gems §cfor dying!");
        }

        if (killer != null && killer != victim) {
            PlayerData killerData = plugin.getPlayerDataManager().getPlayerData(killer.getUniqueId());
            Rank killerRank = killerData.getRank();
            killerData.addKill();

            double killReward = plugin.getConfig().getDouble("economy.kill-reward", 50);
            killerData.addBalance(killReward);
            killer.sendMessage(prefix + "§a+1 Kill! You earned §e$" + killReward + "§a!");

            if (killerRank.hasKillBonus()) {
                double bonusMoney = killerRank.getKillMoneyBonus();
                double bonusGems = killerRank.getKillGemsBonus();

                if (bonusMoney > 0) {
                    killerData.addBalance(bonusMoney);
                    killer.sendMessage(prefix + "§6Rank Bonus: §e+$" + bonusMoney + " money!");
                }

                if (bonusGems > 0) {
                    killerData.addGems(bonusGems);
                    killer.sendMessage(prefix + "§6Rank Bonus: §b+" + bonusGems + " gems!");
                }
            }
        }
    }
}