package com.boxpvp.core.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TradeSession {
    
    private final UUID player1Uuid;
    private final UUID player2Uuid;
    private final Map<Integer, ItemStack> player1Items;
    private final Map<Integer, ItemStack> player2Items;
    private boolean player1Confirmed;
    private boolean player2Confirmed;
    private final long startTime;
    
    public TradeSession(UUID player1Uuid, UUID player2Uuid) {
        this.player1Uuid = player1Uuid;
        this.player2Uuid = player2Uuid;
        this.player1Items = new HashMap<>();
        this.player2Items = new HashMap<>();
        this.player1Confirmed = false;
        this.player2Confirmed = false;
        this.startTime = System.currentTimeMillis();
    }
    
    public UUID getPlayer1Uuid() {
        return player1Uuid;
    }
    
    public UUID getPlayer2Uuid() {
        return player2Uuid;
    }
    
    public UUID getOtherPlayer(UUID playerUuid) {
        return playerUuid.equals(player1Uuid) ? player2Uuid : player1Uuid;
    }
    
    public boolean isPlayer1(UUID playerUuid) {
        return player1Uuid.equals(playerUuid);
    }
    
    public Map<Integer, ItemStack> getPlayerItems(UUID playerUuid) {
        return isPlayer1(playerUuid) ? player1Items : player2Items;
    }
    
    public void addItem(UUID playerUuid, int slot, ItemStack item) {
        if (isPlayer1(playerUuid)) {
            player1Items.put(slot, item);
            player1Confirmed = false;
        } else {
            player2Items.put(slot, item);
            player2Confirmed = false;
        }
    }
    
    public void removeItem(UUID playerUuid, int slot) {
        if (isPlayer1(playerUuid)) {
            player1Items.remove(slot);
            player1Confirmed = false;
        } else {
            player2Items.remove(slot);
            player2Confirmed = false;
        }
    }
    
    public void setConfirmed(UUID playerUuid, boolean confirmed) {
        if (isPlayer1(playerUuid)) {
            player1Confirmed = confirmed;
        } else {
            player2Confirmed = confirmed;
        }
    }
    
    public boolean isPlayerConfirmed(UUID playerUuid) {
        return isPlayer1(playerUuid) ? player1Confirmed : player2Confirmed;
    }
    
    public boolean areBothConfirmed() {
        return player1Confirmed && player2Confirmed;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public boolean involves(UUID playerUuid) {
        return player1Uuid.equals(playerUuid) || player2Uuid.equals(playerUuid);
    }
}
