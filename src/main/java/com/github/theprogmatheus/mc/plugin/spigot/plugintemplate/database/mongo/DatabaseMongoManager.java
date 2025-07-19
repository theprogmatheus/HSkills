package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.database.mongo;

import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.database.mongo.entity.PlayerData;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.database.mongo.repository.PlayerDataRepository;
import com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib.Injector;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DatabaseMongoManager {

    private final Injector injector;
    private final String connectionString;
    private final String databaseName;
    private MongoClient mongoClient;
    private Datastore dataStore;


    /**
     * Register your repositories here
     */
    private void registerRepositories() {
        registerRepository(PlayerDataRepository.class);
    }

    public void openConnection() {
        this.mongoClient = MongoClients.create(this.connectionString);
        this.dataStore = Morphia.createDatastore(this.mongoClient, this.databaseName);
        this.dataStore.getMapper().getEntityModel(PlayerData.class);
        this.injector.registerSingleton(Datastore.class, this.dataStore);

        registerRepositories();
    }


    public void closeConnection() {
        this.mongoClient.close();
    }

    private void registerRepository(Class<?> repositoryClass) {
        this.injector.getInstance(repositoryClass);
    }
}
