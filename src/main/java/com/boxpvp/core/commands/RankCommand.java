package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.PlayerData;
import com.boxpvp.core.data.Rank;
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
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;

public class RankCommand implements CommandExecutor, Listener {
    
    private final BoxPvPCore plugin;
    private static final String GUI_TITLE = "§6§lRank Shop";
    
    public RankCommand(BoxPvPCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        openRankGUI(player);
        
        return true;
    }
    
    private void openRankGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, GUI_TITLE);
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        Rank currentRank = data.getRank();
        
        for (int i = 0; i < 9; i++) {
            ItemStack glassPane = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
            ItemMeta glassMeta = glassPane.getItemMeta();
            glassMeta.setDisplayName(" ");
            glassPane.setItemMeta(glassMeta);
            gui.setItem(i, glassPane);
        }
        
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        org.bukkit.inventory.meta.SkullMeta skullMeta = (org.bukkit.inventory.meta.SkullMeta) playerHead.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skullMeta.setDisplayName("§e§l" + player.getName());
        List<String> headLore = new ArrayList<>();
        headLore.add("§7Current Rank: " + currentRank.getDisplayName());
        headLore.add("§7Coins: §6" + String.format("%.0f", data.getCoins()));
        skullMeta.setLore(headLore);
        playerHead.setItemMeta(skullMeta);
        gui.setItem(4, playerHead);
        
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29};
        int index = 0;
        
        for (Rank rank : Rank.values()) {
            if (!rank.isPurchasable() || index >= slots.length) {
                continue;
            }
            
            ItemStack item = createRankItem(rank, currentRank, data.getCoins());
            gui.setItem(slots[index], item);
            index++;
        }
        
        player.openInventory(gui);
    }
    
    private ItemStack createRankItem(Rank rank, Rank currentRank, double playerCoins) {
        ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        
        meta.setColor(rank.getArmorColor());
        meta.setDisplayName(rank.getDisplayName());
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Giá: §e" + String.format("%.0f", rank.getPrice()) + " Coins");
        lore.add("§7Quyền lợi:");
        lore.add("§7- " + rank.getMaxVaults() + " Virtual Chests");
        lore.add("§7- Mining x" + String.format("%.0f", rank.getMiningMultiplier()));
        lore.add("§7- " + rank.getMaxAuctionListings() + " Auction Listings");
        
        if (rank.canUseNick()) {
            lore.add("§7- /nick command");
        }
        if (rank.canUseFeed()) {
            lore.add("§7- /feed command");
        }
        if (rank.canUseHeal()) {
            lore.add("§7- /heal command");
        }
        if (rank.canUseFly()) {
            lore.add("§7- /fly command");
        }
        if (rank.canUseAutoCraft()) {
            lore.add("§7- /tuchetao (" + rank.getAutoCraftCooldown() + "s cooldown)");
        }
        
        lore.add("");
        
        if (currentRank.ordinal() >= rank.ordinal()) {
            lore.add("§aĐã sở hữu: §l✓");
        } else if (currentRank.ordinal() + 1 == rank.ordinal()) {
            if (playerCoins >= rank.getPrice()) {
                lore.add("§aĐã sở hữu: §c§l✗");
                lore.add("");
                lore.add("§e§lClick để mua!");
            } else {
                lore.add("§aĐã sở hữu: §c§l✗");
                lore.add("");
                lore.add("§cThiếu " + String.format("%.0f", (rank.getPrice() - playerCoins)) + " coins");
            }
        } else {
            lore.add("§aĐã sở hữu: §c§l✗");
            lore.add("");
            lore.add("§cMua rank trước đó!");
        }
        
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_DYE);
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(GUI_TITLE)) {
            return;
        }
        
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        
        if (clicked == null || clicked.getType() != Material.LEATHER_CHESTPLATE) {
            return;
        }
        
        ItemMeta meta = clicked.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }
        
        String displayName = meta.getDisplayName();
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        
        for (Rank rank : Rank.values()) {
            if (rank.getDisplayName().equals(displayName)) {
                if (plugin.getRankManager().purchaseRank(player, rank)) {
                    player.sendMessage(prefix + "§aYou purchased the " + rank.getDisplayName() + " §arank!");
                    player.closeInventory();
                    
                    Bukkit.getScheduler().runTaskLater(plugin, () -> openRankGUI(player), 1L);
                } else {
                    PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
                    if (data.getRank().ordinal() >= rank.ordinal()) {
                        player.sendMessage(prefix + "§cYou already have this rank or higher!");
                    } else if (data.getRank().ordinal() + 1 != rank.ordinal()) {
                        player.sendMessage(prefix + "§cYou must purchase the previous rank first!");
                    } else {
                        player.sendMessage(prefix + "§cYou don't have enough coins!");
                    }
                }
                break;
            }
        }
    }
}
