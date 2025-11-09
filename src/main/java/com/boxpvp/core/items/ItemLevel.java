package com.boxpvp.core.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public enum ItemLevel {
    I(1, TextColor.fromHexString("#808080")),
    II(2, TextColor.fromHexString("#909090")),
    III(3, TextColor.fromHexString("#A0A0A0")),
    IV(4, TextColor.fromHexString("#B0B0B0")),
    V(5, TextColor.fromHexString("#C0C0C0")),
    VI(6, TextColor.fromHexString("#D0D0D0")),
    VII(7, TextColor.fromHexString("#FFE4B5")),
    VIII(8, TextColor.fromHexString("#FFD700")),
    IX(9, TextColor.fromHexString("#FFA500")),
    X(10, TextColor.fromHexString("#FF8C00")),
    XI(11, TextColor.fromHexString("#FF7F50")),
    XII(12, TextColor.fromHexString("#FF6347")),
    XIII(13, TextColor.fromHexString("#FF4500")),
    XIV(14, TextColor.fromHexString("#FF2400")),
    XV(15, TextColor.fromHexString("#FF0000"));

    private final int level;
    private final TextColor color;

    ItemLevel(int level, TextColor color) {
        this.level = level;
        this.color = color;
    }

    public int getLevel() {
        return level;
    }

    public TextColor getColor() {
        return color;
    }

    public String getRomanNumeral() {
        return name();
    }

    public Component getColoredComponent() {
        return Component.text(name()).color(color);
    }

    public static ItemLevel fromLevel(int level) {
        for (ItemLevel itemLevel : values()) {
            if (itemLevel.level == level) {
                return itemLevel;
            }
        }
        return I;
    }

    public static ItemLevel fromString(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return I;
        }
    }
}
