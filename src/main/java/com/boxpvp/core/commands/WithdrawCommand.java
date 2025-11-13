
package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.PlayerData;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class WithdrawCommand implements CommandExecutor {
    
    private final BoxPvPCore plugin;
    
    public WithdrawCommand(BoxPvPCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cChỉ người chơi mới có thể sử dụng lệnh này!");
            return true;
        }
        
        Player player = (Player) sender;
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        
        if (args.length < 2) {
            player.sendMessage(prefix + "§cSử dụng: /withdraw <money> <amount>");
            return true;
        }
        
        if (!args[0].equalsIgnoreCase("money")) {
            player.sendMessage(prefix + "§cHiện tại chỉ hỗ trợ rút money!");
            return true;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
            if (amount <= 0) {
                player.sendMessage(prefix + "§cSố tiền phải lớn hơn 0!");
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(prefix + "§cSố tiền không hợp lệ!");
            return true;
        }
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data.getMoney() < amount) {
            player.sendMessage(prefix + "§cBạn không đủ tiền!");
            return true;
        }
        
        data.removeMoney(amount);
        plugin.getPlayerDataManager().savePlayerData(player);
        
        ItemStack voucher = new ItemStack(Material.PAPER);
        ItemMeta meta = voucher.getItemMeta();
        meta.setDisplayName("§a§lVOUCHER MONEY " + String.format("%,.0f", amount));
        meta.setLore(Arrays.asList(
            "",
            "§7Bấm chuột phải để nhận tiền",
            "§eGiá trị: §f" + String.format("%,.2f", amount) + " coins"
        ));
        voucher.setItemMeta(meta);
        
        player.getInventory().addItem(voucher);
        player.sendMessage(prefix + "§aĐã rút §e" + String.format("%,.2f", amount) + " coins §athành voucher!");
        
        return true;
    }
}
