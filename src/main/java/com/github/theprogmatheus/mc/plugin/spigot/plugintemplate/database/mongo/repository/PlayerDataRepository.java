package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.database.mongo.repository;


import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.database.mongo.entity.PlayerData;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Getter
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PlayerDataRepository {

    private final Datastore datastore;

    public Optional<PlayerData> findByName(String name) {
        if (name == null || name.isBlank())
            return Optional.empty();
        return Optional.ofNullable(this.datastore.find(PlayerData.class).filter(Filters.eq("name", name)).first());
    }

    public List<PlayerData> findAll() {
        return this.datastore.find(PlayerData.class).stream().toList();
    }
}
