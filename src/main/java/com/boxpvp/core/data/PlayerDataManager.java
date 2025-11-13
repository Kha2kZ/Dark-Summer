package com.boxpvp.core.data;

import com.boxpvp.core.BoxPvPCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final BoxPvPCore plugin;
    private final File dataFolder;
    private final Map<UUID, PlayerData> playerDataCache;

    public PlayerDataManager(BoxPvPCore plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        this.playerDataCache = new HashMap<>();

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    public PlayerData getPlayerData(UUID uuid) {
        if (playerDataCache.containsKey(uuid)) {
            return playerDataCache.get(uuid);
        }

        PlayerData data = loadPlayerData(uuid);
        playerDataCache.put(uuid, data);
        return data;
    }

    private PlayerData loadPlayerData(UUID uuid) {
        File file = new File(dataFolder, uuid.toString() + ".yml");

        if (!file.exists()) {
            return new PlayerData(uuid, plugin.getConfig().getInt("economy.starting-balance", 100));
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        int kills = config.getInt("kills", 0);
        int deaths = config.getInt("deaths", 0);
        double balance = config.getDouble("balance", plugin.getConfig().getInt("economy.starting-balance", 100));
        double gems = config.getDouble("gems", 0);
        double coins = config.getDouble("coins", 0);
        long lastLogin = config.getLong("last-login", System.currentTimeMillis());
        Rank rank = Rank.fromString(config.getString("rank", "MEMBER"));
        String nickname = config.getString("nickname", null);

        return new PlayerData(uuid, kills, deaths, balance, gems, coins, lastLogin, rank, nickname);
    }

    public void savePlayerData(UUID uuid) {
        PlayerData data = playerDataCache.get(uuid);
        if (data == null) return;
        File file = new File(dataFolder, uuid.toString() + ".yml");

        FileConfiguration config = new YamlConfiguration();
        config.set("kills", data.getKills());
        config.set("deaths", data.getDeaths());
        config.set("balance", data.getBalance());
        config.set("gems", data.getGems());
        config.set("coins", data.getCoins());
        config.set("last-login", data.getLastLogin());
        config.set("rank", data.getRank().name());
        config.set("nickname", data.getNickname());

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save player data for " + uuid);
            e.printStackTrace();
        }
    }

    public void saveAllData() {
        for (UUID uuid : playerDataCache.keySet()) {
            savePlayerData(uuid);
        }
    }

    public void unloadPlayerData(UUID uuid) {
        savePlayerData(uuid);
        playerDataCache.remove(uuid);
    }
}