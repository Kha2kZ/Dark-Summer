package com.boxpvp.core.data;

import com.boxpvp.core.BoxPvPCore;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AutoCraftingManager {
    
    private final BoxPvPCore plugin;
    private final File recipesFile;
    private FileConfiguration recipesConfig;
    private final Map<UUID, AutoCraftRecipe> recipes;
    private final Map<UUID, Long> playerCooldowns;
    
    public AutoCraftingManager(BoxPvPCore plugin) {
        this.plugin = plugin;
        this.recipesFile = new File(plugin.getDataFolder(), "autocraft_recipes.yml");
        this.recipes = new HashMap<>();
        this.playerCooldowns = new HashMap<>();
        
        loadRecipes();
    }
    
    private void loadRecipes() {
        if (!recipesFile.exists()) {
            try {
                recipesFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create autocraft_recipes.yml file!");
                e.printStackTrace();
            }
        }
        
        recipesConfig = YamlConfiguration.loadConfiguration(recipesFile);
        
        ConfigurationSection recipesSection = recipesConfig.getConfigurationSection("recipes");
        if (recipesSection != null) {
            for (String recipeIdStr : recipesSection.getKeys(false)) {
                try {
                    UUID recipeId = UUID.fromString(recipeIdStr);
                    String path = "recipes." + recipeIdStr + ".";
                    
                    List<ItemStack> inputItems = (List<ItemStack>) recipesConfig.getList(path + "input");
                    List<ItemStack> outputItems = (List<ItemStack>) recipesConfig.getList(path + "output");
                    String creatorName = recipesConfig.getString(path + "creator");
                    long createDate = recipesConfig.getLong(path + "create-date");
                    
                    if (inputItems != null && outputItems != null) {
                        AutoCraftRecipe recipe = new AutoCraftRecipe(recipeId, inputItems, outputItems, creatorName, createDate);
                        recipes.put(recipeId, recipe);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load auto-craft recipe: " + recipeIdStr);
                }
            }
        }
        
        plugin.getLogger().info("Loaded " + recipes.size() + " auto-craft recipes");
    }
    
    public void addRecipe(AutoCraftRecipe recipe) {
        recipes.put(recipe.getRecipeId(), recipe);
        saveRecipe(recipe);
    }
    
    public void removeRecipe(UUID recipeId) {
        recipes.remove(recipeId);
        recipesConfig.set("recipes." + recipeId.toString(), null);
        saveRecipes();
    }
    
    public AutoCraftRecipe getRecipe(UUID recipeId) {
        return recipes.get(recipeId);
    }
    
    public List<AutoCraftRecipe> getAllRecipes() {
        return new ArrayList<>(recipes.values());
    }
    
    public boolean canCraft(Player player) {
        Rank rank = plugin.getRankManager().getRank(player);
        
        if (!rank.canUseAutoCraft()) {
            return false;
        }
        
        long cooldown = rank.getAutoCraftCooldown();
        if (cooldown <= 0) {
            return true;
        }
        
        Long lastUse = playerCooldowns.get(player.getUniqueId());
        if (lastUse == null) {
            return true;
        }
        
        long timeSince = (System.currentTimeMillis() - lastUse) / 1000;
        return timeSince >= cooldown;
    }
    
    public long getRemainingCooldown(Player player) {
        Rank rank = plugin.getRankManager().getRank(player);
        long cooldown = rank.getAutoCraftCooldown();
        
        if (cooldown <= 0) {
            return 0;
        }
        
        Long lastUse = playerCooldowns.get(player.getUniqueId());
        if (lastUse == null) {
            return 0;
        }
        
        long timeSince = (System.currentTimeMillis() - lastUse) / 1000;
        return Math.max(0, cooldown - timeSince);
    }
    
    public void setCooldown(Player player) {
        playerCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
    
    public AutoCraftRecipe findMatchingRecipe(Player player) {
        ItemStack[] inventory = player.getInventory().getContents();
        Map<ItemStack, Integer> playerItems = new HashMap<>();
        
        for (ItemStack item : inventory) {
            if (item != null && item.getType() != org.bukkit.Material.AIR) {
                boolean found = false;
                for (ItemStack existing : playerItems.keySet()) {
                    if (existing.isSimilar(item)) {
                        playerItems.put(existing, playerItems.get(existing) + item.getAmount());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    playerItems.put(item.clone(), item.getAmount());
                }
            }
        }
        
        for (AutoCraftRecipe recipe : recipes.values()) {
            if (hasRequiredItems(playerItems, recipe.getInputItems())) {
                return recipe;
            }
        }
        
        return null;
    }
    
    private boolean hasRequiredItems(Map<ItemStack, Integer> playerItems, List<ItemStack> requiredItems) {
        for (ItemStack required : requiredItems) {
            boolean hasItem = false;
            for (Map.Entry<ItemStack, Integer> entry : playerItems.entrySet()) {
                if (entry.getKey().isSimilar(required) && entry.getValue() >= required.getAmount()) {
                    hasItem = true;
                    break;
                }
            }
            if (!hasItem) {
                return false;
            }
        }
        return true;
    }
    
    public void craftRecipe(Player player, AutoCraftRecipe recipe) {
        for (ItemStack required : recipe.getInputItems()) {
            int amountToRemove = required.getAmount();
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.isSimilar(required)) {
                    if (item.getAmount() >= amountToRemove) {
                        item.setAmount(item.getAmount() - amountToRemove);
                        break;
                    } else {
                        amountToRemove -= item.getAmount();
                        item.setAmount(0);
                    }
                }
            }
        }
        
        for (ItemStack output : recipe.getOutputItems()) {
            player.getInventory().addItem(output.clone());
        }
        
        setCooldown(player);
    }
    
    private void saveRecipe(AutoCraftRecipe recipe) {
        String path = "recipes." + recipe.getRecipeId().toString() + ".";
        recipesConfig.set(path + "input", recipe.getInputItems());
        recipesConfig.set(path + "output", recipe.getOutputItems());
        recipesConfig.set(path + "creator", recipe.getCreatorName());
        recipesConfig.set(path + "create-date", recipe.getCreateDate());
        
        saveRecipes();
    }
    
    private void saveRecipes() {
        try {
            recipesConfig.save(recipesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save auto-craft recipes!");
            e.printStackTrace();
        }
    }
    
    public void saveAll() {
        saveRecipes();
    }
}
