package com.boxpvp.core.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public enum ItemRarity {
    COMMON("Common", TextColor.fromHexString("#CCCCCC")),
    RARE("Rare", TextColor.fromHexString("#5555FF")),
    EPIC("Epic", TextColor.fromHexString("#AA00AA")),
    LEGENDARY("Legendary", TextColor.fromHexString("#FFAA00")),
    MYTHIC("Mythic", TextColor.fromHexString("#FF55FF")),
    A("A", TextColor.fromHexString("#00FFFF")),
    S("S", TextColor.fromHexString("#FFD700")),
    SS("SS", TextColor.fromHexString("#FF6347")),
    SSR("SSR", TextColor.fromHexString("#FF1493"));

    private final String displayName;
    private final TextColor color;

    ItemRarity(String displayName, TextColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public TextColor getColor() {
        return color;
    }

    public Component getColoredComponent() {
        return Component.text(displayName).color(color);
    }

    public static ItemRarity fromString(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return COMMON;
        }
    }
}