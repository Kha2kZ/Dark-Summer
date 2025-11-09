package com.boxpvp.core.items;

public class CustomItemStats {
    
    private int efficiency;
    private int fortune;

    public CustomItemStats() {
        this(0, 0);
    }

    public CustomItemStats(int efficiency, int fortune) {
        this.efficiency = efficiency;
        this.fortune = fortune;
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

    public boolean hasEfficiency() {
        return efficiency > 0;
    }

    public boolean hasFortune() {
        return fortune > 0;
    }

    public String toNBTString() {
        return efficiency + ":" + fortune;
    }

    public static CustomItemStats fromNBTString(String nbt) {
        try {
            String[] parts = nbt.split(":");
            int eff = Integer.parseInt(parts[0]);
            int fort = Integer.parseInt(parts[1]);
            return new CustomItemStats(eff, fort);
        } catch (Exception e) {
            return new CustomItemStats();
        }
    }
}
