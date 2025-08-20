package com.github.theprogmatheus.mc.hunters.hskills.api.impl;

import com.github.theprogmatheus.mc.hunters.hskills.database.repository.PlayerDataRepository;
import com.github.theprogmatheus.mc.hunters.hskills.util.WriteBehindBuffer;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PlayerDataCache extends LinkedHashMap<UUID, PlayerDataImpl> {

    public static final int FLUSH_INTERVAL = 600;

    private final int capacity;
    private final PlayerDataRepository repository;
    private final WriteBehindBuffer<UUID, PlayerDataImpl> writeBehindBuffer;

    public PlayerDataCache(int capacity, PlayerDataRepository repository, WriteBehindBuffer<UUID, PlayerDataImpl> writeBehindBuffer) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
        this.repository = repository;
        this.writeBehindBuffer = writeBehindBuffer;
    }

    @Override
    public PlayerDataImpl get(Object key) {
        PlayerDataImpl player = super.get(key);
        if (player == null && key instanceof UUID uuid) {
            if ((player = loadFromDatabase(uuid)) != null)
                super.put(uuid, player);
        }
        return player;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<UUID, PlayerDataImpl> eldest) {
        boolean shouldRemove = size() > capacity;
        if (shouldRemove) {
            eldest.getValue().persist();
        }
        return shouldRemove;
    }

    private PlayerDataImpl loadFromDatabase(UUID id) {
        return this.repository.findById(id)
                .map(entity -> new PlayerDataImpl(
                        id,
                        entity.skillLevels(),
                        entity.exp(),
                        entity.level(),
                        entity.upgradePoints(),
                        this.writeBehindBuffer
                ))
                .orElse(null);
    }
}
