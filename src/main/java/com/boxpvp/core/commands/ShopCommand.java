package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ShopCommand implements CommandExecutor, Listener {
    
    private final BoxPvPCore plugin;
    private final String shopTitle;
    
    public ShopCommand(BoxPvPCore plugin) {
        this.plugin = plugin;
        this.shopTitle = plugin.getConfig().getString("shop.gui-title", "§6§lBox PvP Shop");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        openShop(player);
        
        return true;
    }
    
    private void openShop(Player player) {
        Inventory shop = Bukkit.createInventory(null, 27, shopTitle);
        
        shop.setItem(10, createShopItem(Material.IRON_SWORD, "§eIron Sword", 100, 
            "§7A basic weapon for combat", "§aPrice: §e$100"));
        shop.setItem(11, createShopItem(Material.DIAMOND_SWORD, "§bDiamond Sword", 500, 
            "§7A powerful weapon", "§aPrice: §e$500"));
        shop.setItem(12, createShopItem(Material.IRON_CHESTPLATE, "§eIron Armor Set", 300, 
            "§7Basic protection", "§aPrice: §e$300"));
        shop.setItem(13, createShopItem(Material.DIAMOND_CHESTPLATE, "§bDiamond Armor Set", 1000, 
            "§7Superior protection", "§aPrice: §e$1000"));
        shop.setItem(14, createShopItem(Material.GOLDEN_APPLE, "§6Golden Apple §7(x5)", 150, 
            "§7Restore health quickly", "§aPrice: §e$150"));
        shop.setItem(15, createShopItem(Material.ENDER_PEARL, "§dEnder Pearl §7(x3)", 200, 
            "§7Quick escape tool", "§aPrice: §e$200"));
        shop.setItem(16, createShopItem(Material.ARROW, "§fArrows §7(x64)", 50, 
            "§7For your bow", "§aPrice: §e$50"));
        
        player.openInventory(shop);
    }
    
    private ItemStack createShopItem(Material material, String name, double price, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equals(shopTitle)) return;
        
        event.setCancelled(true);
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        
        if (clicked == null || clicked.getType() == Material.AIR) return;
        
        String itemName = clicked.getItemMeta().getDisplayName();
        double price = extractPrice(clicked);
        
        if (price == 0) return;
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        if (!data.hasBalance(price)) {
            String prefix = plugin.getConfig().getString("messages.prefix", "");
            String message = plugin.getConfig().getString("messages.insufficient-funds", "§cInsufficient funds!");
            player.sendMessage(prefix + message);
            player.closeInventory();
            return;
        }
        
        data.removeBalance(price);
        
        givePurchasedItems(player, clicked.getType());
        
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        player.sendMessage(prefix + "§aPurchased " + itemName + " §afor §e$" + price + "§a!");
        player.closeInventory();
    }
    
    private double extractPrice(ItemStack item) {
        if (item.getItemMeta() == null || item.getItemMeta().getLore() == null) return 0;
        
        for (String line : item.getItemMeta().getLore()) {
            if (line.contains("Price: $")) {
                String priceStr = line.replace("§aPrice: §e$", "").replace("$", "");
                try {
                    return Double.parseDouble(priceStr);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }
    
    private void givePurchasedItems(Player player, Material type) {
        switch (type) {
            case IRON_CHESTPLATE:
                player.getInventory().addItem(
                    new ItemStack(Material.IRON_HELMET, 1),
                    new ItemStack(Material.IRON_CHESTPLATE, 1),
                    new ItemStack(Material.IRON_LEGGINGS, 1),
                    new ItemStack(Material.IRON_BOOTS, 1)
                );
                break;
            case DIAMOND_CHESTPLATE:
                player.getInventory().addItem(
                    new ItemStack(Material.DIAMOND_HELMET, 1),
                    new ItemStack(Material.DIAMOND_CHESTPLATE, 1),
                    new ItemStack(Material.DIAMOND_LEGGINGS, 1),
                    new ItemStack(Material.DIAMOND_BOOTS, 1)
                );
                break;
            case GOLDEN_APPLE:
                player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 5));
                break;
            case ENDER_PEARL:
                player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 3));
                break;
            case ARROW:
                player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
                break;
            default:
                player.getInventory().addItem(new ItemStack(type, 1));
                break;
        }
    }
}
