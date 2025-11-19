package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.GiftCode;
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

import java.util.*;

public class CreateGiftCodeCommand implements CommandExecutor, Listener {
    
    private final BoxPvPCore plugin;
    private static final String GUI_TITLE = "§6§lCreate Gift Code";
    private final Map<UUID, GiftCode> creatingCodes;
    
    public CreateGiftCodeCommand(BoxPvPCore plugin) {
        this.plugin = plugin;
        this.creatingCodes = new HashMap<>();
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
        
        if (!player.hasPermission("boxpvp.admin")) {
            player.sendMessage(prefix + plugin.getConfig().getString("messages.no-permission", "§cNo permission!"));
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(prefix + "§cUsage: /creategiftcode <code> <maxuses>");
            player.sendMessage(prefix + "§7Max uses: Use -1 for unlimited uses");
            return true;
        }
        
        String code = args[0].toUpperCase();
        
        if (plugin.getGiftCodeManager().giftCodeExists(code)) {
            player.sendMessage(prefix + "§cA gift code with this name already exists!");
            return true;
        }
        
        int maxUses;
        try {
            maxUses = Integer.parseInt(args[1]);
            if (maxUses == 0 || maxUses < -1) {
                player.sendMessage(prefix + "§cMax uses must be -1 (unlimited) or a positive number!");
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(prefix + "§cInvalid number for max uses!");
            return true;
        }
        
        GiftCode giftCode = new GiftCode(code, maxUses);
        creatingCodes.put(player.getUniqueId(), giftCode);
        
        openCreationGUI(player);
        
        player.sendMessage(prefix + "§aCreating gift code: §e" + code);
        player.sendMessage(prefix + "§7Add items and set currency rewards in the GUI");
        
        return true;
    }
    
    private void openCreationGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, GUI_TITLE);
        
