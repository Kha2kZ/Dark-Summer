package com.boxpvp.core.data;

import com.boxpvp.core.BoxPvPCore;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GiftCodeManager {
    
    private final BoxPvPCore plugin;
    private final File giftCodesFile;
    private FileConfiguration giftCodesConfig;
    private final Map<String, GiftCode> giftCodes;
    
    public GiftCodeManager(BoxPvPCore plugin) {
        this.plugin = plugin;
        this.giftCodesFile = new File(plugin.getDataFolder(), "giftcodes.yml");
        this.giftCodes = new HashMap<>();
        
        loadGiftCodes();
    }
    
    private void loadGiftCodes() {
        if (!giftCodesFile.exists()) {
            try {
                giftCodesFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create giftcodes.yml file!");
                e.printStackTrace();
            }
        }
        
        giftCodesConfig = YamlConfiguration.loadConfiguration(giftCodesFile);
        
        ConfigurationSection codesSection = giftCodesConfig.getConfigurationSection("codes");
        if (codesSection != null) {
            for (String code : codesSection.getKeys(false)) {
                try {
                    String path = "codes." + code + ".";
                    
                    Map<Integer, ItemStack> itemRewards = new HashMap<>();
                    ConfigurationSection itemsSection = giftCodesConfig.getConfigurationSection(path + "item-rewards");
                    if (itemsSection != null) {
                        for (String slotStr : itemsSection.getKeys(false)) {
                            try {
                                int slot = Integer.parseInt(slotStr);
                                ItemStack item = itemsSection.getItemStack(slotStr);
                                if (item != null) {
                                    itemRewards.put(slot, item);
                                }
                            } catch (NumberFormatException e) {
                                plugin.getLogger().warning("Invalid slot number in gift code " + code + ": " + slotStr);
                            }
                        }
                    }
                    
                    double moneyReward = giftCodesConfig.getDouble(path + "money-reward", 0);
                    double gemsReward = giftCodesConfig.getDouble(path + "gems-reward", 0);
                    double coinsReward = giftCodesConfig.getDouble(path + "coins-reward", 0);
                    int maxUses = giftCodesConfig.getInt(path + "max-uses", -1);
                    
                    Set<UUID> usedPlayers = new HashSet<>();
                    List<String> usedPlayersList = giftCodesConfig.getStringList(path + "used-players");
                    for (String uuidStr : usedPlayersList) {
                        try {
                            usedPlayers.add(UUID.fromString(uuidStr));
                        } catch (IllegalArgumentException e) {
                            plugin.getLogger().warning("Invalid UUID in gift code " + code + ": " + uuidStr);
                        }
                    }
                    
                    GiftCode giftCode = new GiftCode(code, itemRewards, moneyReward, 
                                                     gemsReward, coinsReward, maxUses, usedPlayers);
                    giftCodes.put(code.toUpperCase(), giftCode);
                    
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load gift code: " + code);
                    e.printStackTrace();
                }
            }
        }
        
        plugin.getLogger().info("Loaded " + giftCodes.size() + " gift codes");
    }
    
    public void saveGiftCodes() {
        giftCodesConfig = new YamlConfiguration();
        
        for (GiftCode giftCode : giftCodes.values()) {
            String path = "codes." + giftCode.getCode() + ".";
            
            Map<Integer, ItemStack> itemRewardsMap = giftCode.getItemRewardsMap();
            for (Map.Entry<Integer, ItemStack> entry : itemRewardsMap.entrySet()) {
                giftCodesConfig.set(path + "item-rewards." + entry.getKey(), entry.getValue());
            }
            
            giftCodesConfig.set(path + "money-reward", giftCode.getMoneyReward());
            giftCodesConfig.set(path + "gems-reward", giftCode.getGemsReward());
            giftCodesConfig.set(path + "coins-reward", giftCode.getCoinsReward());
            giftCodesConfig.set(path + "max-uses", giftCode.getMaxUses());
            
            List<String> usedPlayersList = new ArrayList<>();
            for (UUID uuid : giftCode.getUsedPlayers()) {
                usedPlayersList.add(uuid.toString());
            }
            giftCodesConfig.set(path + "used-players", usedPlayersList);
        }
        
        try {
            giftCodesConfig.save(giftCodesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save gift codes!");
            e.printStackTrace();
        }
    }
    
    public void addGiftCode(GiftCode giftCode) {
        giftCodes.put(giftCode.getCode().toUpperCase(), giftCode);
        saveGiftCodes();
    }
    
    public GiftCode getGiftCode(String code) {
        return giftCodes.get(code.toUpperCase());
    }
    
    public boolean giftCodeExists(String code) {
        return giftCodes.containsKey(code.toUpperCase());
    }
    
    public void removeGiftCode(String code) {
        giftCodes.remove(code.toUpperCase());
        saveGiftCodes();
    }
    
    public Collection<GiftCode> getAllGiftCodes() {
        return new ArrayList<>(giftCodes.values());
    }
    
    public void saveAll() {
        saveGiftCodes();
    }
}
