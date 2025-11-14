package com.boxpvp.core.items;

public class CustomItemStats {
    
    private int efficiency;
    private int fortune;
    private double damage;
    private double attackSpeed;

    public CustomItemStats() {
        this(0, 0, 0, 0);
    }

    public CustomItemStats(int efficiency, int fortune) {
        this(efficiency, fortune, 0, 0);
    }

    public CustomItemStats(int efficiency, int fortune, double damage, double attackSpeed) {
        this.efficiency = efficiency;
        this.fortune = fortune;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
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

    public boolean hasEfficiency() {
        return efficiency > 0;
    }

    public boolean hasFortune() {
        return fortune > 0;
    }

    public boolean hasDamage() {
        return damage > 0;
    }

    public boolean hasAttackSpeed() {
        return attackSpeed > 0;
    }

    public String toNBTString() {
        return efficiency + ":" + fortune + ":" + damage + ":" + attackSpeed;
    }

    public static CustomItemStats fromNBTString(String nbt) {
        try {
            String[] parts = nbt.split(":");
            int eff = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
            int fort = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            double dmg = parts.length > 2 ? Double.parseDouble(parts[2]) : 0;
            double spd = parts.length > 3 ? Double.parseDouble(parts[3]) : 0;
            return new CustomItemStats(eff, fort, dmg, spd);
        } catch (Exception e) {
            return new CustomItemStats();
        }
    }
}