        // Top row decoration
        ItemStack bluePane = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta blueMeta = bluePane.getItemMeta();
        blueMeta.setDisplayName(" ");
        bluePane.setItemMeta(blueMeta);
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, bluePane);
        }
        
        // Info panel at top center
        ItemStack infoIcon = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoIcon.getItemMeta();
        infoMeta.setDisplayName("§b§lGift Code Creator");
        infoMeta.setLore(Arrays.asList(
            "§71. Hold the item you want to add",
            "§72. Click any empty slot (9-44)",
            "§73. The item will be added as a reward",
            "",
            "§7Right-click items to remove them"
        ));
        infoIcon.setItemMeta(infoMeta);
        gui.setItem(4, infoIcon);
        
        ItemStack moneyIcon = new ItemStack(Material.GOLD_INGOT);
        ItemMeta moneyMeta = moneyIcon.getItemMeta();
        moneyMeta.setDisplayName("§6§lMoney Reward");
        moneyMeta.setLore(Arrays.asList(
            "§7Current: §e$0",
            "",
            "§eLeft Click: §a+100",
            "§eRight Click: §a+500",
            "§eShift + Left Click: §a+1000",
            "§eShift + Right Click: §c-100"
        ));
        moneyIcon.setItemMeta(moneyMeta);
        gui.setItem(45, moneyIcon);
        
        ItemStack gemsIcon = new ItemStack(Material.EMERALD);
        ItemMeta gemsMeta = gemsIcon.getItemMeta();
        gemsMeta.setDisplayName("§a§lGems Reward");
        gemsMeta.setLore(Arrays.asList(
            "§7Current: §a0 gems",
            "",
            "§eLeft Click: §a+50",
            "§eRight Click: §a+100",
            "§eShift + Left Click: §a+500",
            "§eShift + Right Click: §c-50"
        ));
        gemsIcon.setItemMeta(gemsMeta);
        gui.setItem(46, gemsIcon);
        
        ItemStack coinsIcon = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta coinsMeta = coinsIcon.getItemMeta();
        coinsMeta.setDisplayName("§e§lCoins Reward");
        coinsMeta.setLore(Arrays.asList(
            "§7Current: §e0 coins",
            "",
            "§eLeft Click: §a+25",
            "§eRight Click: §a+100",
            "§eShift + Left Click: §a+250",
            "§eShift + Right Click: §c-25"
        ));
        coinsIcon.setItemMeta(coinsMeta);
        gui.setItem(47, coinsIcon);
        
        ItemStack createIcon = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta createMeta = createIcon.getItemMeta();
        createMeta.setDisplayName("§a§l✔ CREATE");
        createMeta.setLore(Arrays.asList(
            "§7Click to finalize and save",
            "§7this gift code"
        ));
        createIcon.setItemMeta(createMeta);
        gui.setItem(53, createIcon);
        
        ItemStack cancelIcon = new ItemStack(Material.RED_CONCRETE);
        ItemMeta cancelMeta = cancelIcon.getItemMeta();
        cancelMeta.setDisplayName("§c§l✖ CANCEL");
        cancelMeta.setLore(Arrays.asList(
            "§7Discard this gift code"
        ));
        cancelIcon.setItemMeta(cancelMeta);
        gui.setItem(52, cancelIcon);
        
        player.openInventory(gui);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;
        
        event.setCancelled(true);
        
        if (!creatingCodes.containsKey(player.getUniqueId())) {
            player.closeInventory();
            return;
        }
        
        GiftCode giftCode = creatingCodes.get(player.getUniqueId());
        int slot = event.getRawSlot();
        
        if (slot == 45) {
            handleMoneyClick(player, event.getClick(), giftCode);
            updateGUI(player, event.getInventory(), giftCode);
        } else if (slot == 46) {
            handleGemsClick(player, event.getClick(), giftCode);
            updateGUI(player, event.getInventory(), giftCode);
        } else if (slot == 47) {
            handleCoinsClick(player, event.getClick(), giftCode);
            updateGUI(player, event.getInventory(), giftCode);
        } else if (slot == 53) {
            createGiftCode(player, giftCode);
        } else if (slot == 52) {
            cancelCreation(player);
        } else if (slot >= 0 && slot <= 44) {
            ItemStack currentItem = event.getInventory().getItem(slot);
            
            if (event.isRightClick() && currentItem != null && currentItem.getType() != Material.AIR) {
                event.getInventory().setItem(slot, null);
                giftCode.removeItemReward(slot);
                player.sendMessage("§cRemoved item from gift code rewards!");
            } else if (event.isLeftClick()) {
                ItemStack handItem = player.getInventory().getItemInMainHand();
                if (handItem != null && handItem.getType() != Material.AIR) {
                    ItemStack rewardItem = handItem.clone();
                    event.getInventory().setItem(slot, rewardItem);
                    giftCode.setItemReward(slot, rewardItem);
                    player.sendMessage("§aAdded item to gift code rewards!");
                }
            }
        }
    }
    
    private void handleMoneyClick(Player player, org.bukkit.event.inventory.ClickType clickType, GiftCode giftCode) {
        switch (clickType) {
            case LEFT:
                giftCode.setMoneyReward(giftCode.getMoneyReward() + 100);
                break;
            case RIGHT:
                giftCode.setMoneyReward(giftCode.getMoneyReward() + 500);
                break;
            case SHIFT_LEFT:
                giftCode.setMoneyReward(giftCode.getMoneyReward() + 1000);
                break;
            case SHIFT_RIGHT:
                giftCode.setMoneyReward(Math.max(0, giftCode.getMoneyReward() - 100));
                break;
        }
    }
    
    private void handleGemsClick(Player player, org.bukkit.event.inventory.ClickType clickType, GiftCode giftCode) {
        switch (clickType) {
            case LEFT:
                giftCode.setGemsReward(giftCode.getGemsReward() + 50);
                break;
            case RIGHT:
                giftCode.setGemsReward(giftCode.getGemsReward() + 100);
                break;
            case SHIFT_LEFT:
                giftCode.setGemsReward(giftCode.getGemsReward() + 500);
                break;
            case SHIFT_RIGHT:
                giftCode.setGemsReward(Math.max(0, giftCode.getGemsReward() - 50));
                break;
        }
    }
    
    private void handleCoinsClick(Player player, org.bukkit.event.inventory.ClickType clickType, GiftCode giftCode) {
        switch (clickType) {
            case LEFT:
                giftCode.setCoinsReward(giftCode.getCoinsReward() + 25);
                break;
            case RIGHT:
                giftCode.setCoinsReward(giftCode.getCoinsReward() + 100);
                break;
            case SHIFT_LEFT:
                giftCode.setCoinsReward(giftCode.getCoinsReward() + 250);
                break;
            case SHIFT_RIGHT:
                giftCode.setCoinsReward(Math.max(0, giftCode.getCoinsReward() - 25));
                break;
        }
    }
    
    private void updateGUI(Player player, Inventory gui, GiftCode giftCode) {
        ItemStack moneyIcon = gui.getItem(45);
        if (moneyIcon != null) {
            ItemMeta meta = moneyIcon.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("§7Current: §e$" + (int) giftCode.getMoneyReward());
            lore.add("");
            lore.add("§eLeft Click: §a+100");
            lore.add("§eRight Click: §a+500");
            lore.add("§eShift + Left Click: §a+1000");
            lore.add("§eShift + Right Click: §c-100");
            meta.setLore(lore);
            moneyIcon.setItemMeta(meta);
        }
        
        ItemStack gemsIcon = gui.getItem(46);
        if (gemsIcon != null) {
            ItemMeta meta = gemsIcon.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("§7Current: §a" + (int) giftCode.getGemsReward() + " gems");
            lore.add("");
            lore.add("§eLeft Click: §a+50");
            lore.add("§eRight Click: §a+100");
            lore.add("§eShift + Left Click: §a+500");
            lore.add("§eShift + Right Click: §c-50");
            meta.setLore(lore);
            gemsIcon.setItemMeta(meta);
        }
        
        ItemStack coinsIcon = gui.getItem(47);
        if (coinsIcon != null) {
            ItemMeta meta = coinsIcon.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("§7Current: §e" + (int) giftCode.getCoinsReward() + " coins");
            lore.add("");
            lore.add("§eLeft Click: §a+25");
            lore.add("§eRight Click: §a+100");
            lore.add("§eShift + Left Click: §a+250");
            lore.add("§eShift + Right Click: §c-25");
            meta.setLore(lore);
            coinsIcon.setItemMeta(meta);
        }
        
        player.updateInventory();
    }
    
    private void createGiftCode(Player player, GiftCode giftCode) {
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        
        if (!giftCode.hasRewards()) {
            player.sendMessage(prefix + "§cYou must add at least one reward!");
            return;
        }
        
        plugin.getGiftCodeManager().addGiftCode(giftCode);
        creatingCodes.remove(player.getUniqueId());
        player.closeInventory();
        
        player.sendMessage(prefix + "§a§lGift code created successfully!");
        player.sendMessage(prefix + "§7Code: §e" + giftCode.getCode());
        player.sendMessage(prefix + "§7Max Uses: §e" + (giftCode.getMaxUses() == -1 ? "Unlimited" : giftCode.getMaxUses()));
        
        List<String> rewardSummary = new ArrayList<>();
        if (giftCode.getMoneyReward() > 0) {
            rewardSummary.add("§e$" + (int) giftCode.getMoneyReward());
        }
        if (giftCode.getGemsReward() > 0) {
            rewardSummary.add("§a" + (int) giftCode.getGemsReward() + " gems");
        }
        if (giftCode.getCoinsReward() > 0) {
            rewardSummary.add("§e" + (int) giftCode.getCoinsReward() + " coins");
        }
        if (!giftCode.getItemRewards().isEmpty()) {
            rewardSummary.add("§b" + giftCode.getItemRewards().size() + " items");
        }
        
        if (!rewardSummary.isEmpty()) {
            player.sendMessage(prefix + "§7Rewards: " + String.join("§7, ", rewardSummary));
        }
    }
    
    private void cancelCreation(Player player) {
        creatingCodes.remove(player.getUniqueId());
        player.closeInventory();
        player.sendMessage(plugin.getConfig().getString("messages.prefix", "") + "§cGift code creation cancelled.");
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;
        
        if (creatingCodes.containsKey(player.getUniqueId())) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (creatingCodes.containsKey(player.getUniqueId())) {
                    GiftCode giftCode = creatingCodes.get(player.getUniqueId());
                    openCreationGUI(player);
                }
            }, 1L);
        }
    }
}
