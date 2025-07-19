package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.database.sql.repository;

import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.database.sql.entity.PlayerData;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;

@Getter
@Singleton
public class PlayerDataRepository {

    private final Dao<PlayerData, Long> playerDataDao;

    @Inject
    public PlayerDataRepository(ConnectionSource connectionSource) throws SQLException {
        this.playerDataDao = DaoManager.createDao(connectionSource, PlayerData.class);
    }

}
