package com.github.theprogmatheus.mc.hunters.hskills.api.impl;

import com.github.theprogmatheus.mc.hunters.hskills.api.PlayerData;
import com.github.theprogmatheus.mc.hunters.hskills.database.repository.PlayerDataRepository;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PlayerDataCache extends LinkedHashMap<UUID, PlayerData> {

    private final int capacity;
    private final PlayerDataRepository repository;

    public PlayerDataCache(int capacity, PlayerDataRepository repository) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
        this.repository = repository;
    }

    @Override
    public PlayerData get(Object key) {
        PlayerData player = super.get(key);
        if (player == null && key instanceof UUID uuid) {
            if ((player = loadFromDatabase(uuid)) != null)
                super.put(uuid, player);
        }
        return player;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<UUID, PlayerData> eldest) {
        return size() > capacity;
    }

    private PlayerData loadFromDatabase(UUID id) {
        return this.repository.findById(id)
                .map(entity -> new PlayerDataImpl(
                        id,
                        level -> 100.0 * level * Math.log(level + 1),
                        entity.skillLevels(),
                        entity.exp(),
                        entity.level(),
                        entity.upgradePoints()
                ))
                .orElse(null);
    }
}
