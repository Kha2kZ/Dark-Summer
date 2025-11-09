package com.boxpvp.core.listeners;

import com.boxpvp.core.BoxPvPCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class StarterKitListener implements Listener {
    
    private final BoxPvPCore plugin;

    public StarterKitListener(BoxPvPCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.getStarterKitManager().hasReceivedKit(event.getPlayer())) {
            plugin.getStarterKitManager().giveStarterKit(event.getPlayer());
        }
    }
}
