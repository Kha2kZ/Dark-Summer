package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.trade.NPCTrade;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NPCCommand implements CommandExecutor {
    
    private final BoxPvPCore plugin;
    private final Map<UUID, ConfigMode> configSessions = new HashMap<>();

    public NPCCommand(BoxPvPCore plugin) {
        this.plugin = plugin;
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
            player.sendMessage("§cUsage: /npc <create|addtrade> <name>");
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 2) {
                player.sendMessage("§cUsage: /npc create <name>");
                return true;
            }

            String name = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
            NPCTrade npc = plugin.getNPCTradeManager().createNPCTrader(player.getLocation(), name);

            player.sendMessage("§aCreated NPC trader: " + name);
            player.sendMessage("§7Use §e/npc addtrade <npc_name> §7to configure trades");
        } else if (args[0].equalsIgnoreCase("addtrade")) {
            if (args.length < 2) {
                player.sendMessage("§cUsage: /npc addtrade <npc_name>");
                return true;
            }

            String npcName = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
            NPCTrade npc = plugin.getNPCTradeManager().getNPCByName(npcName);

            if (npc == null) {
                player.sendMessage("§cNPC trader not found: " + npcName);
                return true;
            }

            configSessions.put(player.getUniqueId(), new ConfigMode(npc));
            player.sendMessage("§a§lTrade Configuration Mode Activated!");
            player.sendMessage("§7Step 1: Place §einput items §7in your §ehotbar (slots 1-9)");
            player.sendMessage("§7Step 2: Type §e/npc setinput §7to save inputs");
            player.sendMessage("§7Step 3: Hold the §eoutput item §7in your hand");
            player.sendMessage("§7Step 4: Type §e/npc setoutput §7to complete");
            player.sendMessage("§7Type §e/npc cancel §7to cancel");
        } else if (args[0].equalsIgnoreCase("setinput")) {
            ConfigMode mode = configSessions.get(player.getUniqueId());
            if (mode == null) {
                player.sendMessage("§cYou are not in config mode! Use §e/npc addtrade <name>");
                return true;
            }

            mode.inputs.clear();
            for (int i = 0; i < 9; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    mode.inputs.add(item.clone());
                }
            }

            if (mode.inputs.isEmpty()) {
                player.sendMessage("§cNo items found in your hotbar!");
                return true;
            }

            player.sendMessage("§aSaved " + mode.inputs.size() + " input items from hotbar");
            player.sendMessage("§7Now hold the output item and type §e/npc setoutput");
        } else if (args[0].equalsIgnoreCase("setoutput")) {
            ConfigMode mode = configSessions.get(player.getUniqueId());
            if (mode == null) {
                player.sendMessage("§cYou are not in config mode!");
                return true;
            }

            ItemStack held = player.getInventory().getItemInMainHand();
            if (held == null || held.getType() == Material.AIR) {
                player.sendMessage("§cYou must hold an item in your hand!");
                return true;
            }

            plugin.getNPCTradeManager().addTradeRecipe(mode.npc, mode.inputs, held.clone());
            player.sendMessage("§aTrade recipe added successfully!");
            configSessions.remove(player.getUniqueId());
        } else if (args[0].equalsIgnoreCase("cancel")) {
            configSessions.remove(player.getUniqueId());
            player.sendMessage("§cConfig mode cancelled");
        }

        return true;
    }

    private static class ConfigMode {
        NPCTrade npc;
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>();

        ConfigMode(NPCTrade npc) {
            this.npc = npc;
        }
    }
}
