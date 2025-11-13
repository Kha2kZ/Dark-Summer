
package com.boxpvp.core.items;

import java.util.HashMap;
import java.util.Map;

public class ItemStats {
    private double damage;
    private double attackSpeed;
    private int level;
    private String quality;
    private Map<String, Double> customStats;
    
    public ItemStats() {
        this.damage = 0;
        this.attackSpeed = 10.0;
        this.level = 1;
        this.quality = "Common";
        this.customStats = new HashMap<>();
    }
    
    public double getDamage() {
        return damage;
    }
    
    public void setDamage(double damage) {
        this.damage = damage;
    }
    
    public double getAttackSpeed() {
        return attackSpeed;
    }
    
    public void setAttackSpeed(double attackSpeed) {
        this.attackSpeed = attackSpeed;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    public String getQuality() {
        return quality;
    }
    
    public void setQuality(String quality) {
        this.quality = quality;
    }
    
    public Map<String, Double> getCustomStats() {
        return customStats;
    }
    
    public void addCustomStat(String name, double value) {
        customStats.put(name, value);
    }
    
    public double getCustomStat(String name) {
        return customStats.getOrDefault(name, 0.0);
    }
}
