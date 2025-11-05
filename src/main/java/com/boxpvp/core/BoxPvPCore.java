package com.boxpvp.core;

import com.boxpvp.core.commands.*;
import com.boxpvp.core.data.PlayerDataManager;
import com.boxpvp.core.listeners.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class BoxPvPCore extends JavaPlugin {
    
    private static BoxPvPCore instance;
    private PlayerDataManager playerDataManager;
    private Logger logger;
    
    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        
        logger.info("BoxPvPCore is starting...");
        
        saveDefaultConfig();
        
        playerDataManager = new PlayerDataManager(this);
        
        registerCommands();
        registerListeners();
        
        logger.info("BoxPvPCore has been enabled successfully!");
    }
    
    @Override
    public void onDisable() {
        logger.info("BoxPvPCore is shutting down...");
        
        if (playerDataManager != null) {
            playerDataManager.saveAllData();
        }
        
        logger.info("BoxPvPCore has been disabled!");
    }
    
    private void registerCommands() {
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("stats").setExecutor(new StatsCommand(this));
        getCommand("shop").setExecutor(new ShopCommand(this));
        getCommand("arena").setExecutor(new ArenaCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }
    
    public static BoxPvPCore getInstance() {
        return instance;
    }
    
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}
