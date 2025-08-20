package com.github.theprogmatheus.mc.hunters.hskills.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface PlayerDataManager {

    PlayerData getPlayerData(UUID id);

    default PlayerData getPlayerData(OfflinePlayer player) {
        return getPlayerData(player.getUniqueId());
    }

    PlayerData getOrCreatePlayerData(UUID id);

    default PlayerData getOrCreatePlayerData(Player player) {
        return getOrCreatePlayerData(player.getUniqueId());
    }

    PlayerData createPlayerData(UUID id);

    default PlayerData createPlayerData(Player player) {
        return createPlayerData(player.getUniqueId());
    }

}