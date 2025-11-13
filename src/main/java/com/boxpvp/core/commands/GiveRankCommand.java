package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.PlayerData;
import com.boxpvp.core.data.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveRankCommand implements CommandExecutor {

    private final BoxPvPCore plugin;

    public GiveRankCommand(BoxPvPCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = plugin.getConfig().getString("messages.prefix", "");

        if (!sender.hasPermission("boxpvp.admin")) {
            sender.sendMessage(prefix + "§cBạn không có quyền sử dụng lệnh này!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(prefix + "§cSử dụng: /giverank <player> <rank>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(prefix + "§cKhông tìm thấy người chơi!");
            return true;
        }

        Rank rank;
        try {
            rank = Rank.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(prefix + "§cRank không hợp lệ!");
            return true;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);
        data.setRank(rank);
        plugin.getPlayerDataManager().savePlayerData(target.getUniqueId());

        target.setPlayerListName(rank.getColor() + "[" + rank.getDisplayName() + "] §f" + target.getName());

        sender.sendMessage(prefix + "§aĐã set rank " + rank.getColor() + rank.getDisplayName() + "§a cho " + target.getName());
        target.sendMessage(prefix + "§aBạn đã nhận rank " + rank.getColor() + rank.getDisplayName());

        return true;
    }
}