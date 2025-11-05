package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.GiftCode;
import com.boxpvp.core.data.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GiftCodeCommand implements CommandExecutor {
    
    private final BoxPvPCore plugin;
    
    public GiftCodeCommand(BoxPvPCore plugin) {
        this.plugin = plugin;
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
            player.sendMessage(prefix + "§cUsage: /giftcode <code>");
            return true;
        }
        
        String codeInput = args[0];
        GiftCode giftCode = plugin.getGiftCodeManager().getGiftCode(codeInput);
        
        if (giftCode == null) {
            player.sendMessage(prefix + "§cInvalid gift code!");
            return true;
        }
        
        if (giftCode.hasPlayerUsed(player.getUniqueId())) {
            player.sendMessage(prefix + "§cYou have already used this gift code!");
            return true;
        }
        
        if (!giftCode.isAvailable()) {
            player.sendMessage(prefix + "§cThis gift code has reached its maximum uses!");
            return true;
        }
        
        if (!giftCode.hasRewards()) {
            player.sendMessage(prefix + "§cThis gift code has no rewards configured!");
            return true;
        }
        
        List<ItemStack> itemsToGive = new ArrayList<>();
        for (ItemStack item : giftCode.getItemRewards()) {
            if (item != null) {
                itemsToGive.add(item.clone());
            }
        }
        
        if (!itemsToGive.isEmpty()) {
            Map<Integer, ItemStack> failedItems = player.getInventory().addItem(itemsToGive.toArray(new ItemStack[0]));
            
            if (!failedItems.isEmpty()) {
                player.sendMessage(prefix + "§cYour inventory is full! Some items were dropped on the ground.");
                for (ItemStack item : failedItems.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                }
            }
        }
        
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        
        if (giftCode.getMoneyReward() > 0) {
            playerData.addBalance(giftCode.getMoneyReward());
        }
        
        if (giftCode.getGemsReward() > 0) {
            playerData.addGems(giftCode.getGemsReward());
        }
        
        if (giftCode.getCoinsReward() > 0) {
            playerData.addCoins(giftCode.getCoinsReward());
        }
        
        giftCode.markAsUsed(player.getUniqueId());
        plugin.getGiftCodeManager().saveGiftCodes();
        
        player.sendMessage(prefix + "§a§l✔ Gift code redeemed successfully!");
        player.sendMessage("");
        
        if (giftCode.getMoneyReward() > 0) {
            player.sendMessage(prefix + "§6+ $" + (int) giftCode.getMoneyReward() + " Money");
        }
        if (giftCode.getGemsReward() > 0) {
            player.sendMessage(prefix + "§a+ " + (int) giftCode.getGemsReward() + " Gems");
        }
        if (giftCode.getCoinsReward() > 0) {
            player.sendMessage(prefix + "§e+ " + (int) giftCode.getCoinsReward() + " Coins");
        }
        if (!itemsToGive.isEmpty()) {
            player.sendMessage(prefix + "§b+ " + itemsToGive.size() + " Item(s)");
        }
        
        player.sendMessage("");
        
        if (giftCode.getMaxUses() != -1) {
            int remaining = giftCode.getUsesRemaining();
            if (remaining > 0) {
                player.sendMessage(prefix + "§7Uses remaining: §e" + remaining);
            } else {
                player.sendMessage(prefix + "§7This was the last use of this code!");
            }
        }
        
        return true;
    }
}
