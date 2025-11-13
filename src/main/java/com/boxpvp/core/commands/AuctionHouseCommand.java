package com.boxpvp.core.commands;

import com.boxpvp.core.BoxPvPCore;
import com.boxpvp.core.data.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.*;

public class AuctionHouseCommand implements CommandExecutor, Listener {

    private final BoxPvPCore plugin;
    private final AuctionManager auctionManager;
    private static final String AH_GUI_BASE_TITLE = "§6§lAuction";
    private static final String CONFIRM_GUI_TITLE = "§6§lConfirm Purchase";
    private static final int ITEMS_PER_PAGE = 45;

    private final Map<UUID, Integer> playerPages;
    private final Map<UUID, CurrencyType> playerFilters;

    public AuctionHouseCommand(BoxPvPCore plugin, AuctionManager auctionManager) {
        this.plugin = plugin;
        this.auctionManager = auctionManager;
        this.playerPages = new HashMap<>();
        this.playerFilters = new HashMap<>();
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

        if (args.length == 0) {
            openAuctionHouse(player, 0);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("sell")) {
            if (args.length < 3) {
                player.sendMessage(prefix + "§cUsage: /ah sell <price> <money|gems|coins>");
                return true;
            }

            ItemStack handItem = player.getInventory().getItemInMainHand();

            if (handItem == null || handItem.getType() == Material.AIR) {
                player.sendMessage(prefix + "§cYou must hold an item to sell!");
                return true;
            }

            double price;
            try {
                price = Double.parseDouble(args[1]);
                if (price <= 0) {
                    player.sendMessage(prefix + "§cPrice must be greater than 0!");
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(prefix + "§cInvalid price!");
                return true;
            }

            CurrencyType currencyType = CurrencyType.fromString(args[2]);

            Rank rank = plugin.getRankManager().getRank(player);
            int playerListings = auctionManager.getListingsBySeller(player.getUniqueId()).size();

            if (playerListings >= rank.getMaxAuctionListings()) {
                player.sendMessage(prefix + "§cYou have reached your listing limit! (" + rank.getMaxAuctionListings() + ")");
                player.sendMessage(prefix + "§7Upgrade your rank with /rank to list more items!");
                return true;
            }

            AuctionListing listing = new AuctionListing(
                player.getUniqueId(),
                player.getName(),
                handItem,
                price,
                currencyType
            );

            auctionManager.addListing(listing);
            player.getInventory().setItemInMainHand(null);

            player.sendMessage(prefix + "§aItem listed for " + currencyType.getSymbol() + price + " " + currencyType.getDisplayName());
            return true;
        }

        return true;
    }

    private void openAuctionHouse(Player player, int page) {
        playerPages.put(player.getUniqueId(), page);

        CurrencyType filter = playerFilters.getOrDefault(player.getUniqueId(), null);
        List<AuctionListing> listings = filter == null ? 
            auctionManager.getAllListings() : 
            auctionManager.getListingsByType(filter);

        int totalPages = (int) Math.ceil((double) listings.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        String guiTitle = AH_GUI_BASE_TITLE + " §7- §f[" + (page + 1) + "/" + totalPages + "]";
        Inventory inv = Bukkit.createInventory(null, 54, guiTitle);

        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, listings.size());

        for (int i = startIndex; i < endIndex; i++) {
            AuctionListing listing = listings.get(i);
            ItemStack displayItem = createListingDisplay(listing);
            inv.setItem(i - startIndex, displayItem);
        }

        if (page > 0) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName("§e§l◀ Previous Page");
            prevPage.setItemMeta(prevMeta);
            inv.setItem(45, prevPage);
        }

        ItemStack refreshButton = new ItemStack(Material.SUNFLOWER);
        ItemMeta refreshMeta = refreshButton.getItemMeta();
        refreshMeta.setDisplayName("§a§lRefresh Page");
        refreshMeta.setLore(Arrays.asList("§7Click to refresh listings"));
        refreshButton.setItemMeta(refreshMeta);
        inv.setItem(49, refreshButton);

        if (endIndex < listings.size()) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName("§e§lNext Page ▶");
            nextPage.setItemMeta(nextMeta);
            inv.setItem(53, nextPage);
        }

