package com.boxpvp.core.data;

import com.boxpvp.core.BoxPvPCore;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class AuctionManager {
    
    private final BoxPvPCore plugin;
    private final File auctionsFile;
    private FileConfiguration auctionsConfig;
    private final Map<UUID, AuctionListing> listings;
    
    public AuctionManager(BoxPvPCore plugin) {
        this.plugin = plugin;
        this.auctionsFile = new File(plugin.getDataFolder(), "auctions.yml");
        this.listings = new HashMap<>();
        
        loadAuctions();
    }
    
    private void loadAuctions() {
        if (!auctionsFile.exists()) {
            try {
                auctionsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create auctions.yml file!");
                e.printStackTrace();
            }
        }
        
        auctionsConfig = YamlConfiguration.loadConfiguration(auctionsFile);
        
        ConfigurationSection listingsSection = auctionsConfig.getConfigurationSection("listings");
        if (listingsSection != null) {
            for (String listingIdStr : listingsSection.getKeys(false)) {
                try {
                    UUID listingId = UUID.fromString(listingIdStr);
                    String path = "listings." + listingIdStr + ".";
                    
                    UUID sellerUuid = UUID.fromString(auctionsConfig.getString(path + "seller-uuid"));
                    String sellerName = auctionsConfig.getString(path + "seller-name");
                    ItemStack item = auctionsConfig.getItemStack(path + "item");
                    double price = auctionsConfig.getDouble(path + "price");
                    CurrencyType currencyType = CurrencyType.fromString(auctionsConfig.getString(path + "currency"));
                    long listDate = auctionsConfig.getLong(path + "list-date");
                    
                    if (item != null) {
                        AuctionListing listing = new AuctionListing(listingId, sellerUuid, sellerName, item, price, currencyType, listDate);
                        listings.put(listingId, listing);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load auction listing: " + listingIdStr);
                }
            }
        }
        
        plugin.getLogger().info("Loaded " + listings.size() + " auction listings");
    }
    
    public void addListing(AuctionListing listing) {
        listings.put(listing.getListingId(), listing);
        saveListing(listing);
    }
    
    public void removeListing(UUID listingId) {
        listings.remove(listingId);
        auctionsConfig.set("listings." + listingId.toString(), null);
        saveAuctions();
    }
    
    public AuctionListing getListing(UUID listingId) {
        return listings.get(listingId);
    }
    
    public List<AuctionListing> getAllListings() {
        return new ArrayList<>(listings.values());
    }
    
    public List<AuctionListing> getListingsByType(CurrencyType type) {
        return listings.values().stream()
            .filter(listing -> listing.getCurrencyType() == type)
            .collect(Collectors.toList());
    }
    
    public List<AuctionListing> getListingsBySeller(UUID sellerUuid) {
        return listings.values().stream()
            .filter(listing -> listing.getSellerUuid().equals(sellerUuid))
            .collect(Collectors.toList());
    }
    
    private void saveListing(AuctionListing listing) {
        String path = "listings." + listing.getListingId().toString() + ".";
        auctionsConfig.set(path + "seller-uuid", listing.getSellerUuid().toString());
        auctionsConfig.set(path + "seller-name", listing.getSellerName());
        auctionsConfig.set(path + "item", listing.getItem());
        auctionsConfig.set(path + "price", listing.getPrice());
        auctionsConfig.set(path + "currency", listing.getCurrencyType().name());
        auctionsConfig.set(path + "list-date", listing.getListDate());
        
        saveAuctions();
    }
    
    private void saveAuctions() {
        try {
            auctionsConfig.save(auctionsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save auctions!");
            e.printStackTrace();
        }
    }
    
    public void saveAll() {
        saveAuctions();
    }
}
