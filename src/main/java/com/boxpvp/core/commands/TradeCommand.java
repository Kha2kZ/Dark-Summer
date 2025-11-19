package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.TradeManager;
import com.boxpvp.core.data.TradeSession;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class TradeCommand implements CommandExecutor, Listener {
    
    private final BoxPvPCore plugin;
    private final TradeManager tradeManager;
    private static final String TRADE_GUI_TITLE = "§6§lTrade Window";
    
    public TradeCommand(BoxPvPCore plugin, TradeManager tradeManager) {
        this.plugin = plugin;
        this.tradeManager = tradeManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        
        if (args.length == 0) {
            player.sendMessage(prefix + "§cUsage: /trade <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        
        if (target == null) {
            player.sendMessage(prefix + "§cPlayer not found!");
            return true;
        }
        
        if (target.equals(player)) {
            player.sendMessage(prefix + "§cYou cannot trade with yourself!");
            return true;
        }
        
        if (!player.getWorld().equals(target.getWorld())) {
            player.sendMessage(prefix + "§cPlayer is in a different world!");
            return true;
        }
        
        if (player.getLocation().distance(target.getLocation()) > 10) {
            player.sendMessage(prefix + "§cPlayer is too far away! They must be within 10 blocks.");
            return true;
        }
        
        if (tradeManager.isInTrade(player.getUniqueId())) {
            player.sendMessage(prefix + "§cYou are already in a trade!");
            return true;
        }
        
        if (tradeManager.isInTrade(target.getUniqueId())) {
            player.sendMessage(prefix + "§c" + target.getName() + " is already in a trade!");
            return true;
        }
        
        if (tradeManager.hasPendingRequest(player.getUniqueId())) {
            UUID requester = tradeManager.getPendingRequest(player.getUniqueId());
            if (requester.equals(target.getUniqueId())) {
                tradeManager.removePendingRequest(player.getUniqueId());
                startTrade(player, target);
                return true;
            }
        }
        
        tradeManager.sendTradeRequest(player.getUniqueId(), target.getUniqueId());
        player.sendMessage(prefix + "§aTrade request sent to " + target.getName());
        target.sendMessage(prefix + "§e" + player.getName() + " §awants to trade with you!");
        target.sendMessage(prefix + "§7Type §e/trade " + player.getName() + " §7to accept!");
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (tradeManager.hasPendingRequest(target.getUniqueId()) && 
                tradeManager.getPendingRequest(target.getUniqueId()).equals(player.getUniqueId())) {
                tradeManager.removePendingRequest(target.getUniqueId());
                player.sendMessage(prefix + "§cTrade request to " + target.getName() + " expired.");
            }
        }, 20L * 60);
        
        return true;
    }
    
    private void startTrade(Player player1, Player player2) {
        TradeSession session = tradeManager.createTradeSession(player1.getUniqueId(), player2.getUniqueId());
        
        openTradeGUI(player1, session);
        openTradeGUI(player2, session);
        
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        player1.sendMessage(prefix + "§aTrade started with " + player2.getName());
        player2.sendMessage(prefix + "§aTrade started with " + player1.getName());
    }
    
    private void openTradeGUI(Player player, TradeSession session) {
        Inventory inv = Bukkit.createInventory(null, 54, TRADE_GUI_TITLE);
        
        // Center separator
        ItemStack separator = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta separatorMeta = separator.getItemMeta();
        separatorMeta.setDisplayName("§e§l⬌ Trade Window");
        separatorMeta.setLore(Arrays.asList("§7Your items: Left side", "§7Their items: Right side"));
        separator.setItemMeta(separatorMeta);
        
        for (int i = 4; i <= 49; i += 9) {
            inv.setItem(i, separator);
        }
        
        // Bottom decoration
        ItemStack grayPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta grayMeta = grayPane.getItemMeta();
        grayMeta.setDisplayName(" ");
        grayPane.setItemMeta(grayMeta);
        for (int i = 45; i < 54; i++) {
            if (i != 45 && i != 53) {
                inv.setItem(i, grayPane);
            }
        }
        
        ItemStack confirmButton = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        confirmMeta.setDisplayName("§a§l✔ Confirm Trade");
        confirmMeta.setLore(Arrays.asList("§7Click to confirm the trade", "§7Both players must confirm"));
        confirmButton.setItemMeta(confirmMeta);
        inv.setItem(45, confirmButton);
        
        ItemStack cancelButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cancelMeta = cancelButton.getItemMeta();
        cancelMeta.setDisplayName("§c§l✖ Cancel Trade");
        cancelMeta.setLore(Arrays.asList("§7Click to cancel the trade"));
        cancelButton.setItemMeta(cancelMeta);
        inv.setItem(53, cancelButton);
        
        player.openInventory(inv);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equals(TRADE_GUI_TITLE)) return;
        
        Player player = (Player) event.getWhoClicked();
        TradeSession session = tradeManager.getTradeSession(player.getUniqueId());
        
        if (session == null) {
            event.setCancelled(true);
            return;
        }
        
        int slot = event.getRawSlot();
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        
        if (slot == 45) {
            event.setCancelled(true);
            session.setConfirmed(player.getUniqueId(), true);
            player.sendMessage(prefix + "§aYou confirmed the trade!");
            
            if (session.areBothConfirmed()) {
                completeTrade(session);
            }
            return;
        }
        
        if (slot == 53) {
            event.setCancelled(true);
            cancelTrade(session, player.getName() + " cancelled the trade");
            return;
        }
        
        if (slot % 9 == 4) {
            event.setCancelled(true);
            return;
        }
        
        if (slot >= 0 && slot < 54) {
            if (session.isPlayer1(player.getUniqueId())) {
                if (slot % 9 < 4) {
                    event.setCancelled(false);
                } else {
                    event.setCancelled(true);
                }
            } else {
                if (slot % 9 > 4) {
                    event.setCancelled(false);
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        if (!event.getView().getTitle().equals(TRADE_GUI_TITLE)) return;
        
        Player player = (Player) event.getPlayer();
        TradeSession session = tradeManager.getTradeSession(player.getUniqueId());
        
        if (session != null) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (tradeManager.isInTrade(player.getUniqueId())) {
                    cancelTrade(session, player.getName() + " closed the trade window");
                }
            }, 1L);
        }
    }
    
    private void completeTrade(TradeSession session) {
        Player player1 = Bukkit.getPlayer(session.getPlayer1Uuid());
        Player player2 = Bukkit.getPlayer(session.getPlayer2Uuid());
        
        if (player1 == null || player2 == null) {
            cancelTrade(session, "One player went offline");
            return;
        }
        
        for (ItemStack item : session.getPlayerItems(session.getPlayer1Uuid()).values()) {
            if (item != null) {
                player2.getInventory().addItem(item);
            }
        }
        
        for (ItemStack item : session.getPlayerItems(session.getPlayer2Uuid()).values()) {
            if (item != null) {
                player1.getInventory().addItem(item);
            }
        }
        
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        player1.sendMessage(prefix + "§aTrade completed!");
        player2.sendMessage(prefix + "§aTrade completed!");
        
        player1.closeInventory();
        player2.closeInventory();
        
        tradeManager.endTradeSession(player1.getUniqueId());
    }
    
    private void cancelTrade(TradeSession session, String reason) {
        Player player1 = Bukkit.getPlayer(session.getPlayer1Uuid());
        Player player2 = Bukkit.getPlayer(session.getPlayer2Uuid());
        
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        String message = prefix + "§cTrade cancelled: " + reason;
        
        if (player1 != null) {
            player1.sendMessage(message);
            player1.closeInventory();
        }
        
        if (player2 != null) {
            player2.sendMessage(message);
            player2.closeInventory();
        }
        
        tradeManager.endTradeSession(session.getPlayer1Uuid());
    }
}
