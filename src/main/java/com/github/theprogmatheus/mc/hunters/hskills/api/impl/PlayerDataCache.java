package com.github.theprogmatheus.mc.hunters.hskills.api.impl;

import com.github.theprogmatheus.mc.hunters.hskills.database.entity.PlayerDataEntity;
import com.github.theprogmatheus.mc.hunters.hskills.database.repository.PlayerDataRepository;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class PlayerDataCache extends LinkedHashMap<UUID, PlayerDataImpl> {

    public static final int FLUSH_INTERVAL = 600;

    private final int capacity;
    private final PlayerDataRepository repository;
    private final ScheduledExecutorService executorService;

    public PlayerDataCache(int capacity, PlayerDataRepository repository) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
        this.repository = repository;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.executorService.scheduleWithFixedDelay(this::persistAll, FLUSH_INTERVAL, FLUSH_INTERVAL, TimeUnit.SECONDS);
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
            this.executorService.submit(() -> persistPlayer(eldest.getValue()));
        }
        return shouldRemove;
    }

    private void persistAll() {
        this.repository.save(
                this.values().stream().map(playerData -> new PlayerDataEntity(
                        playerData.getId(),
                        playerData.getSkillLevels(),
                        playerData.getLevel(),
                        playerData.getExp(),
                        playerData.getUpgradePoints()
                )).toList());
    }

    private void persistPlayer(PlayerDataImpl player) {
        PlayerDataEntity entity = new PlayerDataEntity(
                player.getId(),
                player.getSkillLevels(),
                player.getLevel(),
                player.getExp(),
                player.getUpgradePoints()
        );
        this.repository.save(entity);
    }


    private PlayerDataImpl loadFromDatabase(UUID id) {
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

    public void shutdown() {
        try {
            this.executorService.shutdown();
            if (!this.executorService.awaitTermination(30, TimeUnit.SECONDS))
                this.executorService.shutdownNow();
        } catch (InterruptedException ignored) {
            this.executorService.shutdownNow();
        }
        persistAll();
    }

}
