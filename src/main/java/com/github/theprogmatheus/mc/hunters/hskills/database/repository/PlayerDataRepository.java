package com.github.theprogmatheus.mc.hunters.hskills.database.repository;

import com.github.theprogmatheus.mc.hunters.hskills.database.entity.PlayerDataEntity;

import java.util.Optional;
import java.util.UUID;

public interface PlayerDataRepository {

    Optional<PlayerDataEntity> findById(UUID id);

}
