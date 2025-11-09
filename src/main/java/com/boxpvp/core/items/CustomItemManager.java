package com.boxpvp.core.items;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CustomItemManager {
    
    private final Plugin plugin;
    private final Map<String, CustomItem> customItems;
    private final File itemsFile;
    private FileConfiguration itemsConfig;

    public CustomItemManager(Plugin plugin) {
        this.plugin = plugin;
        this.customItems = new HashMap<>();
        this.itemsFile = new File(plugin.getDataFolder(), "custom_items.yml");
        loadItems();
    }

    public void loadItems() {
        if (!itemsFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                itemsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create custom_items.yml");
                return;
            }
        }
        
        itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);
        customItems.clear();
        
        if (!itemsConfig.contains("items")) {
            return;
        }
        
        for (String id : itemsConfig.getConfigurationSection("items").getKeys(false)) {
            String path = "items." + id;
            
            String displayName = itemsConfig.getString(path + ".display_name");
            Material material = Material.valueOf(itemsConfig.getString(path + ".material"));
            ItemLevel level = ItemLevel.fromString(itemsConfig.getString(path + ".level"));
            ItemRarity rarity = ItemRarity.fromString(itemsConfig.getString(path + ".rarity"));
            
            int efficiency = itemsConfig.getInt(path + ".stats.efficiency", 0);
            int fortune = itemsConfig.getInt(path + ".stats.fortune", 0);
            CustomItemStats stats = new CustomItemStats(efficiency, fortune);
            
            CustomItem item = new CustomItem(plugin, id, displayName, material, level, rarity, stats);
            customItems.put(id, item);
        }
        
        plugin.getLogger().info("Loaded " + customItems.size() + " custom items");
    }

    public void saveItems() {
        itemsConfig = new YamlConfiguration();
        
        for (Map.Entry<String, CustomItem> entry : customItems.entrySet()) {
            String id = entry.getKey();
            CustomItem item = entry.getValue();
            String path = "items." + id;
            
            itemsConfig.set(path + ".display_name", item.getDisplayName());
            itemsConfig.set(path + ".material", item.getMaterial().name());
            itemsConfig.set(path + ".level", item.getLevel().name());
            itemsConfig.set(path + ".rarity", item.getRarity().name());
            itemsConfig.set(path + ".stats.efficiency", item.getStats().getEfficiency());
            itemsConfig.set(path + ".stats.fortune", item.getStats().getFortune());
        }
        
        try {
            itemsConfig.save(itemsFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save custom_items.yml");
        }
    }

    public void registerItem(CustomItem item) {
        customItems.put(item.getId(), item);
        saveItems();
    }

    public void unregisterItem(String id) {
        customItems.remove(id);
        saveItems();
    }

    public CustomItem getItem(String id) {
        return customItems.get(id);
    }

    public Set<String> getItemIds() {
        return customItems.keySet();
    }

    public Map<String, CustomItem> getAllItems() {
        return new HashMap<>(customItems);
    }
}
