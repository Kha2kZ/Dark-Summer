package com.boxpvp.core.data;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class AuctionListing {
    
    private final UUID listingId;
    private final UUID sellerUuid;
    private final String sellerName;
    private final ItemStack item;
    private final double price;
    private final CurrencyType currencyType;
    private final long listDate;
    
    public AuctionListing(UUID sellerUuid, String sellerName, ItemStack item, double price, CurrencyType currencyType) {
        this.listingId = UUID.randomUUID();
        this.sellerUuid = sellerUuid;
        this.sellerName = sellerName;
        this.item = item.clone();
        this.price = price;
        this.currencyType = currencyType;
        this.listDate = System.currentTimeMillis();
    }
    
    public AuctionListing(UUID listingId, UUID sellerUuid, String sellerName, ItemStack item, double price, CurrencyType currencyType, long listDate) {
        this.listingId = listingId;
        this.sellerUuid = sellerUuid;
        this.sellerName = sellerName;
        this.item = item.clone();
        this.price = price;
        this.currencyType = currencyType;
        this.listDate = listDate;
    }
    
    public UUID getListingId() {
        return listingId;
    }
    
    public UUID getSellerUuid() {
        return sellerUuid;
    }
    
    public String getSellerName() {
        return sellerName;
    }
    
    public ItemStack getItem() {
        return item.clone();
    }
    
    public double getPrice() {
        return price;
    }
    
    public CurrencyType getCurrencyType() {
        return currencyType;
    }
    
    public long getListDate() {
        return listDate;
    }
}
