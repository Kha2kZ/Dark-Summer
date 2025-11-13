package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final BoxPvPCore plugin;

    public PayCommand(BoxPvPCore plugin) {
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
            player.sendMessage(prefix + "§cSử dụng: /pay <player> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(prefix + "§cKhông tìm thấy người chơi!");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(prefix + "§cBạn không thể chuyển tiền cho chính mình!");
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

        PlayerData senderData = plugin.getPlayerDataManager().getPlayerData(player);
        if (senderData.getMoney() < amount) {
            player.sendMessage(prefix + "§cBạn không đủ tiền!");
            return true;
        }

        PlayerData targetData = plugin.getPlayerDataManager().getPlayerData(target);
        senderData.removeMoney(amount);
        targetData.addMoney(amount);

        plugin.getPlayerDataManager().savePlayerData(player.getUniqueId());
        plugin.getPlayerDataManager().savePlayerData(target.getUniqueId());

        player.sendMessage(prefix + "§aĐã chuyển §e" + String.format("%.2f", amount) + " coins §acho " + target.getName());
        target.sendMessage(prefix + "§aBạn đã nhận §e" + String.format("%.2f", amount) + " coins §atừ " + player.getName());

        return true;
    }
}