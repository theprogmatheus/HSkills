package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.database.repository;

import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.database.entity.PlayerData;

import java.util.Optional;
import java.util.UUID;

public interface PlayerDataRepository {

    Optional<PlayerData> findById(UUID id);

}