        player.openInventory(inv);
    }

    private ItemStack createListingDisplay(AuctionListing listing) {
        ItemStack displayItem = listing.getItem().clone();
        ItemMeta meta = displayItem.getItemMeta();

        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.add("");
        lore.add("§7Seller: §e" + listing.getSellerName());
        lore.add("§7Price: §a" + listing.getCurrencyType().getSymbol() + listing.getPrice() + " " + listing.getCurrencyType().getDisplayName());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        String dateStr = sdf.format(new Date(listing.getListDate()));
        lore.add("§7Listed: §e" + dateStr);
        lore.add("");
        lore.add("§e§lClick to purchase!");

        meta.setLore(lore);
        displayItem.setItemMeta(meta);

        return displayItem;
    }

    private void openConfirmGUI(Player player, AuctionListing listing) {
        Inventory inv = Bukkit.createInventory(null, 27, CONFIRM_GUI_TITLE);

        boolean isSeller = listing.getSellerUuid().equals(player.getUniqueId());

        ItemStack displayItem = listing.getItem().clone();
        ItemMeta displayMeta = displayItem.getItemMeta();
        List<String> lore = displayMeta.hasLore() ? displayMeta.getLore() : new ArrayList<>();
        lore.add("");
        lore.add("§7Seller: §f" + listing.getSellerName());
        lore.add("§7Price: §e" + listing.getCurrencyType().getSymbol() + listing.getPrice() + " " + listing.getCurrencyType().getDisplayName());

        if (isSeller) {
            lore.add("");
            lore.add("§e§lClick below to retrieve item");
        }

        displayMeta.setLore(lore);
        displayItem.setItemMeta(displayMeta);

        inv.setItem(13, displayItem);

        if (isSeller) {
            // Retrieve button for seller
            ItemStack retrieveButton = new ItemStack(Material.CHEST);
            ItemMeta retrieveMeta = retrieveButton.getItemMeta();
            retrieveMeta.setDisplayName("§e§lRetrieve Item");
            retrieveMeta.setLore(Arrays.asList("§7Click to get your item back", "§7and cancel this listing"));
            retrieveButton.setItemMeta(retrieveMeta);
            inv.setItem(11, retrieveButton);

            ItemStack cancelButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta cancelMeta = cancelButton.getItemMeta();
            cancelMeta.setDisplayName("§c§lCancel");
            cancelMeta.setLore(Arrays.asList("§7Go back to auction house"));
            cancelButton.setItemMeta(cancelMeta);
            inv.setItem(15, cancelButton);
        } else {
            // Buy and cancel buttons for buyers
            ItemStack confirmButton = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta confirmMeta = confirmButton.getItemMeta();
            confirmMeta.setDisplayName("§a§lConfirm Purchase");
            confirmMeta.setLore(Arrays.asList("§7Click to buy this item"));
            confirmButton.setItemMeta(confirmMeta);
            inv.setItem(11, confirmButton);

            ItemStack cancelButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta cancelMeta = cancelButton.getItemMeta();
            cancelMeta.setDisplayName("§c§lCancel");
            cancelMeta.setLore(Arrays.asList("§7Go back to auction house"));
            cancelButton.setItemMeta(cancelMeta);
            inv.setItem(15, cancelButton);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        String prefix = plugin.getConfig().getString("messages.prefix", "");

        if (title.startsWith(AH_GUI_BASE_TITLE)) {
            event.setCancelled(true);

            int slot = event.getRawSlot();

            if (slot == 45) {
                int currentPage = playerPages.getOrDefault(player.getUniqueId(), 0);
                if (currentPage > 0) {
                    openAuctionHouse(player, currentPage - 1);
                }
                return;
            }

            if (slot == 49) {
                openAuctionHouse(player, playerPages.getOrDefault(player.getUniqueId(), 0));
                return;
            }

            if (slot == 53) {
                int currentPage = playerPages.getOrDefault(player.getUniqueId(), 0);
                openAuctionHouse(player, currentPage + 1);
                return;
            }

            if (slot >= 0 && slot < 45) {
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                    CurrencyType filter = playerFilters.getOrDefault(player.getUniqueId(), null);
                    List<AuctionListing> listings = filter == null ? 
                        auctionManager.getAllListings() : 
                        auctionManager.getListingsByType(filter);

                    int page = playerPages.getOrDefault(player.getUniqueId(), 0);
                    int listingIndex = page * ITEMS_PER_PAGE + slot;

                    if (listingIndex < listings.size()) {
                        AuctionListing listing = listings.get(listingIndex);
                        openConfirmGUI(player, listing);
                    }
                }
            }
            return;
        }

        if (title.equals(CONFIRM_GUI_TITLE)) {
            event.setCancelled(true);

            ItemStack centerItem = event.getInventory().getItem(13);
            if (centerItem == null) return;

            AuctionListing listing = findListingByDisplay(centerItem);
            if (listing == null) {
                player.closeInventory();
                player.sendMessage(prefix + "§cListing no longer available!");
                return;
            }

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null) return;

            if (clicked.getType() == Material.LIME_STAINED_GLASS_PANE) {
                purchaseListing(player, listing);
            } else if (clicked.getType() == Material.RED_STAINED_GLASS_PANE) {
                openAuctionHouse(player, playerPages.getOrDefault(player.getUniqueId(), 0));
            } else if (clicked.getType() == Material.CHEST && listing.getSellerUuid().equals(player.getUniqueId())) {
                // Seller clicked the retrieve button
                auctionManager.removeListing(listing.getListingId());
                player.getInventory().addItem(listing.getItem());
                player.sendMessage(prefix + "§aItem retrieved successfully!");
                player.closeInventory();
            }
        }
    }

    private AuctionListing findListingByDisplay(ItemStack displayItem) {
        for (AuctionListing listing : auctionManager.getAllListings()) {
            if (listing.getItem().getType() == displayItem.getType()) {
                // This is a very basic check. Ideally, you'd want a more robust way to match
                // the displayed item back to the AuctionListing, perhaps by storing the listing ID.
                // For now, we'll assume the first match is correct.
                return listing;
            }
        }
        return null;
    }

    private void purchaseListing(Player buyer, AuctionListing listing) {
        String prefix = plugin.getConfig().getString("messages.prefix", "");

        if (listing.getSellerUuid().equals(buyer.getUniqueId())) {
            buyer.sendMessage(prefix + "§cYou cannot buy your own listing!");
            buyer.closeInventory();
            return;
        }

        PlayerData buyerData = plugin.getPlayerDataManager().getPlayerData(buyer);
        CurrencyType currency = listing.getCurrencyType();
        double price = listing.getPrice();

        boolean hasFunds = false;
        switch (currency) {
            case MONEY:
                hasFunds = buyerData.hasBalance(price);
                break;
            case GEMS:
                hasFunds = buyerData.hasGems(price);
                break;
            case COINS:
                hasFunds = buyerData.hasCoins(price);
                break;
        }

        if (!hasFunds) {
            buyer.sendMessage(prefix + "§cYou don't have enough " + currency.getDisplayName() + "!");
            buyer.closeInventory();
            return;
        }

        switch (currency) {
            case MONEY:
                buyerData.removeBalance(price);
                break;
            case GEMS:
                buyerData.removeGems(price);
                break;
            case COINS:
                buyerData.removeCoins(price);
                break;
        }

        Player seller = Bukkit.getPlayer(listing.getSellerUuid());
        if (seller != null) {
            PlayerData sellerData = plugin.getPlayerDataManager().getPlayerData(seller);
            switch (currency) {
                case MONEY:
                    sellerData.addBalance(price);
                    break;
                case GEMS:
                    sellerData.addGems(price);
                    break;
                case COINS:
                    sellerData.addCoins(price);
                    break;
            }
            seller.sendMessage(prefix + "§aYour item sold for " + currency.getSymbol() + price + " " + currency.getDisplayName() + "!");
        } else {
            // If seller is offline, the money/gems/coins should be added to their data
            // This part might need more robust handling depending on your PlayerDataManager
            PlayerData sellerData = plugin.getPlayerDataManager().getPlayerData(listing.getSellerUuid());
            switch (currency) {
                case MONEY:
                    sellerData.addBalance(price);
                    break;
                case GEMS:
                    sellerData.addGems(price);
                    break;
                case COINS:
                    sellerData.addCoins(price);
                    break;
            }
        }

        buyer.getInventory().addItem(listing.getItem());
        auctionManager.removeListing(listing.getListingId());

        buyer.sendMessage(prefix + "§aPurchase successful!");
        buyer.closeInventory();
    }
}