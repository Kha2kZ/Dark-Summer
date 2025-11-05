package com.boxpvp.core.data;

import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GiftCode {
    
    private final String code;
    private final Map<Integer, ItemStack> itemRewards;
    private double moneyReward;
    private double gemsReward;
    private double coinsReward;
    private final int maxUses;
    private final Set<UUID> usedPlayers;
    
    public GiftCode(String code, int maxUses) {
        this.code = code.toUpperCase();
        this.itemRewards = new HashMap<>();
        this.moneyReward = 0;
        this.gemsReward = 0;
        this.coinsReward = 0;
        this.maxUses = maxUses;
        this.usedPlayers = new HashSet<>();
    }
    
    public GiftCode(String code, Map<Integer, ItemStack> itemRewards, double moneyReward, 
                    double gemsReward, double coinsReward, int maxUses, Set<UUID> usedPlayers) {
        this.code = code.toUpperCase();
        this.itemRewards = itemRewards != null ? new HashMap<>(itemRewards) : new HashMap<>();
        this.moneyReward = moneyReward;
        this.gemsReward = gemsReward;
        this.coinsReward = coinsReward;
        this.maxUses = maxUses;
        this.usedPlayers = usedPlayers != null ? new HashSet<>(usedPlayers) : new HashSet<>();
    }
    
    public String getCode() {
        return code;
    }
    
    public List<ItemStack> getItemRewards() {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack item : itemRewards.values()) {
            if (item != null) {
                items.add(item.clone());
            }
        }
        return items;
    }
    
    public Map<Integer, ItemStack> getItemRewardsMap() {
        Map<Integer, ItemStack> copy = new HashMap<>();
        for (Map.Entry<Integer, ItemStack> entry : itemRewards.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().clone());
        }
        return copy;
    }
    
    public void setItemReward(int slot, ItemStack item) {
        if (item != null && item.getType() != org.bukkit.Material.AIR) {
            itemRewards.put(slot, item.clone());
        }
    }
    
    public void removeItemReward(int slot) {
        itemRewards.remove(slot);
    }
    
    public double getMoneyReward() {
        return moneyReward;
    }
    
    public void setMoneyReward(double moneyReward) {
        this.moneyReward = Math.max(0, moneyReward);
    }
    
    public double getGemsReward() {
        return gemsReward;
    }
    
    public void setGemsReward(double gemsReward) {
        this.gemsReward = Math.max(0, gemsReward);
    }
    
    public double getCoinsReward() {
        return coinsReward;
    }
    
    public void setCoinsReward(double coinsReward) {
        this.coinsReward = Math.max(0, coinsReward);
    }
    
    public int getMaxUses() {
        return maxUses;
    }
    
    public Set<UUID> getUsedPlayers() {
        return new HashSet<>(usedPlayers);
    }
    
    public boolean hasPlayerUsed(UUID playerUuid) {
        return usedPlayers.contains(playerUuid);
    }
    
    public void markAsUsed(UUID playerUuid) {
        usedPlayers.add(playerUuid);
    }
    
    public int getUsesRemaining() {
        if (maxUses == -1) {
            return -1;
        }
        return Math.max(0, maxUses - usedPlayers.size());
    }
    
    public boolean isAvailable() {
        return maxUses == -1 || usedPlayers.size() < maxUses;
    }
    
    public boolean hasRewards() {
        return !itemRewards.isEmpty() || moneyReward > 0 || gemsReward > 0 || coinsReward > 0;
    }
}
