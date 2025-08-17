package com.github.theprogmatheus.mc.hunters.hskills.database.repository;

import com.github.theprogmatheus.mc.hunters.hskills.database.entity.PlayerDataEntity;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PlayerDataRepository {

    void createTables();

    Optional<PlayerDataEntity> findById(UUID id);

    boolean save(PlayerDataEntity entity);

    boolean save(Collection<PlayerDataEntity> entities);

}
