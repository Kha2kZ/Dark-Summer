package com.boxpvp.core.items;

import java.util.HashMap;
import java.util.Map;

public class ItemStats {
    private double damage;
    private double attackSpeed;
    private double critChance;
    private double critDamage;
    private double lifeSteal;
    private int efficiency;
    private int fortune;
    private int level;
    private String quality;
    private Map<String, Double> customStats;

    public ItemStats() {
        this.damage = 0;
        this.attackSpeed = 10.0;
        this.critChance = 0;
        this.critDamage = 150;
        this.lifeSteal = 0;
        this.efficiency = 0;
        this.fortune = 0;
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

    public double getCritChance() {
        return critChance;
    }

    public void setCritChance(double critChance) {
        this.critChance = critChance;
    }

    public double getCritDamage() {
        return critDamage;
    }

    public void setCritDamage(double critDamage) {
        this.critDamage = critDamage;
    }

    public double getLifeSteal() {
        return lifeSteal;
    }

    public void setLifeSteal(double lifeSteal) {
        this.lifeSteal = lifeSteal;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(int efficiency) {
        this.efficiency = efficiency;
    }

    public int getFortune() {
        return fortune;
    }

    public void setFortune(int fortune) {
        this.fortune = fortune;
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