
package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.PlayerData;
import com.boxpvp.core.data.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class TopCommand implements CommandExecutor {
    
    private final BoxPvPCore plugin;
    
    public TopCommand(BoxPvPCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        
        if (args.length == 0) {
            sender.sendMessage(prefix + "§cSử dụng: /top <money|gem|coin>");
            return true;
        }
        
        String type = args[0].toLowerCase();
        List<Map.Entry<String, Double>> topList = new ArrayList<>();
        
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(p);
            Rank rank = data.getRank();
            
            // Chỉ hiển thị player có rank từ MEMBER đến CUSTOM
            if (rank.ordinal() >= Rank.MEMBER.ordinal() && rank.ordinal() <= Rank.CUSTOM.ordinal()) {
                double value = 0;
                switch (type) {
                    case "money":
                        value = data.getMoney();
                        break;
                    case "gem":
                    case "gems":
                        value = data.getGems();
                        break;
                    case "coin":
                    case "coins":
                        value = data.getCoins();
                        break;
                    default:
                        sender.sendMessage(prefix + "§cLoại không hợp lệ! Sử dụng: money, gem, hoặc coin");
                        return true;
                }
                topList.add(new AbstractMap.SimpleEntry<>(p.getName(), value));
            }
        }
        
        topList.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        
        sender.sendMessage("§8§m                                ");
        sender.sendMessage("  §6§lTOP 10 " + type.toUpperCase());
        sender.sendMessage("");
        
        int position = 1;
        for (int i = 0; i < Math.min(10, topList.size()); i++) {
            Map.Entry<String, Double> entry = topList.get(i);
            String posColor = position <= 3 ? "§e" : "§7";
            sender.sendMessage("  " + posColor + "#" + position + " §f" + entry.getKey() + " §7- §e" + String.format("%.2f", entry.getValue()));
            position++;
        }
        
        sender.sendMessage("§8§m                                ");
        
        return true;
    }
}
