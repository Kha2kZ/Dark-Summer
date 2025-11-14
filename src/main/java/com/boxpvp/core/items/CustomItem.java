package com.boxpvp.core.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class CustomItem {
    
    private String id;
    private String displayName;
    private Material material;
    private ItemLevel level;
    private ItemRarity rarity;
    private CustomItemStats stats;
    private final Plugin plugin;

    public CustomItem(Plugin plugin, String id, String displayName, Material material, 
                      ItemLevel level, ItemRarity rarity, CustomItemStats stats) {
        this.plugin = plugin;
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.level = level;
        this.rarity = rarity;
        this.stats = stats;
    }

    public ItemStack build() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        Component nameComponent = LegacyComponentSerializer.legacySection().deserialize(displayName);
        meta.displayName(nameComponent.decoration(TextDecoration.ITALIC, false));
        
        List<Component> lore = new ArrayList<>();
        
        if (stats.hasDamage()) {
            lore.add(Component.text("Damage: ", net.kyori.adventure.text.format.NamedTextColor.GRAY)
                .append(Component.text("+" + stats.getDamage(), net.kyori.adventure.text.format.NamedTextColor.RED))
                .decoration(TextDecoration.ITALIC, false));
        }
        
        if (stats.hasAttackSpeed()) {
            lore.add(Component.text("Attack Speed: ", net.kyori.adventure.text.format.NamedTextColor.GRAY)
                .append(Component.text(stats.getAttackSpeed(), net.kyori.adventure.text.format.NamedTextColor.GOLD))
                .decoration(TextDecoration.ITALIC, false));
        }
        
        if (stats.hasEfficiency()) {
            lore.add(Component.text("⛏ Efficiency " + stats.getEfficiency(), net.kyori.adventure.text.format.NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false));
        }
        
        if (stats.hasFortune()) {
            lore.add(Component.text("💎 Fortune " + stats.getFortune(), net.kyori.adventure.text.format.NamedTextColor.GREEN)
                .decoration(TextDecoration.ITALIC, false));
        }
        
        lore.add(Component.text(""));
        lore.add(level.getColoredComponent().decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        lore.add(Component.text("PHẨM CHẤT ", net.kyori.adventure.text.format.NamedTextColor.GRAY)
            .append(rarity.getColoredComponent().decoration(TextDecoration.BOLD, true))
            .decoration(TextDecoration.ITALIC, false));
        
        meta.lore(lore);
        
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        
        NamespacedKey idKey = new NamespacedKey(plugin, "custom_item_id");
        NamespacedKey levelKey = new NamespacedKey(plugin, "custom_item_level");
        NamespacedKey rarityKey = new NamespacedKey(plugin, "custom_item_rarity");
        NamespacedKey statsKey = new NamespacedKey(plugin, "custom_item_stats");
        
        meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, id);
        meta.getPersistentDataContainer().set(levelKey, PersistentDataType.STRING, level.name());
        meta.getPersistentDataContainer().set(rarityKey, PersistentDataType.STRING, rarity.name());
        meta.getPersistentDataContainer().set(statsKey, PersistentDataType.STRING, stats.toNBTString());
        
        item.setItemMeta(meta);
        
        return item;
    }

    public static boolean isCustomItem(Plugin plugin, ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        NamespacedKey idKey = new NamespacedKey(plugin, "custom_item_id");
        return item.getItemMeta().getPersistentDataContainer().has(idKey, PersistentDataType.STRING);
    }

    public static CustomItem fromItemStack(Plugin plugin, ItemStack item) {
        if (!isCustomItem(plugin, item)) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        
        NamespacedKey idKey = new NamespacedKey(plugin, "custom_item_id");
        NamespacedKey levelKey = new NamespacedKey(plugin, "custom_item_level");
        NamespacedKey rarityKey = new NamespacedKey(plugin, "custom_item_rarity");
        NamespacedKey statsKey = new NamespacedKey(plugin, "custom_item_stats");
        
        String id = meta.getPersistentDataContainer().get(idKey, PersistentDataType.STRING);
        String levelStr = meta.getPersistentDataContainer().get(levelKey, PersistentDataType.STRING);
        String rarityStr = meta.getPersistentDataContainer().get(rarityKey, PersistentDataType.STRING);
        String statsStr = meta.getPersistentDataContainer().get(statsKey, PersistentDataType.STRING);
        
        ItemLevel level = ItemLevel.fromString(levelStr);
        ItemRarity rarity = ItemRarity.fromString(rarityStr);
        CustomItemStats stats = CustomItemStats.fromNBTString(statsStr);
        
        String displayName = meta.displayName() != null ? 
            ((net.kyori.adventure.text.TextComponent) meta.displayName()).content() : 
            item.getType().name();
        
        return new CustomItem(plugin, id, displayName, item.getType(), level, rarity, stats);
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getMaterial() {
        return material;
    }

    public ItemLevel getLevel() {
        return level;
    }

    public ItemRarity getRarity() {
        return rarity;
    }

    public CustomItemStats getStats() {
        return stats;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setLevel(ItemLevel level) {
        this.level = level;
    }

    public void setRarity(ItemRarity rarity) {
        this.rarity = rarity;
    }

    public void setStats(CustomItemStats stats) {
        this.stats = stats;
    }
}
