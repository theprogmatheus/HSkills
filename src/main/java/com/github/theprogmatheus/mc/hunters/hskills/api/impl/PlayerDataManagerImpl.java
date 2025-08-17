package com.github.theprogmatheus.mc.hunters.hskills.api.impl;

import com.github.theprogmatheus.mc.hunters.hskills.api.PlayerData;
import com.github.theprogmatheus.mc.hunters.hskills.api.PlayerDataManager;
import com.github.theprogmatheus.mc.hunters.hskills.database.repository.PlayerDataRepository;
import com.github.theprogmatheus.mc.hunters.hskills.service.DatabaseSQLService;
import com.github.theprogmatheus.mc.hunters.hskills.service.MainService;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PlayerDataManagerImpl implements PlayerDataManager {

    private final PlayerDataRepository playerDataRepository;
    private final PlayerDataCache playerDataCache;

    public PlayerDataManagerImpl() {
        this.playerDataCache = new PlayerDataCache(
                2048,
                this.playerDataRepository = MainService.getService(DatabaseSQLService.class)
                        .getPlayerDataRepository()
        );
    }

    @Override
    public PlayerData getPlayerData(UUID id) {
        return this.playerDataCache.get(id);
    }
}
