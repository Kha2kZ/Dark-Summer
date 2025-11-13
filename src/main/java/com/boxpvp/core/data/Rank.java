package com.boxpvp.core.data;

import org.bukkit.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class ItemStats {
    private final int id;
    private final String name;
    private final String lore;
    private final int rarity;
    private final int level;
    private final int damage;
    private final int defense;
    private final int speed;
    private final int energy;
    private final int intelligence;
    private final int strength;
    private final int critChance;
    private final int critDamage;
    private final int armor;
    private final int armorToughness;
    private final int maxHealth;
    private final int maxDefense;
    private final int regeneration;
    private final int maxEnergy;
    private final int maxIntelligence;
    private final int maxStrength;
    private final int maxCritChance;
    private final int maxCritDamage;
    private final int maxArmor;
    private final int maxArmorToughness;
    private final int maxRegeneration;

    public ItemStats(int id, String name, String lore, int rarity, int level, int damage, int defense, int speed, int energy, int intelligence, int strength, int critChance, int critDamage, int armor, int armorToughness, int maxHealth, int maxDefense, int regeneration, int maxEnergy, int maxIntelligence, int maxStrength, int maxCritChance, int maxCritDamage, int maxArmor, int maxArmorToughness, int maxRegeneration) {
        this.id = id;
        this.name = name;
        this.lore = lore;
        this.rarity = rarity;
        this.level = level;
        this.damage = damage;
        this.defense = defense;
        this.speed = speed;
        this.energy = energy;
        this.intelligence = intelligence;
        this.strength = strength;
        this.critChance = critChance;
        this.critDamage = critDamage;
        this.armor = armor;
        this.armorToughness = armorToughness;
        this.maxHealth = maxHealth;
        this.maxDefense = maxDefense;
        this.regeneration = regeneration;
        this.maxEnergy = maxEnergy;
        this.maxIntelligence = maxIntelligence;
        this.maxStrength = maxStrength;
        this.maxCritChance = maxCritChance;
        this.maxCritDamage = maxCritDamage;
        this.maxArmor = maxArmor;
        this.maxArmorToughness = maxArmorToughness;
        this.maxRegeneration = maxRegeneration;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getLore() { return lore; }
    public int getRarity() { return rarity; }
    public int getLevel() { return level; }
    public int getDamage() { return damage; }
    public int getDefense() { return defense; }
    public int getSpeed() { return speed; }
    public int getEnergy() { return energy; }
    public int getIntelligence() { return intelligence; }
    public int getStrength() { return strength; }
    public int getCritChance() { return critChance; }
    public int getCritDamage() { return critDamage; }
    public int getArmor() { return armor; }
    public int getArmorToughness() { return armorToughness; }
    public int getMaxHealth() { return maxHealth; }
    public int getMaxDefense() { return maxDefense; }
    public int getRegeneration() { return regeneration; }
    public int getMaxEnergy() { return maxEnergy; }
    public int getMaxIntelligence() { return maxIntelligence; }
    public int getMaxStrength() { return maxStrength; }
    public int getMaxCritChance() { return maxCritChance; }
    public int getMaxCritDamage() { return maxCritDamage; }
    public int getMaxArmor() { return maxArmor; }
    public int getMaxArmorToughness() { return maxArmorToughness; }
    public int getMaxRegeneration() { return maxRegeneration; }
}

class ItemLevel {
    private final int level;
    private final String name;
    private final ItemStats stats;

    public ItemLevel(int level, String name, ItemStats stats) {
        this.level = level;
        this.name = name;
        this.stats = stats;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public ItemStats getStats() {
        return stats;
    }

    public String getDisplay() {
        return name + " (Level " + level + ")";
    }
}

class ItemRarity {
    private final int id;
    private final String name;
    private final String color;
    private final ItemLevel[] itemLevels;

    public ItemRarity(int id, String name, String color, ItemLevel[] itemLevels) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.itemLevels = itemLevels;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public ItemLevel[] getItemLevels() {
        return itemLevels;
    }

    public String getDisplay() {
        return color + name;
    }
}

class PlayerData {
    private String name;
    private int level;
    private int exp;
    private int money;
    private int gems;
    private int kills;
    private int deaths;
    private Map<Integer, Integer> items;
    private Rank rank;

    public PlayerData(String name, int level, int exp, int money, int gems, int kills, int deaths, Rank rank) {
        this.name = name;
        this.level = level;
        this.exp = exp;
        this.money = money;
        this.gems = gems;
        this.kills = kills;
        this.deaths = deaths;
        this.items = new HashMap<>();
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getMoney() {
        return money;
    }

    public int getGems() {
        return gems;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public Rank getRank() {
        return rank;
    }

    public void addMoney(int amount) {
        this.money += amount;
    }

    public void removeMoney(int amount) {
        this.money -= amount;
    }

    public void addGems(int amount) {
        this.gems += amount;
    }

    public void removeGems(int amount) {
        this.gems -= amount;
    }

    public void addKills(int amount) {
        this.kills += amount;
    }

    public void addDeaths(int amount) {
        this.deaths += amount;
    }

    public void setItems(Map<Integer, Integer> items) {
        this.items = items;
    }

    public Map<Integer, Integer> getItems() {
        return items;
    }

    public void addItem(int itemId, int quantity) {
        this.items.put(itemId, this.items.getOrDefault(itemId, 0) + quantity);
    }

    public void removeItem(int itemId, int quantity) {
        if (this.items.containsKey(itemId)) {
            int currentQuantity = this.items.get(itemId);
            if (currentQuantity <= quantity) {
                this.items.remove(itemId);
            } else {
                this.items.put(itemId, currentQuantity - quantity);
            }
        }
    }
}


public enum Rank {
    MEMBER(0, "§7", 1, 1.0, 0, -1, Color.fromRGB(128, 128, 128)),
    VIP(20, "§e", 2, 2.0, 2, 120, Color.fromRGB(255, 255, 0)),
    VIP_PLUS(50, "§a", 3, 3.0, 3, 120, Color.fromRGB(85, 255, 85)),
    MVP(100, "§b", 5, 5.0, 5, 120, Color.fromRGB(85, 255, 255)),
    MVP_PLUS(200, "§3", 7, 7.0, 7, 120, Color.fromRGB(0, 170, 170)),
    RICHKID(300, "§d", 10, 15.0, 10, 60, Color.fromRGB(255, 85, 255)),
    DARK(400, "§1§9", 20, 20.0, 15, 30, Color.fromRGB(0, 0, 170)),
    HEAVEN(500, "§f§e", 35, 25.0, 20, 20, Color.fromRGB(255, 215, 0)),
    INFINITE(700, "§c§9", 40, 30.0, 30, 10, Color.fromRGB(255, 0, 0)),
    EXTREME(850, "§c§6", 40, 50.0, 30, 5, Color.fromRGB(255, 170, 0)),
    CUSTOM(1000, "§c", 40, 70.0, 30, 1, Color.fromRGB(255, 0, 0)),
    HELPER(-1, "§d", 40, 70.0, 30, 1, Color.fromRGB(255, 192, 203)),
    POLICE(-1, "§c§5", 40, 70.0, 30, 1, Color.fromRGB(170, 0, 170)),
    ADMIN(-1, "§4§b", 40, 100.0, 100, 0, Color.fromRGB(255, 255, 255)),
    OWNER(-1, "§4§c§6§e", 40, 1000.0, 1000, 0, Color.fromRGB(255, 215, 0));

    private final double price;
    private final String colorCode;
    private final int maxVaults;
    private final double miningMultiplier;
    private final int maxAuctionListings;
    private final int autoCraftCooldown;
    private final Color armorColor;

    Rank(double price, String colorCode, int maxVaults, double miningMultiplier,
         int maxAuctionListings, int autoCraftCooldown, Color armorColor) {
        this.price = price;
        this.colorCode = colorCode;
        this.maxVaults = maxVaults;
        this.miningMultiplier = miningMultiplier;
        this.maxAuctionListings = maxAuctionListings;
        this.autoCraftCooldown = autoCraftCooldown;
        this.armorColor = armorColor;
    }

    public double getPrice() {
        return price;
    }

    public String getColorCode() {
        return colorCode;
    }

    public int getMaxVaults() {
        return maxVaults;
    }

    public double getMiningMultiplier() {
        return miningMultiplier;
    }

    public int getMaxAuctionListings() {
        return maxAuctionListings;
    }

    public int getAutoCraftCooldown() {
        return autoCraftCooldown;
    }

    public Color getArmorColor() {
        return armorColor;
    }

    public boolean isPurchasable() {
        return price >= 0;
    }

    public boolean isStaffRank() {
        return this == HELPER || this == POLICE || this == ADMIN || this == OWNER;
    }

    public boolean canUseNick() {
        return this.ordinal() >= MVP.ordinal();
    }

    public boolean canUseRGBNick() {
        return this.ordinal() >= RICHKID.ordinal();
    }

    public boolean canUseFeed() {
        return this.ordinal() >= MVP.ordinal();
    }

    public boolean canUseHeal() {
        return this.ordinal() >= MVP_PLUS.ordinal();
    }

    public boolean canUseFly() {
        return this.ordinal() >= DARK.ordinal();
    }

    public boolean canUseInvsee() {
        return this.ordinal() >= VIP.ordinal();
    }

    public boolean canUseAutoCraft() {
        return this.ordinal() >= MVP_PLUS.ordinal();
    }

    public boolean hasKillBonus() {
        return this.ordinal() >= INFINITE.ordinal() && this.ordinal() <= CUSTOM.ordinal();
    }

    public boolean isImmuneToDeathPenalty() {
        return this.ordinal() >= EXTREME.ordinal();
    }

    public double getKillMoneyBonus() {
        if (this == INFINITE || this == EXTREME || this == HEAVEN) {
            return 10000;
        } else if (this == CUSTOM) {
            return 50000;
        }
        return 0;
    }

    public double getKillGemsBonus() {
        if (this == INFINITE || this == EXTREME || this == HEAVEN) {
            return 10;
        } else if (this == CUSTOM) {
            return 50;
        }
        return 0;
    }

    public double getDeathMoneyPenalty() {
        if (isImmuneToDeathPenalty()) {
            return 0;
        }
        if (this == INFINITE) {
            return 5000;
        }
        return 0;
    }

    public double getDeathGemsPenalty() {
        if (isImmuneToDeathPenalty()) {
            return 0;
        }
        if (this == INFINITE) {
            return 5;
        }
        return 0;
    }

    public String getDisplayName() {
        return colorCode + name().replace("_", "+");
    }

    // This method was added to fulfill the user's request to get the color code.
    public String getColor() {
        return colorCode;
    }

    public Rank getNextRank() {
        if (!isPurchasable() || this == CUSTOM) {
            return null;
        }
        int nextOrdinal = this.ordinal() + 1;
        if (nextOrdinal >= HELPER.ordinal()) {
            return null;
        }
        return values()[nextOrdinal];
    }

    public static Rank fromString(String name) {
        try {
            return valueOf(name.toUpperCase().replace("+", "_PLUS"));
        } catch (IllegalArgumentException e) {
            return MEMBER;
        }
    }
}