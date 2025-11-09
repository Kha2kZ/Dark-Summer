package com.boxpvp.core.trade;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TradeRecipe {
    
    private final String id;
    private final List<ItemStack> inputItems;
    private final ItemStack outputItem;

    public TradeRecipe(String id, List<ItemStack> inputItems, ItemStack outputItem) {
        this.id = id;
        this.inputItems = new ArrayList<>(inputItems);
        this.outputItem = outputItem.clone();
    }

    public String getId() {
        return id;
    }

    public List<ItemStack> getInputItems() {
        return new ArrayList<>(inputItems);
    }

    public ItemStack getOutputItem() {
        return outputItem.clone();
    }

    public boolean hasRequiredItems(List<ItemStack> playerItems) {
        List<ItemStack> remaining = new ArrayList<>();
        for (ItemStack item : playerItems) {
            if (item != null && !item.getType().isAir()) {
                remaining.add(item.clone());
            }
        }
        
        for (ItemStack required : inputItems) {
            boolean found = false;
            
            for (int i = 0; i < remaining.size(); i++) {
                ItemStack check = remaining.get(i);
                
                if (check.isSimilar(required) && check.getAmount() >= required.getAmount()) {
                    check.setAmount(check.getAmount() - required.getAmount());
                    
                    if (check.getAmount() <= 0) {
                        remaining.remove(i);
                    }
                    
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                return false;
            }
        }
        
        return true;
    }
}
