package com.github.theprogmatheus.mc.hunters.hskills.database.repository.impl;

import com.github.theprogmatheus.mc.hunters.hskills.database.entity.PlayerDataEntity;
import com.github.theprogmatheus.mc.hunters.hskills.database.repository.PlayerDataRepository;

import java.util.Optional;
import java.util.UUID;

public class PlayerDataRepositoryImpl implements PlayerDataRepository {

    @Override
    public Optional<PlayerDataEntity> findById(UUID id) {
        // TODO
        return Optional.empty();
    }
}
