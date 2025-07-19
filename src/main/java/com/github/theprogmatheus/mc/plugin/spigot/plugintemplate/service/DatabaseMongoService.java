package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.service;

import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.PluginService;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.database.mongo.DatabaseMongoManager;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.Injector;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
public class DatabaseMongoService extends PluginService {

    private final Injector injector;
    private DatabaseMongoManager databaseManager;
    private String connectionString;
    private String databaseName;


    /**
     * Change how to load the database config
     */
    private void loadDatabaseConfig() {
        this.connectionString = "mongodb://username:password@localhost";
        this.databaseName = "databaseName";
    }

    @Override
    public void startup() {
        loadDatabaseConfig();

        this.databaseManager = new DatabaseMongoManager(this.injector, this.connectionString, this.databaseName);
        this.databaseManager.openConnection();
    }

    @Override
    public void shutdown() {
        this.databaseManager.closeConnection();
    }
}
