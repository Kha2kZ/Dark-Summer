package com.boxpvp.core;

import com.boxpvp.core.commands.*;
import com.boxpvp.core.data.*;
import com.boxpvp.core.items.CustomItemManager;
import com.boxpvp.core.trade.NPCTradeManager;
import com.boxpvp.core.kits.StarterKitManager;
import com.boxpvp.core.listeners.PlayerListener;
import com.boxpvp.core.listeners.NPCTradeListener;
import com.boxpvp.core.listeners.StarterKitListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class BoxPvPCore extends JavaPlugin {
    
    private static BoxPvPCore instance;
    private PlayerDataManager playerDataManager;
    private WarpManager warpManager;
    private AuctionManager auctionManager;
    private TradeManager tradeManager;
    private GiftCodeManager giftCodeManager;
    private RankManager rankManager;
    private VirtualChestManager virtualChestManager;
    private AutoCraftingManager autoCraftingManager;
    private CustomItemManager customItemManager;
    private NPCTradeManager npcTradeManager;
    private StarterKitManager starterKitManager;
    private Logger logger;
    
    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        
        logger.info("BoxPvPCore is starting...");
        
        saveDefaultConfig();
        
        playerDataManager = new PlayerDataManager(this);
        warpManager = new WarpManager(this);
        auctionManager = new AuctionManager(this);
        tradeManager = new TradeManager();
        giftCodeManager = new GiftCodeManager(this);
        rankManager = new RankManager(this);
        virtualChestManager = new VirtualChestManager(this);
        autoCraftingManager = new AutoCraftingManager(this);
        customItemManager = new CustomItemManager(this);
        npcTradeManager = new NPCTradeManager(this);
        starterKitManager = new StarterKitManager(this);
        
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
        
        if (auctionManager != null) {
            auctionManager.saveAll();
        }
        
        if (giftCodeManager != null) {
            giftCodeManager.saveAll();
        }
        
        if (autoCraftingManager != null) {
            autoCraftingManager.saveAll();
        }
        
        if (virtualChestManager != null) {
            virtualChestManager.saveAllLoadedVaults();
        }
        
        logger.info("BoxPvPCore has been disabled!");
    }
    
    private void registerCommands() {
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("stats").setExecutor(new StatsCommand(this));
        getCommand("arena").setExecutor(new ArenaCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("warp").setExecutor(new WarpCommand(this));
        getCommand("setwarp").setExecutor(new SetWarpCommand(this));
        getCommand("trade").setExecutor(new TradeCommand(this, tradeManager));
        getCommand("ah").setExecutor(new AuctionHouseCommand(this, auctionManager));
        getCommand("giftcode").setExecutor(new GiftCodeCommand(this));
        getCommand("creategiftcode").setExecutor(new CreateGiftCodeCommand(this));
        getCommand("rank").setExecutor(new RankCommand(this));
        getCommand("pv").setExecutor(new PvCommand(this));
        getCommand("nick").setExecutor(new NickCommand(this));
        getCommand("feed").setExecutor(new FeedCommand(this));
        getCommand("heal").setExecutor(new HealCommand(this));
        getCommand("fly").setExecutor(new FlyCommand(this));
        getCommand("invsee").setExecutor(new InvseeCommand(this));
        getCommand("tuchetao").setExecutor(new TuChetaoCommand(this));
        getCommand("item").setExecutor(new ItemCommand(this));
        getCommand("npc").setExecutor(new NPCCommand(this));
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new com.boxpvp.core.listeners.MiningListener(this), this);
        getServer().getPluginManager().registerEvents(new NPCTradeListener(this), this);
        getServer().getPluginManager().registerEvents(new StarterKitListener(this), this);
    }
    
    public static BoxPvPCore getInstance() {
        return instance;
    }
    
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
    
    public WarpManager getWarpManager() {
        return warpManager;
    }
    
    public AuctionManager getAuctionManager() {
        return auctionManager;
    }
    
    public TradeManager getTradeManager() {
        return tradeManager;
    }
    
    public GiftCodeManager getGiftCodeManager() {
        return giftCodeManager;
    }
    
    public RankManager getRankManager() {
        return rankManager;
    }
    
    public VirtualChestManager getVirtualChestManager() {
        return virtualChestManager;
    }
    
    public AutoCraftingManager getAutoCraftingManager() {
        return autoCraftingManager;
    }
    
    public CustomItemManager getCustomItemManager() {
        return customItemManager;
    }
    
    public NPCTradeManager getNPCTradeManager() {
        return npcTradeManager;
    }
    
    public StarterKitManager getStarterKitManager() {
        return starterKitManager;
    }
}
