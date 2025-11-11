package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.items.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemCommand implements CommandExecutor, Listener {
    
    private final BoxPvPCore plugin;
    private static final String GUI_TITLE = "§6§lCreate Custom Item";
    private static final String CONFIRM_TITLE = "§a§lConfirm Item Name";
    private final Map<UUID, ItemCreationSession> sessions = new HashMap<>();
    private final Map<UUID, ItemCreationSession> pendingNameInput = new HashMap<>();

    public ItemCommand(BoxPvPCore plugin) {
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

        if (!player.hasPermission("boxpvp.admin")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /item <create|give|list>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /item create <tool|weapon|block>");
                    return true;
                }
                openCreationGUI(player, args[1]);
                break;

            case "give":
                if (args.length < 3) {
                    player.sendMessage("§cUsage: /item give <player> <item_id>");
                    return true;
                }
                giveCustomItem(player, args[1], args[2]);
                break;

            case "list":
                listCustomItems(player);
                break;

            default:
                player.sendMessage("§cUsage: /item <create|give|list>");
                break;
        }

        return true;
    }

    private void openCreationGUI(Player player, String type) {
        ItemCreationSession session = new ItemCreationSession();
        session.type = type;
        sessions.put(player.getUniqueId(), session);
        openCreationGUIWithSession(player, session);
    }
    
    private void openCreationGUIWithSession(Player player, ItemCreationSession session) {
        Inventory gui = Bukkit.createInventory(null, 54, GUI_TITLE);
        sessions.put(player.getUniqueId(), session);

        ItemStack materialIcon = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta materialMeta = materialIcon.getItemMeta();
        materialMeta.setDisplayName("§e§lSelect Material");
        materialMeta.setLore(List.of("§7Click to cycle through materials"));
        materialIcon.setItemMeta(materialMeta);
        gui.setItem(10, materialIcon);

        ItemStack levelIcon = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta levelMeta = levelIcon.getItemMeta();
        levelMeta.setDisplayName("§b§lLevel: I");
        levelMeta.setLore(List.of("§7Click to increase level"));
        levelIcon.setItemMeta(levelMeta);
        gui.setItem(12, levelIcon);

        ItemStack rarityIcon = new ItemStack(Material.NETHER_STAR);
        ItemMeta rarityMeta = rarityIcon.getItemMeta();
        rarityMeta.setDisplayName("§f§lRarity: Common");
        rarityMeta.setLore(List.of("§7Click to cycle rarity"));
        rarityIcon.setItemMeta(rarityMeta);
        gui.setItem(14, rarityIcon);

        ItemStack efficiencyIcon = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta effMeta = efficiencyIcon.getItemMeta();
        effMeta.setDisplayName("§a§lEfficiency: 0");
        effMeta.setLore(List.of("§7Left click: +1", "§7Right click: +10"));
        efficiencyIcon.setItemMeta(effMeta);
        gui.setItem(30, efficiencyIcon);

        ItemStack fortuneIcon = new ItemStack(Material.DIAMOND);
        ItemMeta fortMeta = fortuneIcon.getItemMeta();
        fortMeta.setDisplayName("§e§lFortune: 0");
        fortMeta.setLore(List.of("§7Left click: +1", "§7Right click: +10"));
        fortuneIcon.setItemMeta(fortMeta);
        gui.setItem(32, fortuneIcon);

        ItemStack nameIcon = new ItemStack(Material.NAME_TAG);
        ItemMeta nameMeta = nameIcon.getItemMeta();
        nameMeta.setDisplayName("§e§lSet Item Name & Color");
        nameMeta.setLore(Arrays.asList(
            "§7Click to set custom name",
            "§7Use &a, &c, &b etc. for colors"
        ));
        nameIcon.setItemMeta(nameMeta);
        gui.setItem(16, nameIcon);
        
        ItemStack confirmIcon = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta confirmMeta = confirmIcon.getItemMeta();
        confirmMeta.setDisplayName("§a§lCREATE ITEM");
        confirmIcon.setItemMeta(confirmMeta);
        gui.setItem(49, confirmIcon);

        player.openInventory(gui);
    }

    private void giveCustomItem(Player sender, String targetName, String itemId) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return;
        }

        CustomItem customItem = plugin.getCustomItemManager().getItem(itemId);
        if (customItem == null) {
            sender.sendMessage("§cCustom item not found!");
            return;
        }

        target.getInventory().addItem(customItem.build());
        sender.sendMessage("§aGave " + customItem.getDisplayName() + " §ato " + target.getName());
        target.sendMessage("§aYou received " + customItem.getDisplayName());
    }

    private void listCustomItems(Player player) {
        player.sendMessage("§6§l=== Custom Items ===");
        for (String id : plugin.getCustomItemManager().getItemIds()) {
            CustomItem item = plugin.getCustomItemManager().getItem(id);
            player.sendMessage("§e" + id + " §7- " + item.getDisplayName());
        }
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

        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        ItemCreationSession session = sessions.get(player.getUniqueId());
        if (session == null) {
            return;
        }

        int slot = event.getSlot();

        if (slot == 12) {
            int currentLevel = session.level.getLevel();
            currentLevel = (currentLevel % 15) + 1;
            session.level = ItemLevel.fromLevel(currentLevel);
            updateGUIItem(event.getInventory(), 12, "§b§lLevel: " + session.level.getRomanNumeral());
        } else if (slot == 14) {
            ItemRarity[] rarities = ItemRarity.values();
            int currentIndex = session.rarity.ordinal();
            currentIndex = (currentIndex + 1) % rarities.length;
            session.rarity = rarities[currentIndex];
            updateGUIItem(event.getInventory(), 14, "§f§lRarity: " + session.rarity.getDisplayName());
        } else if (slot == 30) {
            if (event.isLeftClick()) {
                session.stats.setEfficiency(session.stats.getEfficiency() + 1);
            } else if (event.isRightClick()) {
                session.stats.setEfficiency(session.stats.getEfficiency() + 10);
            }
            updateGUIItem(event.getInventory(), 30, "§a§lEfficiency: " + session.stats.getEfficiency());
        } else if (slot == 32) {
            if (event.isLeftClick()) {
                session.stats.setFortune(session.stats.getFortune() + 1);
            } else if (event.isRightClick()) {
                session.stats.setFortune(session.stats.getFortune() + 10);
            }
            updateGUIItem(event.getInventory(), 32, "§e§lFortune: " + session.stats.getFortune());
        } else if (slot == 16) {
            player.closeInventory();
            pendingNameInput.put(player.getUniqueId(), session);
            player.sendMessage("§e§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            player.sendMessage("§6§lItem Name Setup");
            player.sendMessage("§7Type the item name and color in chat.");
            player.sendMessage("§7Use §e& §7for color codes (e.g., §e&a&lSuper Pickaxe§7)");
            player.sendMessage("§7Type §ccancel §7to abort.");
            player.sendMessage("§e§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        } else if (slot == 49) {
            if (session.customName == null || session.customName.isEmpty()) {
                player.sendMessage("§cPlease set item name first! (Click the Name Tag)");
                return;
            }
            createCustomItem(player, session);
            player.closeInventory();
            sessions.remove(player.getUniqueId());
        }
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        if (!pendingNameInput.containsKey(player.getUniqueId())) {
            return;
        }
        
        event.setCancelled(true);
        
        String input = event.getMessage();
        
        if (input.equalsIgnoreCase("cancel")) {
            pendingNameInput.remove(player.getUniqueId());
            player.sendMessage("§cItem creation cancelled.");
            return;
        }
        
        ItemCreationSession session = pendingNameInput.get(player.getUniqueId());
        String coloredName = ChatColor.translateAlternateColorCodes('&', input);
        session.customName = coloredName;
        
        Bukkit.getScheduler().runTask(plugin, () -> {
            openConfirmNameGUI(player, session, coloredName);
        });
        
        pendingNameInput.remove(player.getUniqueId());
    }
    
    private void openConfirmNameGUI(Player player, ItemCreationSession session, String itemName) {
        Inventory gui = Bukkit.createInventory(null, 27, CONFIRM_TITLE);
        
        for (int i = 0; i < 27; i++) {
            if (i < 10 || i == 13 || i == 16 || i >= 17) {
                ItemStack redGlass = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                ItemMeta redMeta = redGlass.getItemMeta();
                redMeta.setDisplayName("§c§lCancel");
                redMeta.setLore(Arrays.asList("§7Click to go back"));
                redGlass.setItemMeta(redMeta);
                gui.setItem(i, redGlass);
            }
        }
        
        for (int i = 10; i <= 12; i++) {
            ItemStack greenGlass = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta greenMeta = greenGlass.getItemMeta();
            greenMeta.setDisplayName("§a§lConfirm");
            greenMeta.setLore(Arrays.asList("§7Click to confirm this name"));
            greenGlass.setItemMeta(greenMeta);
            gui.setItem(i, greenGlass);
        }
        
        for (int i = 14; i <= 15; i++) {
            ItemStack greenGlass = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta greenMeta = greenGlass.getItemMeta();
            greenMeta.setDisplayName("§a§lConfirm");
            greenMeta.setLore(Arrays.asList("§7Click to confirm this name"));
            greenGlass.setItemMeta(greenMeta);
            gui.setItem(i, greenGlass);
        }
        
        ItemStack nameTag = new ItemStack(Material.NAME_TAG);
        ItemMeta tagMeta = nameTag.getItemMeta();
        tagMeta.setDisplayName(itemName);
        tagMeta.setLore(Arrays.asList(
            "",
            "§7This will be the item's name",
            "§7Click §agreen §7to confirm",
            "§7Click §cred §7to cancel"
        ));
        nameTag.setItemMeta(tagMeta);
        gui.setItem(13, nameTag);
        
        sessions.put(player.getUniqueId(), session);
        player.openInventory(gui);
    }
    
    @EventHandler
    public void onConfirmNameClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(CONFIRM_TITLE)) {
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
        
        ItemCreationSession session = sessions.get(player.getUniqueId());
        if (session == null) {
            return;
        }
        
        if (clicked.getType() == Material.LIME_STAINED_GLASS_PANE) {
            player.closeInventory();
            player.sendMessage("§aItem name confirmed: " + session.customName);
            Bukkit.getScheduler().runTask(plugin, () -> {
                openCreationGUIWithSession(player, session);
            });
        } else if (clicked.getType() == Material.RED_STAINED_GLASS_PANE) {
            session.customName = null;
            player.closeInventory();
            player.sendMessage("§cName cancelled. Returning to creation menu.");
            Bukkit.getScheduler().runTask(plugin, () -> {
                openCreationGUIWithSession(player, session);
            });
        }
    }

    private void updateGUIItem(Inventory inv, int slot, String newName) {
        ItemStack item = inv.getItem(slot);
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(newName);
            item.setItemMeta(meta);
        }
    }

    private void createCustomItem(Player player, ItemCreationSession session) {
        String id = "custom_" + System.currentTimeMillis();
        String displayName = session.customName != null ? session.customName : session.type + " Item";

        Material material = Material.DIAMOND_PICKAXE;

        CustomItem customItem = new CustomItem(plugin, id, displayName, material, 
            session.level, session.rarity, session.stats);

        plugin.getCustomItemManager().registerItem(customItem);

        player.getInventory().addItem(customItem.build());
        player.sendMessage("§aCreated custom item: " + displayName);
    }

    private static class ItemCreationSession {
        String type = "tool";
        String customName = null;
        ItemLevel level = ItemLevel.I;
        ItemRarity rarity = ItemRarity.COMMON;
        CustomItemStats stats = new CustomItemStats();
    }
}
