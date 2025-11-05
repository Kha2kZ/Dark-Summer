package com.boxpvp.core.data;

import java.util.*;

public class TradeManager {
    
    private final Map<UUID, UUID> pendingRequests;
    private final Map<UUID, TradeSession> activeTrades;
    
    public TradeManager() {
        this.pendingRequests = new HashMap<>();
        this.activeTrades = new HashMap<>();
    }
    
    public void sendTradeRequest(UUID requester, UUID target) {
        pendingRequests.put(target, requester);
    }
    
    public boolean hasPendingRequest(UUID target) {
        return pendingRequests.containsKey(target);
    }
    
    public UUID getPendingRequest(UUID target) {
        return pendingRequests.get(target);
    }
    
    public void removePendingRequest(UUID target) {
        pendingRequests.remove(target);
    }
    
    public TradeSession createTradeSession(UUID player1, UUID player2) {
        TradeSession session = new TradeSession(player1, player2);
        activeTrades.put(player1, session);
        activeTrades.put(player2, session);
        return session;
    }
    
    public TradeSession getTradeSession(UUID playerUuid) {
        return activeTrades.get(playerUuid);
    }
    
    public boolean isInTrade(UUID playerUuid) {
        return activeTrades.containsKey(playerUuid);
    }
    
    public void endTradeSession(UUID playerUuid) {
        TradeSession session = activeTrades.get(playerUuid);
        if (session != null) {
            activeTrades.remove(session.getPlayer1Uuid());
            activeTrades.remove(session.getPlayer2Uuid());
        }
    }
    
    public void cleanupExpiredRequests(long maxAge) {
        long currentTime = System.currentTimeMillis();
        pendingRequests.entrySet().removeIf(entry -> 
            currentTime - System.currentTimeMillis() > maxAge
        );
    }
}
