package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.config;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class ConfigurationManager {

    private final Logger logger;
    private final File dataFolder;

    private final Map<String, ConfigurationFile> configs = new ConcurrentHashMap<>();

    public ConfigurationFile register(String name) {
        return register(name, name);
    }

    public ConfigurationFile register(String name, String resourcePath) {
        var file = new File(dataFolder, name);

        var config = new ConfigurationFile(logger, file, resourcePath)
                .createIfNotExistsAndLoad();

        configs.put(name.toLowerCase(), config);
        return config;
    }

    public ConfigurationFile get(String name) {
        return configs.get(name.toLowerCase());
    }

    public void reload(String name) {
        var config = get(name);
        if (config != null)
            config.load();
    }

    public void reloadAll() {
        configs.values().forEach(ConfigurationFile::load);
    }

    public boolean isRegistered(String name) {
        return configs.containsKey(name.toLowerCase());
    }

}