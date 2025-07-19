package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.service;

import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.PluginTemplate;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.database.sql.DatabaseSQLManager;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.Injector;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.PluginService;
import com.zaxxer.hikari.HikariConfig;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
public class DatabaseSQLService extends PluginService {


    private final PluginTemplate plugin;
    private final Injector injector;
    private DatabaseSQLManager databaseManager;


    /**
     * Change how to load the database config
     */
    private HikariConfig loadDatabaseConfig() {
        var isTest = Boolean.parseBoolean(System.getProperty("BUKKIT_PLUGIN_DB_IN_TEST_MODE", "false"));
        if (isTest)
            return loadTestDatabaseConfig();

        this.plugin.getDataFolder().mkdirs();
        var storageFile = new File(this.plugin.getDataFolder(), "storage-h2");

        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:%s".formatted(storageFile.toPath().toAbsolutePath()));

        return config;
    }

    private HikariConfig loadTestDatabaseConfig() {
        var config = new HikariConfig();
        String dbName = UUID.randomUUID().toString();
        config.setJdbcUrl("jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1".formatted(dbName));
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
