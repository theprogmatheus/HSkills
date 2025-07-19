package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.database.sql;

import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.database.sql.repository.PlayerDataRepository;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.Injector;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@Getter
@RequiredArgsConstructor
public class DatabaseSQLManager {

    private final Injector injector;
    private final HikariConfig databaseConfig;

    private HikariDataSource dataSource;
    private ConnectionSource connectionSource;


    /**
     * Register your repositories here
     */
    private void registerRepositories() {
        registerRepository(PlayerDataRepository.class);
    }


    public void openConnection() throws SQLException {
        this.dataSource = new HikariDataSource(this.databaseConfig);
        this.connectionSource = new DataSourceConnectionSource(this.dataSource, this.databaseConfig.getJdbcUrl());
        this.injector.registerSingleton(ConnectionSource.class, this.connectionSource);

        registerRepositories();
    }

    public void closeConnection() throws Exception {
        this.connectionSource.close();
        this.dataSource.close();
    }

    private void registerRepository(Class<?> repositoryClass) {
        this.injector.getInstance(repositoryClass);
    }

}
