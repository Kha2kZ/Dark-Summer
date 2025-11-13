package com.boxpvp.core.data;

import org.bukkit.Color;

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