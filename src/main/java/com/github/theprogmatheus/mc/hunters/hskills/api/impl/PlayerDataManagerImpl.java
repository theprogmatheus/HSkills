package com.github.theprogmatheus.mc.hunters.hskills.api.impl;

import com.github.theprogmatheus.mc.hunters.hskills.api.PlayerData;
import com.github.theprogmatheus.mc.hunters.hskills.api.PlayerDataManager;
import com.github.theprogmatheus.mc.hunters.hskills.database.entity.PlayerDataEntity;
import com.github.theprogmatheus.mc.hunters.hskills.database.repository.PlayerDataRepository;
import com.github.theprogmatheus.mc.hunters.hskills.service.DatabaseSQLService;
import com.github.theprogmatheus.mc.hunters.hskills.service.MainService;
import com.github.theprogmatheus.mc.hunters.hskills.util.WriteBehindBuffer;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.*;

@Getter
public class PlayerDataManagerImpl implements PlayerDataManager {

    private final Plugin plugin;
    private final PlayerDataRepository playerDataRepository;
    private final PlayerDataCache playerDataCache;
    private final WriteBehindBuffer<UUID, PlayerDataImpl> writeBehindBuffer;

    public PlayerDataManagerImpl(Plugin plugin) {
        this.plugin = plugin;
        this.playerDataCache = new PlayerDataCache(
                2048,
                this.playerDataRepository = MainService.getService(DatabaseSQLService.class)
                        .getPlayerDataRepository(),
                this.writeBehindBuffer = new WriteBehindBuffer<>(
                        this.plugin.getLogger(),
                        30,
                        10,
                        2048,
                        this::flush
                )
        );
    }

    private Boolean flush(Map<UUID, WriteBehindBuffer.FlushAction<PlayerDataImpl>> map) {
        List<PlayerDataEntity> entities = map.values().stream()
                .map(action -> action instanceof WriteBehindBuffer.FlushAction.Put<PlayerDataImpl> put ?
                        put.value() : null)
                .filter(Objects::nonNull)
                .map(playerData -> new PlayerDataEntity(
                        playerData.getId(),
                        playerData.getSkillLevels(),
                        playerData.getLevel(),
                        playerData.getExp(),
                        playerData.getUpgradePoints()
                )).toList();
        return this.playerDataRepository.save(entities);
    }

    @Override
    public PlayerData getPlayerData(UUID id) {
        return this.playerDataCache.get(id);
    }

    @Override
    public PlayerData getOrCreatePlayerData(UUID id) {
        PlayerDataImpl playerData = this.playerDataCache.get(id);
        if (playerData != null)
            return playerData;


        playerData = new PlayerDataImpl(
                id,
                level -> 50 * Math.pow(level, 2) + 500 * level + 1000,
                new HashMap<>(),
                0,
                0,
                0,
                this.writeBehindBuffer
        );

        this.playerDataCache.put(id, playerData);
        return playerData;
    }
}
