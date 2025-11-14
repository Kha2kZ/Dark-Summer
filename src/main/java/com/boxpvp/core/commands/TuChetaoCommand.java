package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.AutoCraftRecipe;
import com.boxpvp.core.data.Rank;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TuChetaoCommand implements CommandExecutor, Listener {

    private final BoxPvPCore plugin;
    private static final String INPUT_GUI_TITLE = "§6§lSet Input Items";
    private static final String OUTPUT_GUI_TITLE = "§6§lSet Output Items";

    private final Map<UUID, List<ItemStack>> recipeInputs;
    private final Map<UUID, List<ItemStack>> recipeOutputs;

    // Pattern for translating hex color codes
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");


    public TuChetaoCommand(BoxPvPCore plugin) {
        this.plugin = plugin;
        this.recipeInputs = new HashMap<>();
        this.recipeOutputs = new HashMap<>();
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
        Rank rank = plugin.getRankManager().getRank(player);
        var autoCraftingManager = plugin.getAutoCraftingManager(); // Get AutoCraftingManager instance

        if (args.length > 0 && args[0].equalsIgnoreCase("create")) {
            if (!player.hasPermission("boxpvp.admin")) {
                player.sendMessage(prefix + "§cYou don't have permission to create recipes!");
                return true;
            }

            openInputGUI(player);
            player.sendMessage(prefix + "§ePlease place the INPUT items in the chest, then close it.");
            return true;
        }

        if (!rank.canAutocraft()) {
            player.sendMessage(prefix + "§cBạn cần ít nhất rank MVP+ để dùng lệnh này!");
            return true;
        }

        // Check persistent cooldown
        if (autoCraftingManager.isOnCooldown(player)) {
            long remaining = autoCraftingManager.getRemainingCooldown(player);
            player.sendMessage(prefix + "§cVui lòng đợi " + remaining + " giây trước khi dùng lại!");
            return true;
        }


        AutoCraftRecipe recipe = plugin.getAutoCraftingManager().findMatchingRecipe(player);

        if (recipe == null) {
            player.sendMessage(prefix + "§cNo matching recipe found! Check your inventory.");
            player.sendMessage(prefix + "§7Make sure you have all required items.");
            return true;
        }

        plugin.getAutoCraftingManager().craftRecipe(player, recipe);

        player.sendMessage(prefix + "§aSuccessfully auto-crafted items!");
        player.sendMessage(prefix + "§7Cooldown: §e" + rank.getAutoCraftCooldown() + "s");

        return true;
    }

    private void openInputGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, INPUT_GUI_TITLE);
        player.openInventory(gui);
    }

    private void openOutputGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, OUTPUT_GUI_TITLE);
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        String title = event.getView().getTitle();
        String prefix = plugin.getConfig().getString("messages.prefix", "");

        if (title.equals(INPUT_GUI_TITLE)) {
            List<ItemStack> inputs = new ArrayList<>();
            for (ItemStack item : event.getInventory().getContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    inputs.add(item.clone());
                }
            }

            if (inputs.isEmpty()) {
                player.sendMessage(prefix + "§cNo input items provided! Recipe creation cancelled.");
                recipeInputs.remove(player.getUniqueId());
                return;
            }

            recipeInputs.put(player.getUniqueId(), inputs);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                openOutputGUI(player);
                player.sendMessage(prefix + "§eNow place the OUTPUT items in the chest, then close it.");
            }, 1L);
        } else if (title.equals(OUTPUT_GUI_TITLE)) {
            List<ItemStack> outputs = new ArrayList<>();
            for (ItemStack item : event.getInventory().getContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    outputs.add(item.clone());
                }
            }

            List<ItemStack> inputs = recipeInputs.remove(player.getUniqueId());

            if (inputs == null) {
                player.sendMessage(prefix + "§cRecipe creation cancelled - no inputs found!");
                return;
            }

            if (outputs.isEmpty()) {
                player.sendMessage(prefix + "§cNo output items provided! Recipe creation cancelled.");
                return;
            }

            AutoCraftRecipe recipe = new AutoCraftRecipe(inputs, outputs, player.getName());
            plugin.getAutoCraftingManager().addRecipe(recipe);

            player.sendMessage(prefix + "§aAuto-craft recipe created successfully!");
            player.sendMessage(prefix + "§7Players can now use /tuchetao to craft with this recipe.");
        }
    }

    /**
     * Translates a string with legacy color codes and hex color codes to Minecraft's component format.
     *
     * @param text The text to translate.
     * @return The translated text.
     */
    public static String translateHexColorCodes(String text) {
        // Translate legacy color codes
        text = ChatColor.translateAlternateColorCodes('&', text);

        // Translate hex color codes
        Matcher matcher = HEX_COLOR_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            // Replace &#RRGGBB with &#RRGGBB which is the format expected by Paper/Spigot for hex colors
            matcher.appendReplacement(buffer, "&#" + matcher.group(1));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}