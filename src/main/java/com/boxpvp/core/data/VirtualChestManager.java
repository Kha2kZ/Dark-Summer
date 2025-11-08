package com.boxpvp.core.data;

import com.boxpvp.core.BoxPvPCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VirtualChestManager {
    
    private final BoxPvPCore plugin;
    private final File vaultsFolder;
    private final Map<UUID, Map<Integer, Inventory>> vaultCache;
    
    public VirtualChestManager(BoxPvPCore plugin) {
        this.plugin = plugin;
        this.vaultsFolder = new File(plugin.getDataFolder(), "vaults");
        this.vaultCache = new HashMap<>();
        
        if (!vaultsFolder.exists()) {
            vaultsFolder.mkdirs();
        }
    }
    
    public Inventory getVault(Player player, int vaultNumber) {
        return getVault(player.getUniqueId(), vaultNumber);
    }
    
    public Inventory getVault(UUID uuid, int vaultNumber) {
        if (!vaultCache.containsKey(uuid)) {
            vaultCache.put(uuid, new HashMap<>());
        }
        
        Map<Integer, Inventory> playerVaults = vaultCache.get(uuid);
        
        if (playerVaults.containsKey(vaultNumber)) {
            return playerVaults.get(vaultNumber);
        }
        
        Inventory vault = loadVault(uuid, vaultNumber);
        playerVaults.put(vaultNumber, vault);
        return vault;
    }
    
    private Inventory loadVault(UUID uuid, int vaultNumber) {
        File file = new File(vaultsFolder, uuid.toString() + ".yml");
        Inventory vault = Bukkit.createInventory(null, 54, "§6§lVault #" + vaultNumber);
        
        if (!file.exists()) {
            return vault;
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        String path = "vaults." + vaultNumber + ".";
        
        for (int i = 0; i < 54; i++) {
            if (config.contains(path + i)) {
                ItemStack item = config.getItemStack(path + i);
                if (item != null) {
                    vault.setItem(i, item);
                }
            }
        }
        
        return vault;
    }
    
    public void saveVault(UUID uuid, int vaultNumber) {
        if (!vaultCache.containsKey(uuid)) {
            return;
        }
        
        Map<Integer, Inventory> playerVaults = vaultCache.get(uuid);
        if (!playerVaults.containsKey(vaultNumber)) {
            return;
        }
        
        File file = new File(vaultsFolder, uuid.toString() + ".yml");
        FileConfiguration config;
        
        if (file.exists()) {
            config = YamlConfiguration.loadConfiguration(file);
        } else {
            config = new YamlConfiguration();
        }
        
        Inventory vault = playerVaults.get(vaultNumber);
        String path = "vaults." + vaultNumber + ".";
        
        for (int i = 0; i < 54; i++) {
            ItemStack item = vault.getItem(i);
            if (item != null) {
                config.set(path + i, item);
            } else {
                config.set(path + i, null);
            }
        }
        
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save vault " + vaultNumber + " for " + uuid);
            e.printStackTrace();
        }
    }
    
    public void saveAllVaults(UUID uuid) {
        if (!vaultCache.containsKey(uuid)) {
            return;
        }
        
        Map<Integer, Inventory> playerVaults = vaultCache.get(uuid);
        for (int vaultNumber : playerVaults.keySet()) {
            saveVault(uuid, vaultNumber);
        }
    }
    
    public void unloadVaults(UUID uuid) {
        saveAllVaults(uuid);
        vaultCache.remove(uuid);
    }
    
    public void saveAllLoadedVaults() {
        plugin.getLogger().info("Saving all loaded vaults...");
        for (UUID uuid : vaultCache.keySet()) {
            saveAllVaults(uuid);
        }
        plugin.getLogger().info("Saved " + vaultCache.size() + " player vault data");
    }
}
