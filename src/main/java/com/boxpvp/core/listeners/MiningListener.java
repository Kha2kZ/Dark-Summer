package com.boxpvp.core.listeners;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.Rank;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class MiningListener implements Listener {
    
    private final BoxPvPCore plugin;
    
    public MiningListener(BoxPvPCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        if (!isMineableBlock(block.getType())) {
            return;
        }
        
        Rank rank = plugin.getRankManager().getRank(player);
        double multiplier = rank.getMiningMultiplier();
        
        if (multiplier <= 1.0) {
            return;
        }
        
        Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());
        
        if (drops.isEmpty()) {
            return;
        }
        
        event.setDropItems(false);
        
        for (ItemStack drop : drops) {
            int amount = drop.getAmount();
            int bonusAmount = (int) (amount * (multiplier - 1));
            
            if (bonusAmount > 0) {
                ItemStack bonus = drop.clone();
                bonus.setAmount(bonusAmount);
                block.getWorld().dropItemNaturally(block.getLocation(), bonus);
            }
            
            block.getWorld().dropItemNaturally(block.getLocation(), drop);
        }
    }
    
    private boolean isMineableBlock(Material material) {
        switch (material) {
            case COAL_ORE:
            case DEEPSLATE_COAL_ORE:
            case IRON_ORE:
            case DEEPSLATE_IRON_ORE:
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE:
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE:
            case EMERALD_ORE:
            case DEEPSLATE_EMERALD_ORE:
            case LAPIS_ORE:
            case DEEPSLATE_LAPIS_ORE:
            case REDSTONE_ORE:
            case DEEPSLATE_REDSTONE_ORE:
            case COPPER_ORE:
            case DEEPSLATE_COPPER_ORE:
            case NETHER_GOLD_ORE:
            case NETHER_QUARTZ_ORE:
            case ANCIENT_DEBRIS:
                return true;
            default:
                return false;
        }
    }
}
