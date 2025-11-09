package com.boxpvp.core.listeners;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.trade.NPCTrade;
import com.boxpvp.core.trade.TradeRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class NPCTradeListener implements Listener {
    
    private final BoxPvPCore plugin;
    private static final String TRADE_GUI_TITLE = "§6§lNPC Trader";

    public NPCTradeListener(BoxPvPCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNPCClick(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager)) {
            return;
        }

        NPCTrade npcTrade = plugin.getNPCTradeManager().getNPCTrade(event.getRightClicked());
        if (npcTrade == null) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        openTradeGUI(player, npcTrade);
    }

    private void openTradeGUI(Player player, NPCTrade npcTrade) {
        Inventory gui = Bukkit.createInventory(null, 54, TRADE_GUI_TITLE);

        List<TradeRecipe> recipes = npcTrade.getRecipes();

        if (recipes.isEmpty()) {
            ItemStack noTrades = new ItemStack(Material.BARRIER);
            ItemMeta meta = noTrades.getItemMeta();
            meta.setDisplayName("§c§lNo Trades Available");
            meta.setLore(List.of("§7This NPC has no trades configured yet"));
            noTrades.setItemMeta(meta);
            gui.setItem(22, noTrades);
        } else {
            int slot = 10;
            for (TradeRecipe recipe : recipes) {
                if (slot >= 44) break;

                ItemStack displayItem = recipe.getOutputItem().clone();
                ItemMeta meta = displayItem.getItemMeta();

                List<String> lore = new ArrayList<>();
                lore.add("§e§lRequired Items:");

                for (ItemStack input : recipe.getInputItems()) {
                    lore.add("§7- " + input.getAmount() + "x " + input.getType().name());
                }

                lore.add("");
                lore.add("§a§lClick to trade!");

                meta.setLore(lore);
                displayItem.setItemMeta(meta);

                gui.setItem(slot, displayItem);

                slot++;
                if (slot % 9 == 8) {
                    slot += 2;
                }
            }
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onTradeClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(TRADE_GUI_TITLE)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        player.sendMessage("§cTrade system coming soon!");
    }
}
