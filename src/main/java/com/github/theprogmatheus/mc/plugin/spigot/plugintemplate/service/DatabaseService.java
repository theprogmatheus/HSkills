package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.service;

import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.core.AbstractService;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.database.DatabaseManager;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.Injector;
import com.zaxxer.hikari.HikariConfig;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
public class DatabaseService extends AbstractService {


    private final Injector injector;
    private DatabaseManager databaseManager;


    /**
     * Change how to load the database config
     */
    private HikariConfig loadDatabaseConfig() {
        var config = new HikariConfig();

        config.setJdbcUrl("jdbc:mariadb://localhost/databaseName");
        config.setUsername("username");
        config.setPassword("password");
        config.setMaximumPoolSize(10);

        return config;
    }

    @Override
    public void startup() {
        this.databaseManager = new DatabaseManager(this.injector, loadDatabaseConfig());
        try {
            this.databaseManager.openConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void shutdown() {
        try {
            this.databaseManager.closeConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
