package com.boxpvp.core.kits;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class StarterKitManager {
    
    private final Plugin plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;
    private final Set<UUID> receivedKit;

    public StarterKitManager(Plugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "starter_kits.yml");
        this.receivedKit = new HashSet<>();
        loadData();
    }

    private void loadData() {
        if (!dataFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create starter_kits.yml");
                return;
            }
        }
        
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        
        if (dataConfig.contains("received")) {
            for (String uuidStr : dataConfig.getStringList("received")) {
                receivedKit.add(UUID.fromString(uuidStr));
            }
        }
    }

    private void saveData() {
        dataConfig = new YamlConfiguration();
        
        java.util.List<String> uuidList = new java.util.ArrayList<>();
        for (UUID uuid : receivedKit) {
            uuidList.add(uuid.toString());
        }
        
        dataConfig.set("received", uuidList);
        
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save starter_kits.yml");
        }
    }

    public boolean hasReceivedKit(Player player) {
        return receivedKit.contains(player.getUniqueId());
    }

    public void giveStarterKit(Player player) {
        if (hasReceivedKit(player)) {
            return;
        }
        
        ItemStack helmet = new ItemStack(Material.IRON_HELMET);
        ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
        ItemStack boots = new ItemStack(Material.IRON_BOOTS);
        
        makeUnbreakable(helmet);
        makeUnbreakable(chestplate);
        makeUnbreakable(leggings);
        makeUnbreakable(boots);
        
        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
        
        ItemStack ironPickaxe = new ItemStack(Material.IRON_PICKAXE);
        ItemStack ironSword = new ItemStack(Material.IRON_SWORD);
        makeUnbreakable(ironPickaxe);
        makeUnbreakable(ironSword);
        
        ItemStack elytra = new ItemStack(Material.ELYTRA);
        makeUnbreakable(elytra);
        
        ItemStack fireworks = new ItemStack(Material.FIREWORK_ROCKET, 32);
        ItemStack goldenCarrots = new ItemStack(Material.GOLDEN_CARROT, 64);
        
        player.getInventory().addItem(ironPickaxe);
        player.getInventory().addItem(ironSword);
        player.getInventory().addItem(elytra);
        player.getInventory().addItem(fireworks);
        player.getInventory().addItem(goldenCarrots);
        
        receivedKit.add(player.getUniqueId());
        saveData();
        
        player.sendMessage("§a§lYou have received the starter kit!");
    }

    private void makeUnbreakable(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
    }
}
