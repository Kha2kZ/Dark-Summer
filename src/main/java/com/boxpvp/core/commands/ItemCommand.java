
package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.items.CustomItem;
import com.boxpvp.core.items.CustomItemStats;
import com.boxpvp.core.items.ItemLevel;
import com.boxpvp.core.items.ItemRarity;
import com.boxpvp.core.items.ItemStats;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemCommand implements CommandExecutor, Listener {

    private final BoxPvPCore plugin;
    private static final String CREATION_GUI_TITLE = "§6§lCreate Custom Item";
    private static final String PREVIEW_GUI_TITLE = "§e§lItem Preview";
    private final Map<UUID, ItemCreationSession> sessions = new HashMap<>();

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

        if (args.length == 0) {
            player.sendMessage("§cUsage: /item create");
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            openCreationGUI(player);
            return true;
        }

        return true;
    }

    private void openCreationGUI(Player player) {
        ItemCreationSession session = new ItemCreationSession();
        sessions.put(player.getUniqueId(), session);
        openItemEditorGUI(player, session);
    }

    private void openItemEditorGUI(Player player, ItemCreationSession session) {
        Inventory inv = Bukkit.createInventory(null, 54, CREATION_GUI_TITLE);

        // Decoration - Top and Bottom rows
        ItemStack bluePane = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta blueMeta = bluePane.getItemMeta();
        blueMeta.setDisplayName(" ");
        bluePane.setItemMeta(blueMeta);
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, bluePane);
            inv.setItem(45 + i, bluePane);
        }

        // Left column decoration
        ItemStack grayPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta grayMeta = grayPane.getItemMeta();
        grayMeta.setDisplayName(" ");
        grayPane.setItemMeta(grayMeta);
        for (int i = 9; i <= 36; i += 9) {
            inv.setItem(i, grayPane);
        }

        // Config buttons - Left side
        inv.setItem(11, createConfigButton(Material.NAME_TAG, "§e§lItem Name", 
            session.customName != null ? session.customName : "§7Not set", session.customName != null));
        
        inv.setItem(20, createConfigButton(Material.PAPER, "§e§lItem Display", 
            session.displayMaterial != null ? session.displayMaterial.name() : "§7Not set", session.displayMaterial != null));
        
        inv.setItem(29, createConfigButton(Material.EXPERIENCE_BOTTLE, "§e§lLevel", 
            session.level.getRomanNumeral(), true));

        // Config buttons - Right side  
        inv.setItem(15, createConfigButton(Material.ENCHANTED_BOOK, "§a§lEfficiency", 
            "Level: " + session.stats.getEfficiency(), session.stats.getEfficiency() > 0));
        
        inv.setItem(16, createConfigButton(Material.EMERALD, "§a§lFortune", 
            "Level: " + session.stats.getFortune(), session.stats.getFortune() > 0));
        
        inv.setItem(24, createConfigButton(Material.IRON_SWORD, "§c§lDamage", 
            "+" + session.weaponDamage + " DMG", session.weaponDamage > 0));
        
        inv.setItem(25, createConfigButton(Material.SUGAR, "§c§lAttack Speed", 
            session.weaponSpeed + " APS", session.weaponSpeed > 0));
        
        inv.setItem(33, createConfigButton(Material.NETHER_STAR, "§d§lRarity", 
            session.rarity.getDisplayName(), true));

        // Info panel
        ItemStack infoIcon = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoIcon.getItemMeta();
        infoMeta.setDisplayName("§b§lCustom Item Creator");
        infoMeta.setLore(Arrays.asList(
            "§7Create tools or weapons with custom stats",
            "",
            "§e§lTool Stats:",
            "§7• Efficiency & Fortune",
            "",
            "§c§lWeapon Stats:",
            "§7• Damage & Attack Speed",
            "",
            "§aSet all stats as needed!"
        ));
        infoIcon.setItemMeta(infoMeta);
        inv.setItem(4, infoIcon);

        // Preview button
        ItemStack preview = new ItemStack(Material.CHEST);
        ItemMeta previewMeta = preview.getItemMeta();
        previewMeta.setDisplayName("§a§lPREVIEW ITEM");
        previewMeta.setLore(Arrays.asList("§7Click to see how your", "§7item will look"));
        preview.setItemMeta(previewMeta);
        inv.setItem(48, preview);

        // Create button
        ItemStack create = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta createMeta = create.getItemMeta();
        createMeta.setDisplayName("§a§l✔ CREATE ITEM");
        createMeta.setLore(Arrays.asList("§7Click to create your custom item"));
        create.setItemMeta(createMeta);
        inv.setItem(50, create);

        player.openInventory(inv);
    }

    private void openToolCreationGUI(Player player, ItemCreationSession session) {
        Inventory inv = Bukkit.createInventory(null, 54, "§b§lCreate Tool");

        // Decoration
        addGUIDecoration(inv);

        // Config buttons
        inv.setItem(10, createConfigButton(Material.NAME_TAG, "§e§lItem Name", 
            session.customName != null ? session.customName : "§7Not set", session.customName != null));
        
        inv.setItem(12, createConfigButton(Material.PAPER, "§e§lItem Display", 
            session.displayMaterial != null ? session.displayMaterial.name() : "§7Not set", session.displayMaterial != null));
        
        inv.setItem(14, createConfigButton(Material.ENCHANTED_BOOK, "§e§lEfficiency", 
            "Level: " + session.stats.getEfficiency(), session.stats.getEfficiency() > 0));
        
        inv.setItem(16, createConfigButton(Material.EMERALD, "§e§lFortune", 
            "Level: " + session.stats.getFortune(), session.stats.getFortune() > 0));
        
        inv.setItem(28, createConfigButton(Material.EXPERIENCE_BOTTLE, "§e§lLevel", 
            session.level.getRomanNumeral(), true));
        
        inv.setItem(30, createConfigButton(Material.NETHER_STAR, "§e§lRarity", 
            session.rarity.getDisplayName(), true));

        // Preview button
        ItemStack preview = new ItemStack(Material.CHEST);
        ItemMeta previewMeta = preview.getItemMeta();
        previewMeta.setDisplayName("§a§lPREVIEW ITEM");
        previewMeta.setLore(Arrays.asList("§7Click to see how your", "§7item will look"));
        preview.setItemMeta(previewMeta);
        inv.setItem(34, preview);

        // Create button
        ItemStack create = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta createMeta = create.getItemMeta();
        createMeta.setDisplayName("§a§lCREATE ITEM");
        create.setItemMeta(createMeta);
        inv.setItem(49, create);

        player.openInventory(inv);
    }

    private void openWeaponCreationGUI(Player player, ItemCreationSession session) {
        Inventory inv = Bukkit.createInventory(null, 54, "§c§lCreate Weapon");

        // Decoration
        addGUIDecoration(inv);

        // Config buttons
        inv.setItem(10, createConfigButton(Material.NAME_TAG, "§e§lItem Name", 
            session.customName != null ? session.customName : "§7Not set", session.customName != null));
        
        inv.setItem(12, createConfigButton(Material.PAPER, "§e§lItem Display", 
            session.displayMaterial != null ? session.displayMaterial.name() : "§7Not set", session.displayMaterial != null));
        
        inv.setItem(14, createConfigButton(Material.IRON_SWORD, "§e§lDamage", 
            "+" + session.weaponDamage + " DMG", session.weaponDamage > 0));
        
        inv.setItem(16, createConfigButton(Material.SUGAR, "§e§lAttack Speed", 
            session.weaponSpeed + " APS", session.weaponSpeed > 0));
        
        inv.setItem(28, createConfigButton(Material.EXPERIENCE_BOTTLE, "§e§lLevel", 
            session.level.getRomanNumeral(), true));
        
        inv.setItem(30, createConfigButton(Material.NETHER_STAR, "§e§lRarity", 
            session.rarity.getDisplayName(), true));

        // Preview button
        ItemStack preview = new ItemStack(Material.CHEST);
        ItemMeta previewMeta = preview.getItemMeta();
        previewMeta.setDisplayName("§a§lPREVIEW ITEM");
        previewMeta.setLore(Arrays.asList("§7Click to see how your", "§7item will look"));
        preview.setItemMeta(previewMeta);
        inv.setItem(34, preview);

        // Create button
        ItemStack create = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta createMeta = create.getItemMeta();
        createMeta.setDisplayName("§a§lCREATE WEAPON");
        create.setItemMeta(createMeta);
        inv.setItem(49, create);

        player.openInventory(inv);
    }

    private void addGUIDecoration(Inventory inv) {
        ItemStack grayPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta grayMeta = grayPane.getItemMeta();
        grayMeta.setDisplayName(" ");
        grayPane.setItemMeta(grayMeta);
        
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, grayPane);
            inv.setItem(45 + i, grayPane);
        }
    }

    private ItemStack createConfigButton(Material material, String name, String value, boolean configured) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList("§7Current: §f" + value, "", "§eClick to configure"));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        if (title.equals(CREATION_GUI_TITLE)) {
            event.setCancelled(true);
            handleItemEditorClick(player, event.getRawSlot());
        } else if (title.equals("§b§lCreate Tool")) {
            event.setCancelled(true);
            handleToolGUIClick(player, event.getRawSlot());
        } else if (title.equals("§c§lCreate Weapon")) {
            event.setCancelled(true);
            handleWeaponGUIClick(player, event.getRawSlot());
        } else if (title.equals(PREVIEW_GUI_TITLE)) {
            event.setCancelled(true);
        }
    }

    private void handleItemEditorClick(Player player, int slot) {
        ItemCreationSession session = sessions.get(player.getUniqueId());
        if (session == null) return;
        
        switch (slot) {
            case 11: // Name
                promptForName(player, session);
                break;
            case 20: // Display Material
                promptForMaterial(player, session);
                break;
            case 15: // Efficiency
                promptForEfficiency(player, session);
                break;
            case 16: // Fortune
                promptForFortune(player, session);
                break;
            case 24: // Damage
                promptForDamage(player, session);
                break;
            case 25: // Attack Speed
                promptForAttackSpeed(player, session);
                break;
            case 29: // Level
                cycleLevel(session);
                openItemEditorGUI(player, session);
                break;
            case 33: // Rarity
                cycleRarity(session);
                openItemEditorGUI(player, session);
                break;
            case 48: // Preview
                showPreview(player, session, hasWeaponStats(session));
                break;
            case 50: // Create
                createCustomItem(player, session);
                break;
        }
    }

    private boolean hasWeaponStats(ItemCreationSession session) {
        return session.weaponDamage > 0 || session.weaponSpeed > 0;
    }

    private void createCustomItem(Player player, ItemCreationSession session) {
        if (hasWeaponStats(session)) {
            createCustomWeapon(player, session);
        } else {
            createCustomTool(player, session);
        }
    }

    private void handleToolGUIClick(Player player, int slot) {
        ItemCreationSession session = sessions.get(player.getUniqueId());
        if (session == null) return;
        
        switch (slot) {
            case 10: // Name
                promptForName(player, session);
                break;
            case 12: // Display Material
                promptForMaterial(player, session);
                break;
            case 14: // Efficiency
                promptForEfficiency(player, session);
                break;
            case 16: // Fortune
                promptForFortune(player, session);
                break;
            case 28: // Level
                cycleLevel(session);
                openToolCreationGUI(player, session);
                break;
            case 30: // Rarity
                cycleRarity(session);
                openToolCreationGUI(player, session);
                break;
            case 34: // Preview
                showPreview(player, session, false);
                break;
            case 49: // Create
                createCustomTool(player, session);
                break;
        }
    }

    private void handleWeaponGUIClick(Player player, int slot) {
        ItemCreationSession session = sessions.get(player.getUniqueId());
        if (session == null) return;
        
        switch (slot) {
            case 10: // Name
                promptForName(player, session);
                break;
            case 12: // Display Material
                promptForMaterial(player, session);
                break;
            case 14: // Damage
                promptForDamage(player, session);
                break;
            case 16: // Attack Speed
                promptForAttackSpeed(player, session);
                break;
            case 28: // Level
                cycleLevel(session);
                openWeaponCreationGUI(player, session);
                break;
            case 30: // Rarity
                cycleRarity(session);
                openWeaponCreationGUI(player, session);
                break;
            case 34: // Preview
                showPreview(player, session, true);
                break;
            case 49: // Create
                createCustomWeapon(player, session);
                break;
        }
    }

    private void promptForName(Player player, ItemCreationSession session) {
        player.closeInventory();
        player.sendMessage("§eEnter item name (use & for colors, &# for RGB like &#FF5733):");
        player.sendMessage("§7Type 'cancel' to cancel");
        
        Conversation conversation = new ConversationFactory(plugin)
            .withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return "";
                }
                
                @Override
                public Prompt acceptInput(ConversationContext context, String input) {
                    if (input.equalsIgnoreCase("cancel")) {
                        return Prompt.END_OF_CONVERSATION;
                    }
                    
                    session.customName = translateColorCodes(input);
                    Bukkit.getScheduler().runTask(plugin, () -> openItemEditorGUI(player, session));
                    return Prompt.END_OF_CONVERSATION;
                }
            })
            .withLocalEcho(false)
            .withTimeout(60)
            .thatExcludesNonPlayersWithMessage("")
            .buildConversation(player);
        
        conversation.begin();
    }

    private void promptForMaterial(Player player, ItemCreationSession session) {
        player.closeInventory();
        player.sendMessage("§eEnter material (e.g., DIAMOND_SWORD, IRON_PICKAXE):");
        player.sendMessage("§7Type 'cancel' to cancel");
        
        Conversation conversation = new ConversationFactory(plugin)
            .withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return "";
                }
                
                @Override
                public Prompt acceptInput(ConversationContext context, String input) {
                    if (input.equalsIgnoreCase("cancel")) {
                        return Prompt.END_OF_CONVERSATION;
                    }
                    
                    try {
                        session.displayMaterial = Material.valueOf(input.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        player.sendMessage("§cInvalid material!");
                    }
                    
                    Bukkit.getScheduler().runTask(plugin, () -> openItemEditorGUI(player, session));
                    return Prompt.END_OF_CONVERSATION;
                }
            })
            .withLocalEcho(false)
            .withTimeout(60)
            .buildConversation(player);
        
        conversation.begin();
    }

    private void promptForEfficiency(Player player, ItemCreationSession session) {
        player.closeInventory();
        player.sendMessage("§eEnter efficiency level (1-10):");
        
        Conversation conversation = new ConversationFactory(plugin)
            .withFirstPrompt(new NumericPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return "";
                }
                
                @Override
                protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
                    session.stats.setEfficiency(Math.max(0, Math.min(10, input.intValue())));
                    Bukkit.getScheduler().runTask(plugin, () -> openItemEditorGUI(player, session));
                    return Prompt.END_OF_CONVERSATION;
                }
            })
            .withLocalEcho(false)
            .buildConversation(player);
        
        conversation.begin();
    }

    private void promptForFortune(Player player, ItemCreationSession session) {
        player.closeInventory();
        player.sendMessage("§eEnter fortune level (1-10):");
        
        Conversation conversation = new ConversationFactory(plugin)
            .withFirstPrompt(new NumericPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return "";
                }
                
                @Override
                protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
                    session.stats.setFortune(Math.max(0, Math.min(10, input.intValue())));
                    Bukkit.getScheduler().runTask(plugin, () -> openItemEditorGUI(player, session));
                    return Prompt.END_OF_CONVERSATION;
                }
            })
            .withLocalEcho(false)
            .buildConversation(player);
        
        conversation.begin();
    }

    private void promptForDamage(Player player, ItemCreationSession session) {
        player.closeInventory();
        player.sendMessage("§eEnter damage (1-100):");
        
        Conversation conversation = new ConversationFactory(plugin)
            .withFirstPrompt(new NumericPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return "";
                }
                
                @Override
                protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
                    session.weaponDamage = Math.max(1, Math.min(100, input.doubleValue()));
                    Bukkit.getScheduler().runTask(plugin, () -> openItemEditorGUI(player, session));
                    return Prompt.END_OF_CONVERSATION;
                }
            })
            .withLocalEcho(false)
            .buildConversation(player);
        
        conversation.begin();
    }

    private void promptForAttackSpeed(Player player, ItemCreationSession session) {
        player.closeInventory();
        player.sendMessage("§eEnter attack speed (1-20, vanilla is 4):");
        
        Conversation conversation = new ConversationFactory(plugin)
            .withFirstPrompt(new NumericPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return "";
                }
                
                @Override
                protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
                    session.weaponSpeed = Math.max(1, Math.min(20, input.doubleValue()));
                    Bukkit.getScheduler().runTask(plugin, () -> openItemEditorGUI(player, session));
                    return Prompt.END_OF_CONVERSATION;
                }
            })
            .withLocalEcho(false)
            .buildConversation(player);
        
        conversation.begin();
    }

    private void cycleLevel(ItemCreationSession session) {
        ItemLevel[] levels = ItemLevel.values();
        int current = session.level.ordinal();
        session.level = levels[(current + 1) % levels.length];
    }

    private void cycleRarity(ItemCreationSession session) {
        ItemRarity[] rarities = ItemRarity.values();
        int current = session.rarity.ordinal();
        session.rarity = rarities[(current + 1) % rarities.length];
    }

    private void showPreview(Player player, ItemCreationSession session, boolean isWeapon) {
        Inventory preview = Bukkit.createInventory(null, 27, PREVIEW_GUI_TITLE);
        
        ItemStack previewItem = buildPreviewItem(session, isWeapon);
        preview.setItem(13, previewItem);
        
        player.openInventory(preview);
    }

    private ItemStack buildPreviewItem(ItemCreationSession session, boolean isWeapon) {
        Material mat = session.displayMaterial != null ? session.displayMaterial : 
                      (isWeapon ? Material.DIAMOND_SWORD : Material.DIAMOND_PICKAXE);
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        
        String name = session.customName != null ? session.customName : "§fCustom Item";
        meta.setDisplayName(name);
        
        List<String> lore = new ArrayList<>();
        if (isWeapon) {
            lore.add("§7Damage: §c+" + session.weaponDamage);
            lore.add("§7Attack Speed: §e" + session.weaponSpeed);
        } else {
            if (session.stats.getEfficiency() > 0) {
                lore.add("§7Efficiency: §b" + romanNumeral(session.stats.getEfficiency()));
            }
            if (session.stats.getFortune() > 0) {
                lore.add("§7Fortune: §a" + romanNumeral(session.stats.getFortune()));
            }
        }
        lore.add("");
        lore.add("§6§l" + session.level.getDisplay());
        lore.add(session.rarity.getDisplay() + " §l" + session.rarity.name());
        
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        
        return item;
    }

    private void createCustomTool(Player player, ItemCreationSession session) {
        Material mat = session.displayMaterial != null ? session.displayMaterial : Material.DIAMOND_PICKAXE;
        String name = session.customName != null ? session.customName : "§fCustom Tool";
        String id = "custom_tool_" + UUID.randomUUID().toString();
        
        CustomItemStats stats = new CustomItemStats(session.stats.getEfficiency(), session.stats.getFortune(), 0, 0);
        CustomItem customItem = new CustomItem(plugin, id, name, mat, session.level, session.rarity, stats);
        ItemStack tool = customItem.build();
        
        ItemMeta meta = tool.getItemMeta();
        
        if (session.stats.getEfficiency() > 0) {
            meta.addEnchant(Enchantment.DIG_SPEED, session.stats.getEfficiency(), true);
        }
        if (session.stats.getFortune() > 0) {
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, session.stats.getFortune(), true);
        }
        
        tool.setItemMeta(meta);
        
        player.getInventory().addItem(tool);
        player.sendMessage("§aCreated custom tool: " + name);
        player.closeInventory();
        sessions.remove(player.getUniqueId());
    }

    private void createCustomWeapon(Player player, ItemCreationSession session) {
        Material mat = session.displayMaterial != null ? session.displayMaterial : Material.DIAMOND_SWORD;
        String name = session.customName != null ? session.customName : "§fCustom Weapon";
        String id = "custom_weapon_" + UUID.randomUUID().toString();
        
        CustomItemStats stats = new CustomItemStats(0, 0, session.weaponDamage, session.weaponSpeed);
        CustomItem customItem = new CustomItem(plugin, id, name, mat, session.level, session.rarity, stats);
        ItemStack weapon = customItem.build();
        
        ItemMeta meta = weapon.getItemMeta();
        
        AttributeModifier damageModifier = new AttributeModifier(
            UUID.randomUUID(), "generic.attackDamage", 
            session.weaponDamage - 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND
        );
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, damageModifier);
        
        AttributeModifier speedModifier = new AttributeModifier(
            UUID.randomUUID(), "generic.attackSpeed", 
            session.weaponSpeed - 4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND
        );
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, speedModifier);
        
        weapon.setItemMeta(meta);
        
        player.getInventory().addItem(weapon);
        player.sendMessage("§aCreated custom weapon: " + name);
        player.closeInventory();
        sessions.remove(player.getUniqueId());
    }

    private String translateColorCodes(String text) {
        // Support &#RRGGBB format
        Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(text);
        StringBuffer buffer = new StringBuffer();
        
        while (matcher.find()) {
            String hex = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.of("#" + hex).toString());
        }
        matcher.appendTail(buffer);
        
        // Support traditional & codes
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    private String romanNumeral(int num) {
        String[] romans = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return num > 0 && num <= 10 ? romans[num - 1] : String.valueOf(num);
    }

    private static class ItemCreationSession {
        String itemType = "tool";
        String customName;
        Material displayMaterial;
        ItemLevel level = ItemLevel.I;
        ItemRarity rarity = ItemRarity.COMMON;
        ItemStats stats = new ItemStats();
        double weaponDamage = 7.0;
        double weaponSpeed = 4.0;
    }
}
