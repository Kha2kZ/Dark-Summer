
package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PvViewCommand implements CommandExecutor {
    
    private final BoxPvPCore plugin;
    
    public PvViewCommand(BoxPvPCore plugin) {
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
        
        if (!player.hasPermission("boxpvp.admin")) {
            player.sendMessage(prefix + "§cBạn không có quyền sử dụng lệnh này!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(prefix + "§cSử dụng: /pvview <player> <số>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(prefix + "§cKhông tìm thấy người chơi!");
            return true;
        }
        
        int vaultNumber;
        try {
            vaultNumber = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(prefix + "§cSố vault không hợp lệ!");
            return true;
        }
        
        if (vaultNumber < 1 || vaultNumber > 40) {
            player.sendMessage(prefix + "§cSố vault phải từ 1 đến 40!");
            return true;
        }
        
        Inventory vault = plugin.getVirtualChestManager().getVault(target, vaultNumber);
        player.openInventory(vault);
        player.sendMessage(prefix + "§aĐang xem PV #" + vaultNumber + " của " + target.getName());
        
        return true;
    }
}
