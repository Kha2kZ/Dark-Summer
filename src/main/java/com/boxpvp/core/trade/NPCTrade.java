package com.boxpvp.core.trade;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCTrade {
    
    private final UUID npcId;
    private final String npcName;
    private final Location location;
    private final List<TradeRecipe> recipes;

    public NPCTrade(UUID npcId, String npcName, Location location) {
        this.npcId = npcId;
        this.npcName = npcName;
        this.location = location;
        this.recipes = new ArrayList<>();
    }

    public UUID getNpcId() {
        return npcId;
    }

    public String getNpcName() {
        return npcName;
    }

    public Location getLocation() {
        return location;
    }

    public List<TradeRecipe> getRecipes() {
        return new ArrayList<>(recipes);
    }

    public void addRecipe(TradeRecipe recipe) {
        recipes.add(recipe);
    }

    public void removeRecipe(String recipeId) {
        recipes.removeIf(recipe -> recipe.getId().equals(recipeId));
    }

    public void clearRecipes() {
        recipes.clear();
    }
}
