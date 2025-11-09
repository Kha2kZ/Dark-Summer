package com.boxpvp.core.trade;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class NPCTradeManager {
    
    private final Plugin plugin;
    private final Map<UUID, NPCTrade> npcTrades;
    private final File tradesFile;
    private FileConfiguration tradesConfig;

    public NPCTradeManager(Plugin plugin) {
        this.plugin = plugin;
        this.npcTrades = new HashMap<>();
        this.tradesFile = new File(plugin.getDataFolder(), "npc_trades.yml");
        loadTrades();
    }

    public void loadTrades() {
        if (!tradesFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                tradesFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create npc_trades.yml");
                return;
            }
        }
        
        tradesConfig = YamlConfiguration.loadConfiguration(tradesFile);
        npcTrades.clear();
        
        if (!tradesConfig.contains("npcs")) {
            plugin.getLogger().info("No NPC trades to load");
            return;
        }
        
        for (String npcIdStr : tradesConfig.getConfigurationSection("npcs").getKeys(false)) {
            String path = "npcs." + npcIdStr;
            
            UUID npcId = UUID.fromString(npcIdStr);
            String name = tradesConfig.getString(path + ".name");
            String worldName = tradesConfig.getString(path + ".location.world");
            double x = tradesConfig.getDouble(path + ".location.x");
            double y = tradesConfig.getDouble(path + ".location.y");
            double z = tradesConfig.getDouble(path + ".location.z");
            
            Location loc = new Location(plugin.getServer().getWorld(worldName), x, y, z);
            NPCTrade npcTrade = new NPCTrade(npcId, name, loc);
            
            if (tradesConfig.contains(path + ".recipes")) {
                for (String recipeId : tradesConfig.getConfigurationSection(path + ".recipes").getKeys(false)) {
                    String recipePath = path + ".recipes." + recipeId;
                    
                    List<ItemStack> inputs = (List<ItemStack>) tradesConfig.getList(recipePath + ".inputs");
                    ItemStack output = tradesConfig.getItemStack(recipePath + ".output");
                    
                    if (inputs != null && output != null) {
                        com.boxpvp.core.trade.TradeRecipe recipe = 
                            new com.boxpvp.core.trade.TradeRecipe(recipeId, inputs, output);
                        npcTrade.addRecipe(recipe);
                    }
                }
            }
            
            npcTrades.put(npcId, npcTrade);
        }
        
        plugin.getLogger().info("Loaded " + npcTrades.size() + " NPC traders");
    }

    public void saveTrades() {
        tradesConfig = new YamlConfiguration();
        
        for (Map.Entry<UUID, NPCTrade> entry : npcTrades.entrySet()) {
            String npcId = entry.getKey().toString();
            NPCTrade npc = entry.getValue();
            String path = "npcs." + npcId;
            
            tradesConfig.set(path + ".name", npc.getNpcName());
            Location loc = npc.getLocation();
            tradesConfig.set(path + ".location.world", loc.getWorld().getName());
            tradesConfig.set(path + ".location.x", loc.getX());
            tradesConfig.set(path + ".location.y", loc.getY());
            tradesConfig.set(path + ".location.z", loc.getZ());
            
            int recipeIndex = 0;
            for (com.boxpvp.core.trade.TradeRecipe recipe : npc.getRecipes()) {
                String recipePath = path + ".recipes." + recipeIndex;
                tradesConfig.set(recipePath + ".inputs", recipe.getInputItems());
                tradesConfig.set(recipePath + ".output", recipe.getOutputItem());
                recipeIndex++;
            }
        }
        
        try {
            tradesConfig.save(tradesFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save npc_trades.yml");
        }
    }

    public NPCTrade createNPCTrader(Location location, String name) {
        Villager npc = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        npc.setCustomName(name);
        npc.setCustomNameVisible(true);
        npc.setAI(false);
        npc.setInvulnerable(true);
        
        NPCTrade trade = new NPCTrade(npc.getUniqueId(), name, location);
        npcTrades.put(npc.getUniqueId(), trade);
        saveTrades();
        
        return trade;
    }

    public NPCTrade getNPCTrade(UUID npcId) {
        return npcTrades.get(npcId);
    }

    public NPCTrade getNPCTrade(Entity entity) {
        return npcTrades.get(entity.getUniqueId());
    }

    public void removeNPCTrader(UUID npcId) {
        npcTrades.remove(npcId);
        saveTrades();
    }

    public boolean isNPCTrader(Entity entity) {
        return npcTrades.containsKey(entity.getUniqueId());
    }

    public Collection<NPCTrade> getAllTrades() {
        return new ArrayList<>(npcTrades.values());
    }

    public NPCTrade getNPCByName(String name) {
        for (NPCTrade npc : npcTrades.values()) {
            if (npc.getNpcName().equalsIgnoreCase(name)) {
                return npc;
            }
        }
        return null;
    }

    public void addTradeRecipe(NPCTrade npc, List<ItemStack> inputs, ItemStack output) {
        String recipeId = "recipe_" + System.currentTimeMillis();
        com.boxpvp.core.trade.TradeRecipe recipe = new com.boxpvp.core.trade.TradeRecipe(recipeId, inputs, output);
        npc.addRecipe(recipe);
        saveTrades();
    }
}
