package com.github.theprogmatheus.mc.hunters.hskills.api;

import org.bukkit.OfflinePlayer;

import java.util.UUID;

public interface PlayerDataManager {

    PlayerData getPlayerData(UUID id);

    default PlayerData getPlayerData(OfflinePlayer player) {
        return getPlayerData(player.getUniqueId());
    }

}