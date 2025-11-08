package com.boxpvp.core.data;

import com.boxpvp.core.BoxPvPCore;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RankManager {
    
    private final BoxPvPCore plugin;
    private final Map<UUID, Rank> displayRankOverrides;
    private final Map<UUID, String> customRankNames;
    
    public RankManager(BoxPvPCore plugin) {
        this.plugin = plugin;
        this.displayRankOverrides = new HashMap<>();
        this.customRankNames = new HashMap<>();
    }
    
    public Rank getRank(Player player) {
        return getRank(player.getUniqueId());
    }
    
    public Rank getRank(UUID uuid) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(uuid);
        return data.getRank();
    }
    
    public Rank getDisplayRank(UUID uuid) {
        if (displayRankOverrides.containsKey(uuid)) {
            return displayRankOverrides.get(uuid);
        }
        return getRank(uuid);
    }
    
    public String getDisplayName(UUID uuid) {
        Rank displayRank = getDisplayRank(uuid);
        if (displayRank == Rank.CUSTOM && customRankNames.containsKey(uuid)) {
            return Rank.CUSTOM.getColorCode() + customRankNames.get(uuid);
        }
        return displayRank.getDisplayName();
    }
    
    public void setDisplayRankOverride(UUID uuid, Rank rank) {
        if (rank.isStaffRank()) {
            displayRankOverrides.put(uuid, rank);
        }
    }
    
    public void removeDisplayRankOverride(UUID uuid) {
        displayRankOverrides.remove(uuid);
    }
    
    public void setCustomRankName(UUID uuid, String name) {
        customRankNames.put(uuid, name);
    }
    
    public boolean canPurchaseRank(Player player, Rank rank) {
        if (!rank.isPurchasable()) {
            return false;
        }
        
        Rank currentRank = getRank(player);
        
        if (currentRank.ordinal() >= rank.ordinal()) {
            return false;
        }
        
        if (rank.ordinal() > currentRank.ordinal() + 1) {
            return false;
        }
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        return data.getCoins() >= rank.getPrice();
    }
    
    public boolean purchaseRank(Player player, Rank rank) {
        if (!canPurchaseRank(player, rank)) {
            return false;
        }
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.removeCoins(rank.getPrice());
        data.setRank(rank);
        
        plugin.getPlayerDataManager().savePlayerData(player.getUniqueId());
        
        return true;
    }
    
    public void setRank(Player player, Rank rank) {
        setRank(player.getUniqueId(), rank);
    }
    
    public void setRank(UUID uuid, Rank rank) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(uuid);
        data.setRank(rank);
        plugin.getPlayerDataManager().savePlayerData(uuid);
    }
    
    public boolean hasPermission(Player player, String permission) {
        Rank rank = getRank(player);
        
        switch (permission) {
            case "nick":
                return rank.canUseNick();
            case "feed":
                return rank.canUseFeed();
            case "heal":
                return rank.canUseHeal();
            case "fly":
                return rank.canUseFly();
            case "invsee":
                return rank.canUseInvsee();
            case "autocraft":
                return rank.canUseAutoCraft();
            case "mute":
                return rank == Rank.HELPER || rank == Rank.POLICE || rank == Rank.ADMIN || rank == Rank.OWNER;
            case "ban":
            case "kick":
                return rank == Rank.POLICE || rank == Rank.ADMIN || rank == Rank.OWNER;
            case "admin":
                return rank == Rank.ADMIN || rank == Rank.OWNER;
            default:
                return false;
        }
    }
}
