package com.boxpvp.core.data;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class AutoCraftRecipe {
    
    private final UUID recipeId;
    private final List<ItemStack> inputItems;
    private final List<ItemStack> outputItems;
    private final String creatorName;
    private final long createDate;
    
    public AutoCraftRecipe(UUID recipeId, List<ItemStack> inputItems, List<ItemStack> outputItems, String creatorName, long createDate) {
        this.recipeId = recipeId;
        this.inputItems = inputItems;
        this.outputItems = outputItems;
        this.creatorName = creatorName;
        this.createDate = createDate;
    }
    
    public AutoCraftRecipe(List<ItemStack> inputItems, List<ItemStack> outputItems, String creatorName) {
        this(UUID.randomUUID(), inputItems, outputItems, creatorName, System.currentTimeMillis());
    }
    
    public UUID getRecipeId() {
        return recipeId;
    }
    
    public List<ItemStack> getInputItems() {
        return inputItems;
    }
    
    public List<ItemStack> getOutputItems() {
        return outputItems;
    }
    
    public String getCreatorName() {
        return creatorName;
    }
    
    public long getCreateDate() {
        return createDate;
    }
}
