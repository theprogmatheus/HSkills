package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.service;

import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.PluginTemplate;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.core.AbstractService;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.database.sql.DatabaseSQLManager;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.Injector;
import com.zaxxer.hikari.HikariConfig;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.sql.SQLException;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
public class DatabaseSQLService extends AbstractService {


    private final PluginTemplate plugin;
    private final Injector injector;
    private DatabaseSQLManager databaseManager;


    /**
     * Change how to load the database config
     */
    private HikariConfig loadDatabaseConfig() {

        this.plugin.getDataFolder().mkdirs();
        var storageFile = new File(this.plugin.getDataFolder(), "storage.db");

        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:%s".formatted(storageFile.toPath().toAbsolutePath()));

        return config;
    }

    @Override
    public void startup() {
        this.databaseManager = new DatabaseSQLManager(this.injector, loadDatabaseConfig());
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
