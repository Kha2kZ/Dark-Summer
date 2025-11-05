package com.boxpvp.core.data;

import com.boxpvp.core.BoxPvPCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WarpManager {
    
    private final BoxPvPCore plugin;
    private final File warpsFile;
    private FileConfiguration warpsConfig;
    private final Map<String, Location> warps;
    
    public WarpManager(BoxPvPCore plugin) {
        this.plugin = plugin;
        this.warpsFile = new File(plugin.getDataFolder(), "warps.yml");
        this.warps = new HashMap<>();
        
        loadWarps();
    }
    
    private void loadWarps() {
        if (!warpsFile.exists()) {
            try {
                warpsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create warps.yml file!");
                e.printStackTrace();
            }
        }
        
        warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);
        
        ConfigurationSection warpsSection = warpsConfig.getConfigurationSection("warps");
        if (warpsSection != null) {
            for (String warpName : warpsSection.getKeys(false)) {
                String path = "warps." + warpName + ".";
                
                String worldName = warpsConfig.getString(path + "world");
                double x = warpsConfig.getDouble(path + "x");
                double y = warpsConfig.getDouble(path + "y");
                double z = warpsConfig.getDouble(path + "z");
                float yaw = (float) warpsConfig.getDouble(path + "yaw");
                float pitch = (float) warpsConfig.getDouble(path + "pitch");
                
                if (worldName != null && Bukkit.getWorld(worldName) != null) {
                    Location location = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
                    warps.put(warpName.toLowerCase(), location);
                }
            }
        }
        
        plugin.getLogger().info("Loaded " + warps.size() + " warps");
    }
    
    public void setWarp(String name, Location location) {
        String warpName = name.toLowerCase();
        warps.put(warpName, location);
        
        String path = "warps." + warpName + ".";
        warpsConfig.set(path + "world", location.getWorld().getName());
        warpsConfig.set(path + "x", location.getX());
        warpsConfig.set(path + "y", location.getY());
        warpsConfig.set(path + "z", location.getZ());
        warpsConfig.set(path + "yaw", location.getYaw());
        warpsConfig.set(path + "pitch", location.getPitch());
        
        saveWarps();
    }
    
    public Location getWarp(String name) {
        return warps.get(name.toLowerCase());
    }
    
    public boolean warpExists(String name) {
        return warps.containsKey(name.toLowerCase());
    }
    
    public Set<String> getWarpNames() {
        return warps.keySet();
    }
    
    public void deleteWarp(String name) {
        String warpName = name.toLowerCase();
        warps.remove(warpName);
        warpsConfig.set("warps." + warpName, null);
        saveWarps();
    }
    
    private void saveWarps() {
        try {
            warpsConfig.save(warpsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save warps.yml file!");
            e.printStackTrace();
        }
    }
}
