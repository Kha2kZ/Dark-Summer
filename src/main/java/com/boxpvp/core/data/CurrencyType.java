package com.boxpvp.core.data;

public enum CurrencyType {
    MONEY,
    GEMS,
    COINS;
    
    public static CurrencyType fromString(String type) {
        try {
            return valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return MONEY;
        }
    }
    
    public String getDisplayName() {
        switch (this) {
            case MONEY:
                return "Money";
            case GEMS:
                return "Gems";
            case COINS:
                return "Coins";
            default:
                return "Money";
        }
    }
    
    public String getSymbol() {
        switch (this) {
            case MONEY:
                return "$";
            case GEMS:
                return "💎";
            case COINS:
                return "🪙";
            default:
                return "$";
        }
    }
}
