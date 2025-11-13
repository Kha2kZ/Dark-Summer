package com.boxpvp.core.data;

import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private int kills;
    private int deaths;
    private double balance;
    private double gems;
    private double coins;
    private long lastLogin;
    private Rank rank;
    private String nickname;

    public PlayerData(UUID uuid, double startingBalance) {
        this(uuid, 0, 0, startingBalance, 0, 0, System.currentTimeMillis(), Rank.MEMBER, null);
    }

    public PlayerData(UUID uuid, int kills, int deaths, double balance, double gems, double coins, long lastLogin, Rank rank, String nickname) {
        this.uuid = uuid;
        this.kills = kills;
        this.deaths = deaths;
        this.balance = balance;
        this.gems = gems;
        this.coins = coins;
        this.lastLogin = lastLogin;
        this.rank = rank != null ? rank : Rank.MEMBER;
        this.nickname = nickname;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void addKill() {
        this.kills++;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void addDeath() {
        this.deaths++;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void addBalance(double amount) {
        this.balance += amount;
    }

    public void removeBalance(double amount) {
        this.balance -= amount;
    }

    public boolean hasBalance(double amount) {
        return this.balance >= amount;
    }

    public double getGems() {
        return gems;
    }

    public void setGems(double gems) {
        this.gems = gems;
    }

    public void addGems(double amount) {
        this.gems += amount;
    }

    public void removeGems(double amount) {
        this.gems -= amount;
    }

    public boolean hasGems(double amount) {
        return this.gems >= amount;
    }

    public double getCoins() {
        return coins;
    }

    public void setCoins(double coins) {
        this.coins = coins;
    }

    public void addCoins(double amount) {
        this.coins += amount;
    }

    public void removeCoins(double amount) {
        this.coins -= amount;
    }

    public boolean hasCoins(double amount) {
        return this.coins >= amount;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public double getKDRatio() {
        if (deaths == 0) {
            return kills;
        }
        return (double) kills / deaths;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean hasNickname() {
        return nickname != null && !nickname.isEmpty();
    }

    public double getMoney() {
        return this.balance;
    }

    public void addMoney(double amount) {
        this.balance += amount;
    }

    public void removeMoney(double amount) {
        this.balance -= amount;
    }
}